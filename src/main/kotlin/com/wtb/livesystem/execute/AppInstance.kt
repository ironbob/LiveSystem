package com.wtb.livesystem.execute

import com.wtb.livesystem.config.app.model.App
import com.wtb.livesystem.core.*
import com.wtb.livesystem.core.rule.RuleEvaluator
import com.wtb.livesystem.core.rule.RuleFieldName
import com.wtb.livesystem.core.rule.RuleParser
import com.wtb.livesystem.core.streamer.LiveStreamer
import com.wtb.livesystem.core.streamer.StreamerConfig
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.LocalDateTime
import java.util.*


class AppInstance(private val app: App) {
    private var running = false
    private var startTime: LocalDateTime? = null

    private val roomStatus: RoomStatus = RoomStatus(0, hashSetOf(), arrayListOf())
    val streamer: LiveStreamer
    init {
           val liveConfigDto = (parseLiveConfigZip(app.liveConfig!!))
        val parseScripts = liveConfigDto.scripts.map {
            ParsedScript(it)
        }
        val streamerConfig = StreamerConfig(parseScripts,liveConfigDto.warmConfig,liveConfigDto.rhythmConfig
        ,liveConfigDto.catchphrases)
        streamer = LiveStreamer(streamerConfig)
    }

    private val processedActions = arrayListOf<ProcessAction>()


    data class ProcessAction(
        val replyContent: SpeechReply,
        val timeStamp: Long,
        val processEvents: List<LiveEvent>,
        val actionType: ActionType
    )

    /**
     * 读稿、暖场、回复
     */
    enum class ActionType {
        ReadDraft, Warmup, ReplyAudience
    }

    data class Draft(val sentences: List<String>, var warmups: List<String>)

    class DraftPlayState(var currentDraft: Draft)


    private val eventListLock = Any()

    /**
     * 新的用户消息
     */
    fun onNewLiveEvent(liveEvent: LiveEvent) {
        synchronized(eventListLock) {
            roomStatus.pendingEvents.add(liveEvent)
        }
    }


    /**
     * 更新房间状态
     */
    fun onNewRoomStatus(status: RoomStatusUpdateMessage) {
        roomStatus.onlineUserCount = status.onlineUserCount;
        roomStatus.activeUserIds = status.activeUserIds
    }

    private fun getRuleStates(): Map<String, Int> {
        return hashMapOf(
            RuleFieldName.FieldCount.value to roomStatus.onlineUserCount,
            RuleFieldName.FieldLiveDuration.value to getRunningDurationMinutes().toInt()
        )
    }



    fun fetchNextSpeechReply(): SpeechReply?{
        return streamer.fetchNextSpeechReply(getRuleStates())
    }

    init {

        this.startTime = LocalDateTime.now()
    }

    fun getApp(): App {
        return app
    }

    fun getStartTime(): LocalDateTime? {
        return startTime
    }

    fun getRunningDuration(): String {
        val duration: Duration = Duration.between(startTime, LocalDateTime.now())
        val hours: Long = duration.toHours()
        val minutes: Int = duration.toMinutesPart()
        val seconds: Int = duration.toSecondsPart()
        return String.format("%d小时 %d分钟 %d秒", hours, minutes, seconds)
    }

    fun getRunningDurationMinutes(): Long {
        val duration: Duration = Duration.between(startTime, LocalDateTime.now())
        val minutes = duration.toMinutes()
        return minutes
    }


    val logger = LoggerFactory.getLogger(AppInstance::class.java)
    fun start() {
        val zip = app.liveConfig
        running = true
        logger.info("应用 ${app.name} 已启动")
    }

    fun stop() {
        running = false
        logger.info("应用 ${app.name} 已停止")
    }

    fun getNextSpeech(): SpeechReply? {
        return if (running) {
            streamer.fetchNextSpeechReply(getRuleStates())
        } else {
            null
        }
    }
}

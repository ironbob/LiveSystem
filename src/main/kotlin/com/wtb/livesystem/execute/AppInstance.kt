package com.wtb.livesystem.execute

import com.wtb.livesystem.config.app.model.App
import com.wtb.livesystem.config.auth.SecurityConfig
import com.wtb.livesystem.core.*
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.LocalDateTime
import java.util.*


class AppInstance(private val app: App) {
    private var running = false
    private val speechQueue: Queue<String> = LinkedList()
    private var startTime: LocalDateTime? = null

    private val roomStatus: RoomStatus = RoomStatus(0, hashSetOf(), arrayListOf())
    private val streamerState: StreamerState = StreamerState(null, null, 0)

    private val processedActions = arrayListOf<ProcessAction>()


    data class ProcessAction(val replyContent: SpeechReply, val timeStamp: Long, val processEvents: List<LiveEvent>, val actionType:ActionType)

    /**
     * 读稿、暖场、回复
     */
    enum class ActionType{
        ReadDraft,Warmup,ReplyAudience
    }

    data class Draft(val sentences: List<String>, var warmups:List<String>, )

    class DraftPlayState(var currentDraft:Draft)


    private val eventListLock = Any()
    /**
     * 新的用户消息
     */
    fun onNewLiveEvent(liveEvent: LiveEvent){
        synchronized(eventListLock) {
            roomStatus.pendingEvents.add(liveEvent)
        }
    }

    /**
     * 更新房间状态
     */
    fun onNewRoomStatus(status: RoomStatusUpdateMessage){
        roomStatus.onlineUserCount = status.onlineUserCount;
        roomStatus.activeUserIds = status.activeUserIds
    }

    /**
     *
     */
    fun fetchNextSpeechReply():SpeechReply?{
        return null

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

    init {
        speechQueue.add("你好，我是你的虚拟主播！")
        speechQueue.add("接下来，我们继续进行直播...")
        speechQueue.add("欢迎新的观众加入直播间！")
    }

    val logger = LoggerFactory.getLogger(AppInstance::class.java)
    fun start() {
        running = true
        logger.info("应用 ${app.name} 已启动")
    }

    fun stop() {
        running = false
        logger.info("应用 ${app.name} 已停止")
    }

    fun getNextSpeech(): String {
        return if (running) {
            speechQueue.poll() ?: "主播暂时没有新的话要说"
        } else {
            "应用未运行"
        }
    }
}

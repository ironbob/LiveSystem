package com.wtb.livesystem.core.streamer

import com.wtb.livesystem.core.*
import com.wtb.livesystem.core.rule.RuleEvaluator
import com.wtb.livesystem.core.rule.RuleParser
import org.slf4j.LoggerFactory

data class StreamerConfig(
    val parsedScripts: List<ParsedScript>,
    val warmupConfig: WarmupConfig,
    val rhythmConfig: RhythmConfig?,
    val catchphrases: MutableList<String>
)

fun List<SpeechReply>.formatted():List<String>{
    return this.map{
        it.getFormatedContent();
    }
}

class LiveStreamer(val streamerConfig: StreamerConfig) {
    val currentEmotion: Emotion? = null
    val lastSpokenAt: Long = 0

    private val logger = LoggerFactory.getLogger(LiveStreamer::class.java)
    var curScript: ParsedScript? = null
        set(value) {
            field = value
            if (value != null) {
                scriptState = ReadScriptState(value)
            }
        }

    fun updateScriptState(script: ParsedScript?) {
        curScript = script
    }

    fun hasScript() = curScript != null
    private var scriptState: ReadScriptState? = null
    fun getNextScriptSentence(): String? {
        scriptState?.also {
            return it.getNextSentence()
        } ?: run {
            logger.error("没有合适的稿子")
            return null
        }
        return null
    }

    public fun chooseScriptByRule(ruleStates: Map<String, Int>) {
        val script = streamerConfig.parsedScripts.firstOrNull {
            val rules = it.parsedRules
            rules.all {
                RuleEvaluator.evaluate(it, ruleStates)
            }
        }
        if (script != null) {
            if (curScript != script) {
                updateScriptState(script)
                logger.info("切换稿子")
            }
        }
        if (!hasScript()) {
            updateScriptState(streamerConfig.parsedScripts.firstOrNull())
        }
    }

    private fun makeNormalSpeechReply(content: String): SpeechReply {
        val msg = ContentReplyMsg(content)
        return SpeechReply(arrayListOf(msg))
    }

    private val historyList = arrayListOf<SpeechReply>()
    private fun addToHistory(reply: SpeechReply) {
        historyList.add(reply)
    }

    fun getHistory(): List<SpeechReply>  = historyList

    /**
     *
     */
    fun fetchNextSpeechReply(ruleStates: Map<String, Int>): SpeechReply? {
        chooseScriptByRule(ruleStates)
        getNextScriptSentence()?.let {
            return makeNormalSpeechReply(it).also { it ->
                addToHistory(it)
            }
        }
        logger.error("没有找到合适的话")
        return null

    }

}
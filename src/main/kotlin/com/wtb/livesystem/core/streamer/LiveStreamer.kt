package com.wtb.livesystem.core.streamer

import com.wtb.livesystem.core.Emotion
import com.wtb.livesystem.core.ParsedScript
import com.wtb.livesystem.core.ReadScriptState
import com.wtb.livesystem.core.Script
import org.slf4j.LoggerFactory

class LiveStreamer {
    val currentEmotion: Emotion? = null
    val lastSpokenAt: Long = 0

    private val logger = LoggerFactory.getLogger(LiveStreamer::class.java)
    var curScript: Script? = null
        set(value) {
            field = value
            if (value != null) {
                scriptState = ReadScriptState(ParsedScript(value))
            }
        }

    fun updateScriptState(script: Script?) {
        curScript = script
    }

    fun hasScript() = curScript != null
    private var scriptState: ReadScriptState? = null
    fun getNextSentence(): String? {
        scriptState?.also {
            return it.getNextSentence()
        } ?: run {
            logger.error("没有合适的稿子")
            return null
        }
        return null
    }

}
package com.wtb.livesystem.core

sealed class ReplyMsg {
    data class ContentReplyMsg(val content: String)

    data class BreathReplyMsg(val breathDuration: Int)//气声

    data class BreakReplyMsg(val breakDuration: Int)//停顿
}

data class SpeechReply(val messages: List<ReplyMsg>)
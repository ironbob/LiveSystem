package com.wtb.livesystem.core

sealed class ReplyMsg {

}
data class ContentReplyMsg(val content: String):ReplyMsg()

data class BreathReplyMsg(val breathDuration: Int):ReplyMsg()//气声

data class BreakReplyMsg(val breakDuration: Int):ReplyMsg()//停顿
data class SpeechReply(val messages: List<ReplyMsg>)
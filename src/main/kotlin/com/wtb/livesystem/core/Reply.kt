package com.wtb.livesystem.core

sealed class ReplyMsg {

}
data class ContentReplyMsg(val content: String):ReplyMsg()

data class BreathReplyMsg(val breathDuration: Int):ReplyMsg()//气声

data class BreakReplyMsg(val breakDuration: Int):ReplyMsg()//停顿

val emptyReply = SpeechReply(emptyList())
data class SpeechReply(val messages: List<ReplyMsg>){
    fun getFormatedContent(): String {
        return messages.joinToString(separator = "") { msg ->
            when (msg) {
                is ContentReplyMsg -> msg.content
                is BreathReplyMsg -> "[气声:${msg.breathDuration}s]"
                is BreakReplyMsg -> "[停顿:${msg.breakDuration}s]"
            }
        }
    }
}
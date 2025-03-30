package com.wtb.livesystem.streamer

import java.util.*


data class LiveRoomState(
    @JvmField var audience: Int = 0,
    @JvmField var activateSpeech: SpeechType = SpeechType.Nobody,
    @JvmField val ordersList:ArrayList<Order> = arrayListOf(),
    @JvmField val pendingQuestions:ArrayList<Question> = arrayListOf()
)

enum class SpeechType(val draftName:String){
    Nobody("无人"),
    Few("少人"),
    Medium("中等"),
    Many("较多"),
    Crowd("拥挤"),
    Super("超多"),
    Mega("巨多"),

}

data class Order(
    var amount: Double,
    var type: String
)


data class Question(
    var text: String,
    var askTimeStamp: Long
)

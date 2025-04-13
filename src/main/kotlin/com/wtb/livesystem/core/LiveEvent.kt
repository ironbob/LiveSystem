package com.wtb.livesystem.core

enum class LiveEventType(val t:Int, val eventName:String){
    UserFollow(1,"关注"),
    UserEnter(2,"进入直播间"),
    UserMessage(3,"用户提问"),
    UserGift(4,"用户送礼物")
}
sealed class LiveEvent {
    abstract val timestamp: Long

    data class UserFollow(
        val userId: String,
        val username: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : LiveEvent()

    data class UserEnter(
        val userId: String,
        val username: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : LiveEvent()

    data class UserMessage(
        val userId: String,
        val username: String,
        val message: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : LiveEvent()

    data class UserGift(
        val userId: String,
        val username: String,
        val giftName: String,
        val giftCount: Int,
        override val timestamp: Long = System.currentTimeMillis()
    ) : LiveEvent()
}

data class RoomStatus(
    var onlineUserCount: Int,
    var activeUserIds: Set<String>,
    val pendingEvents: ArrayList<LiveEvent>, //
)


data class RoomStatusUpdateMessage(val onlineUserCount: Int, val activeUserIds: Set<String>)




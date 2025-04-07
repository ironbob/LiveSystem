package com.wtb.livesystem.core

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
data class StreamerState(
    val currentEmotion: Emotion?,
    val currentScript: Script?, // 当前绑定的话术稿（你之前的 Script 类）
    val lastSpokenAt: Long
)



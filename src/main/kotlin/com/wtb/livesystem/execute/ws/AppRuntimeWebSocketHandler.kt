package com.wtb.livesystem.execute.ws

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.AbstractWebSocketHandler
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap


@Component
class AppRuntimeWebSocketHandler : AbstractWebSocketHandler() {
    // Map<appId, Set<session>>
    private val logger = LoggerFactory.getLogger("AppRuntimeWebSocketHandler")
    private val sessionMap: MutableMap<Long, MutableSet<WebSocketSession>> = ConcurrentHashMap()


    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val appId = getAppId(session)
        val sessions = sessionMap[appId]
        if (sessions != null) {
            sessions.remove(session)
            if (sessions.isEmpty()) {
                sessionMap.remove(appId)
            }
        }
    }

    fun broadcast(appId: Long, message: String) {
        val sessions: Set<WebSocketSession>? = sessionMap[appId]
        if (sessions != null) {
            for (session in sessions) {
                try {
                    session.sendMessage(TextMessage(message))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getAppId(session: WebSocketSession): Long {
        try {
            return (session.attributes["appId"] as String).toLong()
        }catch (e:Exception){
            e.printStackTrace()
           return -1
        }
    }


    @Throws(Exception::class)
    override fun afterConnectionEstablished(session: WebSocketSession) {
        val appId = getAppId(session)
        if(appId>=0){
            logger.info("WebSocket session established for appId: $appId")
            if (session.uri != null) {
                session.attributes["appId"] = appId
            }
            super.afterConnectionEstablished(session)
        }

    }
}


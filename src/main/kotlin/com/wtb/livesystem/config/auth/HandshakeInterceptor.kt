package com.wtb.livesystem.config.auth

import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor


class HandshakeInterceptor : HttpSessionHandshakeInterceptor() {
    override fun beforeHandshake(
        request: ServerHttpRequest, response: ServerHttpResponse,
        wsHandler: WebSocketHandler, attributes: MutableMap<String?, Any?>
    ): Boolean {
        if (request is ServletServerHttpRequest) {
            val servletRequest = request as ServletServerHttpRequest
            val appId = servletRequest.servletRequest.getParameter("appId")
            attributes["appId"] = appId // 存储到 WebSocket Session 属性中
        }
        return super.beforeHandshake(request, response, wsHandler, attributes)
    }
}

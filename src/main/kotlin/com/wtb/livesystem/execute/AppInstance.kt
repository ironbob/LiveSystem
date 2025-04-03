package com.wtb.livesystem.execute

import java.util.*

class AppInstance(private val appId: Long) {
    private var running = false
    private val speechQueue: Queue<String> = LinkedList()

    init {
        speechQueue.add("你好，我是你的虚拟主播！")
        speechQueue.add("接下来，我们继续进行直播...")
        speechQueue.add("欢迎新的观众加入直播间！")
    }

    fun start() {
        running = true
        println("应用 $appId 已启动")
    }

    fun stop() {
        running = false
        println("应用 $appId 已停止")
    }

    fun getNextSpeech(): String {
        return if (running) {
            speechQueue.poll() ?: "主播暂时没有新的话要说"
        } else {
            "应用未运行"
        }
    }
}

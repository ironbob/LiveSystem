package com.wtb.livesystem.execute

import com.wtb.livesystem.core.SpeechReply
import com.wtb.livesystem.core.emptyReply
import com.wtb.livesystem.core.streamer.formatted
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/running")
class RunningController(private val appExecutionService: AppExecutionService) {

    val logger = LoggerFactory.getLogger(RunningController::class.java)

    /**
     * 拉取当前正在讲解的脚本（explanations）
     */
    @GetMapping("{appId}/script")
    fun fetchCurrentScript(@PathVariable appId: Long): List<String> {
        val instance = appExecutionService.queryById(appId) ?: return emptyList()
        val streamer = instance.streamer

        val curScript = streamer.curScript
        return curScript?.explanations ?: listOf("当前没有可用的脚本")
    }

    /**
     * 拉取下一句主播要说的话
     */
    @PostMapping("{appId}/nextSpeech")
    fun fetchNextSpeech(@PathVariable appId: Long): SpeechReply {
        val instance = appExecutionService.queryById(appId) ?: return emptyReply

        val speechReply = instance.fetchNextSpeechReply() // 这里传你需要的 ruleStates


        return speechReply?:emptyReply
    }

    /**
     * 获取主播历史说过的话（历史记录）
     */
    @GetMapping("{appId}/history")
    fun fetchSpeechHistory(@PathVariable appId: Long): List<String> {
        val instance = appExecutionService.queryById(appId) ?: return emptyList()
        return instance.streamer.getHistory().formatted()
    }
}


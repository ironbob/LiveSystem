package com.wtb.livesystem.execute

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/running")
class RunningController(private val appExecutionService: AppExecutionService) {
val logger = LoggerFactory.getLogger("RunningController")
     /**
      *  运行状态
     */
     @PostMapping("{appId}/nextSpeech")
     @ResponseBody
     fun fetchNextSpeech(@PathVariable appId: Long): String {
         val instance = appExecutionService.queryById(appId) ?: return "未找到应用"

         val speechReply = instance.fetchNextSpeechReply() // 这里传你需要的 ruleStates

         return speechReply?.messages?.joinToString("\n") { it.toString()} ?: "没有更多内容了"
     }

    @GetMapping("{appId}/script")
    @ResponseBody
    fun fetchCurrentScript(@PathVariable appId: Long): List<String> {
        val instance = appExecutionService.queryById(appId) ?: return emptyList()
        val streamer = instance.streamer

        val curScript = streamer.curScript
        return curScript?.explanations ?: listOf("当前没有可用的脚本")
    }
}

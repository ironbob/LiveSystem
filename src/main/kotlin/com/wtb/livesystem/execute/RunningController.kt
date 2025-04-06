package com.wtb.livesystem.execute

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Controller
@RequestMapping("/running")
class RunningController(private val appExecutionService: AppExecutionService) {
val logger = LoggerFactory.getLogger("RunningController")
     /**
      *  运行状态
     */
    @GetMapping("{appId}/state")
    fun getNextSpeech(@PathVariable appId: Long,
                      model: Model): String {
         val instance = appExecutionService.queryById(appId) ?: return "redirect:/apps"
         logger.info("instance : $instance")
        model.addAttribute("appInstance", instance)
        return "apps/running";
    }
}

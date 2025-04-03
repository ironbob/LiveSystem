package com.wtb.livesystem.execute

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/running")
class RunningController(private val appExecutionService: AppExecutionService) {

    /**
     * 获取主播的下一句台词
     */
    @GetMapping("/next/{appId}")
    fun getNextSpeech(@PathVariable appId: Long): ResponseEntity<String> {
        val speech = appExecutionService.getNextSpeech(appId)
        return if (speech != null) {
            ResponseEntity.ok(speech)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("应用 $appId 未运行或无台词")
        }
    }
}

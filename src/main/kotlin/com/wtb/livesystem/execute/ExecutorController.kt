package com.wtb.livesystem.execute

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes


@Controller
@RequestMapping("/executor")
class ExecutorController(private val appExecutionService: AppExecutionService) {

    @PostMapping("/start/{appId}")
    fun startApp(@PathVariable appId: Long, redirectAttributes: RedirectAttributes): String {
        if (appExecutionService.startApp(appId)) {
            redirectAttributes.addFlashAttribute("success", "应用 $appId 已成功启动")
        } else {
            redirectAttributes.addFlashAttribute("error", "应用 $appId 启动失败或已在运行")
        }
        return "redirect:/apps"
    }

    @PostMapping("/stop/{appId}")
    fun stopApp(@PathVariable appId: Long, redirectAttributes: RedirectAttributes): String {
        if (appExecutionService.stopApp(appId)) {
            redirectAttributes.addFlashAttribute("success", "应用 $appId 已成功停止")
        } else {
            redirectAttributes.addFlashAttribute("error", "应用 $appId 停止失败或未运行")
        }
        return "redirect:/apps"
    }
}





package com.wtb.livesystem.config.app

import com.wtb.livesystem.execute.AppExecutionService
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.io.InputStream
import java.security.Principal


@Controller
@RequestMapping("/apps")
class AppController(
    private val appService: AppService,
    private val streamerConfigService: StreamerConfigService,
    private val appExecutionService: AppExecutionService,
    private val userService: UserService
) {

    // 显示用户的所有App
    @GetMapping
    fun listApps(model: Model, principal: Principal): String {
        val username = principal.name
        model.addAttribute("apps", appService.findByUsername(username))
        model.addAttribute("currentUser", userService.findByUsername(username))
        model.addAttribute("runningApps", appExecutionService.queryRunningAppMap())
        return "apps/apps"
    }


       // 显示单个App详情
    @GetMapping("/{appId}")
    fun showApp(
        @PathVariable appId: Long,
        model: Model,
        principal: Principal
    ): String {
        validateOwnership(appId, principal.name)
        model.addAttribute("app", appService.findById(appId))
        return "apps/show"
    }

    @PostMapping("/{appId}/delete")
    fun deleteApp(
        @PathVariable appId: Long,
        principal: Principal,
        redirectAttributes: RedirectAttributes
    ): String {
        validateOwnership(appId, principal.name)
        appService.deleteApp(appId)
        redirectAttributes.addFlashAttribute("success", "App deleted successfully")
        return "redirect:/apps"
    }


    // 下载配置文件
    @GetMapping("/{appId}/configs/{configId}/download")
    fun downloadConfig(
        @PathVariable appId: Long,
        @PathVariable configId: Long,
        principal: Principal
    ): ResponseEntity<Resource> {
        validateOwnership(appId, principal.name)
        val config = streamerConfigService.findById(configId)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${config.configName}.json\"")
            .contentType(MediaType.APPLICATION_JSON)
            .body(config.jsonConfig.byteInputStream().toResource())
    }

    // 验证用户对App的所有权
    private fun validateOwnership(appId: Long, username: String) {
        if (!appService.validateUserOwnership(appId, username)) {
            throw IllegalArgumentException("You don't have permission to access this resource")
        }
    }
}

// 扩展函数：将InputStream转换为Resource
fun InputStream.toResource(): Resource {
    return object : InputStreamResource(this) {
        override fun getFilename(): String? = null
    }
}


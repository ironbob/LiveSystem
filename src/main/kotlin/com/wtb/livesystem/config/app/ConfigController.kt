package com.wtb.livesystem.config.app
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.nio.charset.StandardCharsets
import java.security.Principal

@Controller
@RequestMapping("/apps/{appId}/configs")
class ConfigController(
    private val appService: AppService
) {

    @GetMapping
    fun configPage(@PathVariable appId: Long, model: Model, principal: Principal): String {
        val app = appService.findById(appId)
        app.initializeLiveConfig() // 确保 LiveConfig 存在
        appService.save(app)

        model.addAttribute("app", app)
        model.addAttribute("liveConfig", app.liveConfig)
        return "apps/config"
    }

    // 保存口头禅和直播话术稿
    @PostMapping("/save")
    fun saveConfig(
        @PathVariable appId: Long,
        @RequestParam catchphrases: String,
        @RequestParam scripts: String,
        principal: Principal,
        redirectAttributes: RedirectAttributes
    ): String {
        val app = appService.findById(appId)
        app.initializeLiveConfig()
        app.liveConfig?.let {
            it.catchphrases = catchphrases.split("\n").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
            it.scripts = scripts.split("\n").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
            appService.save(app)
        }
        redirectAttributes.addFlashAttribute("success", "配置已保存")
        return "redirect:/apps/$appId/configs"
    }

    // 上传 JSON 配置文件
    @PostMapping("/upload-json")
    fun uploadJsonConfig(
        @PathVariable appId: Long,
        @RequestParam("file") file: MultipartFile,
        principal: Principal,
        redirectAttributes: RedirectAttributes
    ): String {
        val app = appService.findById(appId)
        val jsonContent = file.bytes.toString(StandardCharsets.UTF_8)
        app.liveConfig?.playbackRhythmJson = jsonContent
        appService.save(app)

        redirectAttributes.addFlashAttribute("success", "JSON 配置上传成功")
        return "redirect:/apps/$appId/configs"
    }

    // 验证用户是否有权限访问
    private fun validateOwnership(appId: Long, username: String) {
        if (!appService.validateUserOwnership(appId, username)) {
            throw IllegalArgumentException("You don't have permission to access this resource")
        }
    }
}




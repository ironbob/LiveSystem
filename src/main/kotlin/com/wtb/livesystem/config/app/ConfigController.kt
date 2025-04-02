package com.wtb.livesystem.config.app
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.nio.charset.StandardCharsets
import java.security.Principal
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.io.File
import java.io.IOException

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

        val resource = ClassPathResource("static/catchphrases.txt")
        val exampleCatchphrases = resource.inputStream.bufferedReader().readLines()

        model.addAttribute("app", app)
        model.addAttribute("liveConfig", app.liveConfig)
        model.addAttribute("exampleCatchphrases", exampleCatchphrases) // 传递示例口头禅列表
        return "apps/config"
    }

    @PostMapping("/save")
    fun saveConfig(
        @PathVariable appId: Long,
        @RequestParam catchphrases: List<String>,
        @RequestParam scriptNames: List<String>,
        @RequestParam explanation: List<String>,
        @RequestParam warmUpContent: List<String>,
        @RequestParam triggers: List<String>,
        principal: Principal,
        redirectAttributes: RedirectAttributes
    ): String {
        val app = appService.findById(appId)
        app.initializeLiveConfig()
        app.liveConfig?.let {
            it.catchphrases = catchphrases.map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()

            // Update scripts with new data
            it.scripts = scriptNames.mapIndexed { index, name ->
                Script(
                    name = name,
                    explanation = explanation.getOrElse(index) { "" },
                    warmUpContent = warmUpContent.getOrElse(index) { "" },
                    triggers = triggers.getOrElse(index) { "" }.split(",").map { it.trim() }.toMutableList()
                )
            }.toMutableList()

            appService.save(app)
        }
        redirectAttributes.addFlashAttribute("success", "配置已保存")
        return "redirect:/apps/$appId/configs"
    }


    @GetMapping("/download_sample_json")
    fun downloadExampleJson(): ResponseEntity<Resource> {
        return try {
            val resource = ClassPathResource("static/streamer/reader.json")
            if (!resource.exists()) {
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
            } else {
                val headers = HttpHeaders()
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reader.json\"")
                ResponseEntity.ok()
                    .headers(headers)
                    .body(resource)
            }
        } catch (e: IOException) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
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




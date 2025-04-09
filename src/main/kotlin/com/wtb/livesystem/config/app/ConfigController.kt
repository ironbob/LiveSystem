package com.wtb.livesystem.config.app
import com.fasterxml.jackson.databind.ObjectMapper
import com.wtb.livesystem.config.app.rule.RuleParser
import com.wtb.livesystem.core.RhythmConfig
import com.wtb.livesystem.core.Script
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.security.Principal
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.io.IOException
import java.nio.file.Paths

@Controller
@RequestMapping("/apps/{appId}/configs")
class ConfigController(
    private val appService: AppService,
    private val fileStorageService: FileStorageService
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
        model.addAttribute("liveConfigForm",LiveConfigForm(app.liveConfig?.catchphrases ?: mutableListOf(),app.liveConfig?.scripts?.map {
            ScriptForm(it.name, it.explanation, it.warmUpContent, it.ruleString)
        } ?: mutableListOf()))
        return "apps/config"
    }



    @PostMapping("/save")
    fun saveConfig(
        @PathVariable appId: Long,
        @ModelAttribute form: LiveConfigForm,
        principal: Principal,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        val app = appService.findById(appId)
        app.initializeLiveConfig()

        val scripts = mutableListOf<Script>()

        for (scriptForm in form.scripts) {
            val error = RuleParser.validate(scriptForm.rules)
            if (error != null) {
                model.addAttribute("error", "规则【${scriptForm.name}】不合法: $error")
                model.addAttribute("app", app)
                val resource = ClassPathResource("static/catchphrases.txt")
                val exampleCatchphrases = resource.inputStream.bufferedReader().readLines()
                model.addAttribute("liveConfigForm", form) // 回传用户填写的表单
                model.addAttribute("exampleCatchphrases", exampleCatchphrases) // 传递示例口头禅列表
                return "apps/config" // 这里返回你的 Thymeleaf 页面名，如 config.html
            }

            val parsedRules = RuleParser.parse(scriptForm.rules)
                .flatMap { it.rules }

            scripts.add(
                Script(
                    name = scriptForm.name,
                    explanation = scriptForm.explanation,
                    warmUpContent = scriptForm.warmUpContent,
                    ruleString = scriptForm.rules
                )
            )
        }

        app.liveConfig?.let {
            it.catchphrases = form.catchphrases.toMutableList()
            it.scripts = scripts
            appService.save(app)
        }

        redirectAttributes.addFlashAttribute("success", "配置已保存")
        return "redirect:/apps/$appId/configs"
    }
    private fun parseRules(ruleLines: String): MutableList<String> {
        return ruleLines.lines()                      // 按行分割
            .map { it.trim() }                        // 去除前后空格
            .filter { it.isNotEmpty() }               // 过滤空行
            .toMutableList()                          // 转成 MutableList
    }

    data class LiveConfigForm(
        val catchphrases: List<String> = emptyList(),
        val scripts: List<ScriptForm> = emptyList()
    )

    data class ScriptForm(
        val name: String = "",
        val explanation: String = "",
        val warmUpContent: String = "",
        val rules: String = "" // 存储JSON字符串
    )

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
        var storePath = fileStorageService.storeFile(file)
        app.liveConfig?.rhythmConfig = loadFileContent(storePath)
        appService.save(app)

        redirectAttributes.addFlashAttribute("success", "JSON 配置上传成功")
        return "redirect:/apps/$appId/configs"
    }
    fun loadFileContent(path: String): RhythmConfig {
        return ObjectMapper().readValue(Paths.get(path).toFile(), RhythmConfig::class.java)
    }
    // 验证用户是否有权限访问
    private fun validateOwnership(appId: Long, username: String) {
        if (!appService.validateUserOwnership(appId, username)) {
            throw IllegalArgumentException("You don't have permission to access this resource")
        }
    }
}




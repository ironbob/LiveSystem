package com.wtb.livesystem.config.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.wtb.livesystem.core.RhythmConfig
import com.wtb.livesystem.core.Script
import com.wtb.livesystem.core.rule.RuleParser
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.security.Principal
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Paths
import java.util.zip.ZipInputStream

@Controller
@RequestMapping("/apps/{appId}/configs")
class ConfigController(
    private val appService: AppService,
    private val fileStorageService: FileStorageService
) {

    @GetMapping
    fun showConfigPage(@PathVariable appId: Long, model: Model, principal: Principal): String {
        val app = appService.findById(appId)
        app.initializeLiveConfig()
        appService.save(app)

        model.addAttribute("app", app)
        model.addAttribute("liveConfig", app.liveConfig)

        val zipPath = app.liveConfig?.zipPath
        if (!zipPath.isNullOrBlank()) {
            val zipFile = File(zipPath)
            if (zipFile.exists()) {
                val zipContents = mutableListOf<String>()
                ZipInputStream(FileInputStream(zipFile)).use { zipStream ->
                    var entry = zipStream.nextEntry
                    while (entry != null) {
                        if (!entry.isDirectory) {
                            zipContents.add(entry.name)
                        }
                        entry = zipStream.nextEntry
                    }
                }
                model.addAttribute("zipFileName", zipFile.name)
                model.addAttribute("zipContents", zipContents)
            }
        }

        return "apps/config_zip"
    }

    private val expectedFiles = setOf(
        "rhythmConfig.json",
        "catchphrases.txt",
        "warmup.txt"
    )


    private val scriptFilePattern = Regex("""script_.*\.(ini|txt)""")
    @PostMapping("/upload")
    fun uploadZipFile(
        @PathVariable appId: Long,
        @RequestParam("zipFile") zipFile: MultipartFile,
        model: Model
    ): String {
        val zipEntries = mutableSetOf<String>()

        ZipInputStream(zipFile.inputStream).use { zipStream ->
            var entry = zipStream.nextEntry
            while (entry != null) {
                if (!entry.isDirectory) {
                    zipEntries.add(entry.name)
                }
                entry = zipStream.nextEntry
            }
        }

        val missingRequired = expectedFiles.filter { it !in zipEntries }
        val hasScript = zipEntries.any { it.matches(scriptFilePattern) }

        if (missingRequired.isNotEmpty() || !hasScript) {
            val msg = buildString {
                if (missingRequired.isNotEmpty()) {
                    append("缺少必要文件: ${missingRequired.joinToString("、")}。")
                }
                if (!hasScript) {
                    append("至少需要一个脚本文件（文件名以 script_ 开头）。")
                }
            }
            val app = appService.findById(appId)
            model.addAttribute("app", app)
            model.addAttribute("error", msg)
            return "apps/config_zip"
        }

        // 文件校验通过，可以将 zip 保存路径绑定到 LiveConfig
        val app = appService.findById(appId)
        val zipFilePath = fileStorageService.storeConfigZip(appId,zipFile)
        app.initializeLiveConfig()
        app.liveConfig?.zipPath = zipFilePath
        appService.save(app)

        return "redirect:/apps/$appId/configs"
    }

    @GetMapping("/download")
    fun downloadZip(@PathVariable appId: Long, response: HttpServletResponse): ResponseEntity<Any> {
        val app = appService.findById(appId)
        val zipPath = app.liveConfig?.zipPath

        if (zipPath.isNullOrBlank()) {
            return ResponseEntity.notFound().build()
        }

        val file = File(zipPath)
        if (!file.exists()) {
            return ResponseEntity.notFound().build()
        }

        val resource = InputStreamResource(FileInputStream(file))

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"config.zip\"")
            .contentLength(file.length())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource)
    }

//    @PostMapping("/save")
//    fun saveConfig(
//        @PathVariable appId: Long,
//        @ModelAttribute form: LiveConfigForm,
//        principal: Principal,
//        model: Model,
//        redirectAttributes: RedirectAttributes
//    ): String {
//        val app = appService.findById(appId)
//        app.initializeLiveConfig()
//
//        val scripts = mutableListOf<Script>()
//
//        for (scriptForm in form.scripts) {
//            val error = RuleParser.validate(scriptForm.rules)
//            if (error != null) {
//                model.addAttribute("error", "规则【${scriptForm.name}】不合法: $error")
//                model.addAttribute("app", app)
//                val resource = ClassPathResource("static/catchphrases.txt")
//                val exampleCatchphrases = resource.inputStream.bufferedReader().readLines()
//                model.addAttribute("liveConfigForm", form) // 回传用户填写的表单
//                model.addAttribute("exampleCatchphrases", exampleCatchphrases) // 传递示例口头禅列表
//                return "apps/config" // 这里返回你的 Thymeleaf 页面名，如 config.html
//            }
//
//            scripts.add(
//                Script(
//                    name = scriptForm.name,
//                    explanation = scriptForm.explanation,
//                )
//            )
//        }
//
//        app.liveConfig?.let {
//            it.catchphrases = form.catchphrases.toMutableList()
//            it.scripts = scripts
//            appService.save(app)
//        }
//
//        redirectAttributes.addFlashAttribute("success", "配置已保存")
//        return "redirect:/apps/$appId/configs"
//    }


    data class LiveConfigForm(
        val catchphrases: List<String> = emptyList(),
        val scripts: List<ScriptForm> = emptyList()
    )

    data class ScriptForm(
        val name: String = "",
        val explanation: String = "",
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
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rhythmConfig.json\"")
                ResponseEntity.ok()
                    .headers(headers)
                    .body(resource)
            }
        } catch (e: IOException) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

//    // 上传 JSON 配置文件
//    @PostMapping("/upload-json")
//    fun uploadJsonConfig(
//        @PathVariable appId: Long,
//        @RequestParam("file") file: MultipartFile,
//        principal: Principal,
//        redirectAttributes: RedirectAttributes
//    ): String {
//        val app = appService.findById(appId)
//        var storePath = fileStorageService.storeFile(file)
//        app.liveConfig?.rhythmConfig = loadFileContent(storePath)
//        appService.save(app)
//
//        redirectAttributes.addFlashAttribute("success", "JSON 配置上传成功")
//        return "redirect:/apps/$appId/configs"
//    }
//
//    fun loadFileContent(path: String): RhythmConfig {
//        return ObjectMapper().readValue(Paths.get(path).toFile(), RhythmConfig::class.java)
//    }
//
//    // 验证用户是否有权限访问
//    private fun validateOwnership(appId: Long, username: String) {
//        if (!appService.validateUserOwnership(appId, username)) {
//            throw IllegalArgumentException("You don't have permission to access this resource")
//        }
//    }
}




package com.wtb.livesystem.config.app

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Service
class FileStorageService(
    @Value("\${file.upload-dir}") private val uploadDir: String
) {
    init {
        Files.createDirectories(Paths.get(uploadDir)) // 确保目录存在
    }

    fun storeFile(file: MultipartFile): String {
        val filename = "${UUID.randomUUID()}.json"
        val targetPath = Paths.get(uploadDir).resolve(filename)
        file.transferTo(targetPath)
        return targetPath.toString() // 返回完整路径，或根据需求返回相对路径
    }

}

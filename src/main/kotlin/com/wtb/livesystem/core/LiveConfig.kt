package com.wtb.livesystem.core

import com.fasterxml.jackson.annotation.JsonCreator
import com.wtb.livesystem.config.app.model.App
import jakarta.persistence.*
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.io.FileNotFoundException
import java.util.zip.ZipFile


@Entity
@Table(name = "live_configs")
data class LiveConfig(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", nullable = false)
    val app: App,

    var zipPath:String? = null
){

}

fun parseLiveConfigZip(liveConfig: LiveConfig): LiveConfigDto {
    val dto = LiveConfigDto(liveConfig)
    val zipFile = File(liveConfig.zipPath ?: throw IllegalArgumentException("zipPath is null"))
    if (!zipFile.exists()) throw FileNotFoundException("Zip file not found: ${zipFile.absolutePath}")

    ZipFile(zipFile).use { zip ->
        zip.entries().asSequence().forEach { entry ->
            val name = entry.name
            val inputStream = zip.getInputStream(entry)

            when {
                name.endsWith("rythmConfig.json") -> {
                    dto.rhythmConfig = inputStream.bufferedReader().use {
                        ObjectMapper().readValue(it, RhythmConfig::class.java)
                    }
                }

                name.endsWith("catchphrases.txt") -> {
                    dto.catchphrases = inputStream.bufferedReader().readLines().map { it.trim() }
                        .filter { it.isNotEmpty() }.toMutableList()
                }

                name.matches(Regex("""script\d+\.txt""")) -> {
                    val iniContent = inputStream.bufferedReader().readText()
                    dto.scripts.add(parseScriptCustomFormat(iniContent, name))
                }

                name == "warmup.ini" -> {
                    dto.warmConfig = WarmupConfig(parseWarmup(inputStream.bufferedReader().readLines()))
                }
            }
        }
    }

    return dto
}

//--explanation--
//这是第一句讲解
//这是第二句讲解
//
//--rules--
//在线人数 > 5
//直播时长 > 30 且 直接时长 < 60
fun parseScriptCustomFormat(content: String, fileName: String): Script {
    val sections = mutableMapOf<String, MutableList<String>>()
    var currentSection: String? = null

    content.lineSequence().forEach { line ->
        when {
            line.startsWith("--") && line.endsWith("--") -> {
                currentSection = line.removePrefix("--").removeSuffix("--").trim().lowercase()
                sections[currentSection!!] = mutableListOf()
            }
            currentSection != null -> {
                sections[currentSection]?.add(line)
            }
        }
    }

    val explanation = sections["explanation"]?.joinToString("\n")?.trim() ?: ""
    val ruleString = sections["rules"]?.joinToString(",")?.trim() ?: ""

    val scriptName = parseScriptNameFromFile(fileName)

    return Script(
        name = scriptName,
        explanation = explanation,
        ruleString = ruleString
    )
}

fun parseScriptNameFromFile(fileName: String): String {
    return Regex("""script_(.+)\.\w+$""")
        .find(fileName)
        ?.groupValues?.get(1)
        ?: "未命名稿"
}

fun parseWarmup(lines: List<String>): MutableList<Warmup> {
    val warmups = mutableListOf<Warmup>()
    for (line in lines) {
        if (line.isBlank()) continue
        val parts = line.split("#", limit = 2)
        val content = parts[0].trim()
        val affinities = mutableListOf<WarmupAffinity>()

        if (parts.size == 2) {
            val affinityStr = parts[1].trim()
            val regex = Regex("""\("(\w+)",\s*([0-9.]+)\)""")
            regex.findAll(affinityStr).forEach {
                val (type, weight) = it.destructured
                affinities.add(WarmupAffinity(LiveEventType.valueOf(type), weight.toFloat()))
            }
        }

        warmups.add(Warmup(content, affinities))
    }
    return warmups
}

data class LiveConfigDto(val config:LiveConfig){
    var rhythmConfig: RhythmConfig? = null
    var catchphrases: MutableList<String> = mutableListOf()
    var scripts: MutableList<Script> = mutableListOf()
    var warmConfig:WarmupConfig = WarmupConfig(mutableListOf())
}

data class WarmupConfig @JsonCreator constructor( @JsonProperty("name") val warmups:MutableList<Warmup>)

data class Warmup(val content:String,val affinities: MutableList<WarmupAffinity>)

data class WarmupAffinity(val eventType:LiveEventType, val weightBias:Float)


data class RhythmConfig @JsonCreator constructor(
    @JsonProperty("name") val name: String,
    @JsonProperty("type") val type: String,
    @JsonProperty("baseParameter") val baseParameter: BaseParameter,
    @JsonProperty("emotions") val emotions: List<Emotion>
){

}

@Entity
@Table(name = "scripts")
data class Script(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String, // 话术稿的名称（例如：A话术稿，B话术稿等）

    @Column(nullable = false)
    var explanation: String, // 讲解内容

    @Column(name = "rules")
    var ruleString: String = "" // 触发规则列表

)



data class DurationRange  @JsonCreator constructor(
    @JsonProperty("start") val start: Int,
    @JsonProperty("end") val end: Int
)

data class BaseParameter @JsonCreator constructor(
    @JsonProperty("speedRate") val speedRate: Double,
    @JsonProperty("volume") val volume: Double,
    @JsonProperty("breakProbability") val breakProbability: Double,
    @JsonProperty("breakDurationRange") val breakDurationRange: DurationRange,
    @JsonProperty("breathProbability") val breathProbability: Double,
    @JsonProperty("breathDurationRange") val breathDurationRange: DurationRange,
    @JsonProperty("fillWordsProbability") val fillWordsProbability: Double,
    @JsonProperty("speechErrorProbability") val speechErrorProbability: Double
)

// Apply the same pattern to Emotion and Bias classes
data class Emotion @JsonCreator constructor(
    @JsonProperty("name") val name: String,
    @JsonProperty("bias") val bias: Bias
)

data class Bias @JsonCreator constructor(
    @JsonProperty("speedRateBias") val speedRateBias: Double,
    @JsonProperty("volumeBias") val volumeBias: Double,
    @JsonProperty("breakProbabilityBias") val breakProbabilityBias: Double,
    @JsonProperty("breakDurationRangeBias") val breakDurationRangeBias: DurationRange,
    @JsonProperty("breathProbabilityBias") val breathProbabilityBias: Double,
    @JsonProperty("breathDurationRangeBias") val breathDurationRangeBias: DurationRange,
    @JsonProperty("fillWordsProbabilityBias") val fillWordsProbabilityBias: Double,
    @JsonProperty("speechErrorProbabilityBias") val speechErrorProbabilityBias: Double
)
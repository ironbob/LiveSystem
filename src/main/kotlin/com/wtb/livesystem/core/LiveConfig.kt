package com.wtb.livesystem.core

import com.fasterxml.jackson.annotation.JsonCreator
import com.wtb.livesystem.config.app.model.App
import jakarta.persistence.*
import com.fasterxml.jackson.annotation.JsonProperty


@Entity
@Table(name = "live_configs")
data class LiveConfig(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", nullable = false)
    val app: App,

    @Column(name = "playback_rhythm_path", length = 512) // 存储文件路径
    var playbackRhythmPath: String? = null, // 文件路径（如 "uploads/configs/123.json"）

    @ElementCollection
    @CollectionTable(name = "live_catchphrases", joinColumns = [JoinColumn(name = "live_config_id")])
    @Column(name = "catchphrase")
    var catchphrases: MutableList<String> = mutableListOf(), // 口头禅列表

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "live_config_id")
    var scripts: MutableList<Script> = mutableListOf() // 直播话术稿列表
){
    @Transient
    var rhythmConfig: RhythmConfig? = null
}
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

    @Column(nullable = false)
    var warmUpContent: String, // 暖场内容

    @ElementCollection
    @CollectionTable(name = "script_triggers", joinColumns = [JoinColumn(name = "script_id")])
    @Column(name = "trigger")
    var triggers: MutableList<String> = mutableListOf() // 触发规则列表

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
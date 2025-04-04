package com.wtb.livesystem.config.app

import com.wtb.livesystem.config.app.model.App
import jakarta.persistence.*


@Entity
@Table(name = "live_configs")
data class LiveConfig(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", nullable = false)
    val app: App,

    @Column(columnDefinition = "TEXT")
    var playbackRhythmJson: String = "{}", // 播放节奏配置 (JSON)

    @ElementCollection
    @CollectionTable(name = "live_catchphrases", joinColumns = [JoinColumn(name = "live_config_id")])
    @Column(name = "catchphrase")
    var catchphrases: MutableList<String> = mutableListOf(), // 口头禅列表

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "live_config_id")
    var scripts: MutableList<Script> = mutableListOf() // 直播话术稿列表
)

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
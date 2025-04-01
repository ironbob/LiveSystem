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

        @ElementCollection
        @CollectionTable(name = "live_scripts", joinColumns = [JoinColumn(name = "live_config_id")])
        @Column(name = "script")
        var scripts: MutableList<String> = mutableListOf() // 直播话术稿列表
    )
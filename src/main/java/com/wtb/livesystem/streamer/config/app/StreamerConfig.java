package com.wtb.livesystem.streamer.config.app;
import com.wtb.livesystem.config.app.model.App;
import jakarta.persistence.*;

@Entity
public class StreamerConfig {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String configName;

    @Lob // 大文本字段
    @Column(nullable = false)
    private String jsonConfig;

    @ManyToOne
    @JoinColumn(name = "app_id", nullable = false)
    private App app;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getJsonConfig() {
        return jsonConfig;
    }

    public void setJsonConfig(String jsonConfig) {
        this.jsonConfig = jsonConfig;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }
}

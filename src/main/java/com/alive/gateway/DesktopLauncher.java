package com.alive.gateway;

import com.alive.gateway.backend.GatewayApplication;
import javafx.application.Application;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class DesktopLauncher {
    public static void main(String[] args) {
        //Start Spring Boot in Background thread (Same JVM)
        new Thread(() -> {
            new SpringApplicationBuilder(GatewayApplication.class)
                    .logStartupInfo(false)
                    .run(args);
        }).start();

        //Then Start JavaFX UI
        Application.launch(App.class, args);
    }
}

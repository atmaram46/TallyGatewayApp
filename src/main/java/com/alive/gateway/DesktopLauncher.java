package com.alive.gateway;

import com.alive.gateway.backend.GatewayApplication;
import javafx.application.Application;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class DesktopLauncher {
    public static void main(String[] args) {
        System.out.println(">>> DesktopLauncher main started <<<");
        //Start Spring Boot in Background thread (Same JVM)
        new Thread(() -> {
            System.out.println(">>> Starting Spring Boot <<<");
            new SpringApplicationBuilder(GatewayApplication.class)
                    .logStartupInfo(false)
                    .run(args);
        }).start();

        //Then Start JavaFX UI
        System.out.println(">>> Launching JavaFX <<<");
        Application.launch(App.class, args);
    }
}

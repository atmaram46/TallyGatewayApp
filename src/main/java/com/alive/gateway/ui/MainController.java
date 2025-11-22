package com.alive.gateway.ui;

import javafx.fxml.FXML;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;


public class MainController {
    @FXML
    public TextField tallyHost;
    @FXML
    public TextField apiKey;
    @FXML
    public TextField tallyExe;
    @FXML
    public TextField gatewayPort;
    @FXML
    public TextArea logArea;

    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();

    @FXML
    public void initialize() {
        apiKey.setText(UUID.randomUUID().toString());
    }

    @FXML
    public void onStart() {
        String host = tallyHost.getText().trim();
        String port = gatewayPort.getText().trim();
        String key = apiKey.getText().trim();

        new Thread(() -> {
            boolean ready = probeTally(host, 60);
            if(!ready) {
                System.out.println("Tally not ready");
                return;
            }
            System.out.println("API key:" + key);
        }).start();
    }

    private boolean probeTally(String host, int timeout) {
        String probe = "<?xml version=\"1.0\"?>\n<ENVELOPE>\n<HEADER>\n<TALLYREQUEST>EXPORT</TALLYREQUEST>\n</HEADER>\n<BODY>\n<EXPORTDATA>\n<REQUESTDESC>\n<REPORTNAME>SysInfo</REPORTNAME>\n</REQUESTDESC>\n</EXPORTDATA>\n</BODY>\n</ENVELOPE>";
        int attempts = timeout;
        for(int i = 0; i < attempts; i++) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(host)).timeout(Duration.ofSeconds(60))
                        .header("Content-Type", "application/xml")
                        .POST(HttpRequest.BodyPublishers.ofString(probe, StandardCharsets.UTF_8))
                        .build();
                HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
                String body = resp.body();
                if(body != null && (body.contains("Tally") || body.contains("<ENVELOP") || body.contains("<COMPANY"))) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @FXML
    public void onStop() {
        String tally = tallyExe.getText().trim();
    }

}

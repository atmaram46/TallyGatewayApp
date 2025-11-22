package com.alive.gateway.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class TallyService {
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final XmlMapper xmlMapper = new XmlMapper();
    @Value("${tally.host:http://localhost:9000}")
    private String tallyHost;

    public String postXml(String xml) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tallyHost)).timeout(Duration.ofSeconds(60))
                .header("Content-Type", "application/xml")
                .POST(HttpRequest.BodyPublishers.ofString(xml))
                .build();
        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        return xml;
    }

    public boolean probe() {
        String probeXml = "<?xml version=\"1.0\">\n<ENVELOPE>\n<HEADER><VERSION>1</VERSION><TALLYREQUEST>EXPORT</TALLYREQUEST></HEADER><BODY><EXPORTDATA><REQUESTDESC><REPORTNAME>SysInfo</REPORTNAME></REQUESTDESC></EXPORTDATA></BODY></ENVELOPE>";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(tallyHost)).timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/xml")
                    .POST(HttpRequest.BodyPublishers.ofString(probeXml))
                    .build();
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = resp.body();
            return body != null && (body.contains("<ENVELOPE") || body.contains("<COMPANY") || body.contains("Tally"));
        } catch (Exception e) {
            return false;
        }
    }

    public JsonNode xmlToJson(String xml) throws IOException {
        return xmlMapper.readTree(xml.getBytes());
    }
}

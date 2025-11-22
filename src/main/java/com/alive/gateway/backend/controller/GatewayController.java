package com.alive.gateway.backend.controller;

import com.alive.gateway.backend.service.TallyService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.IOException;

@RestController
@RequestMapping("/v1")
public class GatewayController {
    private final TallyService tallyService;

    @Value("${gateway.api-key:}")
    private String apiKey;

    @Autowired
    public GatewayController(TallyService tallyService) {
        this.tallyService = tallyService;
    }

    private boolean checkApiKey(String key) {
        if(this.apiKey == null || this.apiKey.isEmpty()) return key != null && !key.isBlank();
        return key != null && key.equals(apiKey);
    }

    @PostMapping(value = "/raw", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> raw(@RequestHeader(value = "x-api-key", required = false) String key,
                                      @RequestBody String xml) throws IOException, InterruptedException {
        if(!checkApiKey(key)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String response = tallyService.postXml(xml);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/raw/json", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> rawJson(@RequestHeader(value = "x-api-key", required = false) String key,
                                      @RequestBody String xml) throws IOException, InterruptedException {
        if(!checkApiKey(key)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String response = tallyService.postXml(xml);
        JsonNode node = tallyService.xmlToJson(response);
        return ResponseEntity.ok(node);
    }

    @PostMapping(value = "/import", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> importXml(@RequestHeader(value = "x-api-key", required = false) String key,
                                            @RequestBody String xml) throws IOException, InterruptedException {
        if(!checkApiKey(key)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String response = tallyService.postXml(xml);
        JsonNode node = tallyService.xmlToJson(response);
        return ResponseEntity.ok(node);
    }

}

package com.dac.charge_proxy.controller;

import com.dac.charge_proxy.business.observer.WebhookEventObserver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/webhook/asaas")
public class AsaasWebhookController {

    private final List<WebhookEventObserver> observers = new ArrayList<>();
    
    @Value("${asaas.webhook.token}")
    private String webhookToken;

    public AsaasWebhookController(List<WebhookEventObserver> observers) {
        this.observers.addAll(observers);
    }

    @PostMapping
    public ResponseEntity<String> receiveWebhook(
            @RequestHeader(value = "Asaas-Access-Token", required = false) String token,
            @RequestBody Map<String, Object> payload
    ) {
        System.out.println(token);
        System.out.println(webhookToken);
        if (token == null || !token.equals(webhookToken)) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String event = payload.get("event").toString();
        String jsonPayload;

        try {
            jsonPayload = new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(payload);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao serializar payload");
        }

        notifyObservers(event, jsonPayload);

        return ResponseEntity.ok("Evento recebido e observadores notificados");
    }

    private void notifyObservers(String event, String payload) {
        for (WebhookEventObserver observer : observers) {
            try {
                observer.onWebhookEvent(event, payload);
            } catch (Exception e) {
                System.err.println("Erro ao notificar observer: " + e.getMessage());
            }
        }
    }
}
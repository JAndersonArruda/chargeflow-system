package com.dac.charge_manager.api;

import com.dac.charge_manager.business.asaas.AsaasEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/internal/asaas")
public class AsaasController {

    private final AsaasEventService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public AsaasController(AsaasEventService service) {
        this.service = service;
    }

    @PostMapping
    public void receive(@RequestBody Map<String, Object> payload) throws Exception {
        String event = payload.get("event").toString();
        String json = mapper.writeValueAsString(payload);
        service.save(event, json);
    }
}
package com.dac.charge_manager.api.soap;

import com.dac.chargemanager.*;

import com.dac.charge_manager.business.asaas.AsaasEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Endpoint
public class AsaasEventEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(AsaasEventEndpoint.class);
    private static final String NAMESPACE = "http://dac.com/chargemanager";

    private final AsaasEventService service;

    public AsaasEventEndpoint(AsaasEventService service) {
        this.service = service;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "AsaasEventRequest")
    @ResponsePayload
    public AsaasEventResponse receive(@RequestPayload AsaasEventRequest request) {
        String event = request.getEvent();
        String payload = request.getPayload();

        logger.info("📩 Webhook ASAAS recebido - event={}, payload={}", event, payload);

        service.save(event, payload);

        AsaasEventResponse response = new AsaasEventResponse();
        response.setStatus("SALVO_COM_SUCESSO");

        return response;
    }
}
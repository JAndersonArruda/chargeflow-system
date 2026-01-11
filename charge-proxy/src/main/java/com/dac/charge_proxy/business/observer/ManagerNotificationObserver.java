package com.dac.charge_proxy.business.observer;

import com.dac.charge_proxy.soap.AsaasSoapClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class ManagerNotificationObserver implements WebhookEventObserver {

    private static final Logger logger = LoggerFactory.getLogger(ManagerNotificationObserver.class);
    
    private final AsaasSoapClient soapClient;

    public ManagerNotificationObserver(AsaasSoapClient soapClient) {
        this.soapClient = soapClient;
    }

    @Override
    public void onWebhookEvent(String event, String payload) {
        logger.info("🔔 Observer notificando Manager via SOAP - event: {}", event);
        try {
            soapClient.sendEvent(event, payload);
            logger.info("✅ Manager notificado com sucesso via SOAP");
        } catch (Exception e) {
            logger.error("❌ Erro ao notificar Manager via SOAP: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao notificar Manager", e);
        }
    }
}







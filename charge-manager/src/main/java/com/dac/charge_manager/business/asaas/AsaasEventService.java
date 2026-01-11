package com.dac.charge_manager.business.asaas;

import com.dac.charge_manager.business.charge.Charge;
import com.dac.charge_manager.business.charge.ChargeService;
import com.dac.charge_manager.infra.repository.AsaasEventRepository;
import com.dac.charge_manager.infra.repository.ChargeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AsaasEventService {

    private static final Logger logger = LoggerFactory.getLogger(AsaasEventService.class);

    private final AsaasEventRepository repository;
    private final ChargeRepository chargeRepository;
    private final ChargeService chargeService;
    private final ObjectMapper mapper = new ObjectMapper();

    public AsaasEventService(
            AsaasEventRepository repository,
            ChargeRepository chargeRepository,
            ChargeService chargeService
    ) {
        this.repository = repository;
        this.chargeRepository = chargeRepository;
        this.chargeService = chargeService;
    }

    public void save(String event, String payload) {
        AsaasEvent e = new AsaasEvent();
        e.setEvent(event);
        e.setPayload(payload);
        repository.save(e);

        if ("PAYMENT_RECEIVED".equals(event)) {
            processPaymentReceived(payload);
        }
    }

    private void processPaymentReceived(String payload) {
        try {
            JsonNode root = mapper.readTree(payload);

            String idCandidate = null;
            if (root.has("payment")) {
                idCandidate = root.path("payment").path("id").asText(null);
            }
            if (idCandidate == null || idCandidate.isBlank()) {
                idCandidate = root.path("id").asText(null);
            }

            if (idCandidate == null || idCandidate.isBlank()) {
                logger.error("Webhook ASAAS sem campo 'id' válido: {}", payload);
                return;
            }

            final String asaasIdFinal = idCandidate;

            Charge charge = chargeRepository
                    .findByAsaasId(asaasIdFinal)
                    .orElse(null);

            if (charge == null) {
                logger.error("Cobrança não encontrada para asaasId={}", asaasIdFinal);
                return;
            }

            chargeService.markAsPaid(charge.getId());
            logger.info("Cobrança {} marcada como PAGA via webhook ASAAS", charge.getId());

        } catch (Exception ex) {
            logger.error("Erro ao processar webhook ASAAS: {}", ex.getMessage(), ex);
        }
    }
}
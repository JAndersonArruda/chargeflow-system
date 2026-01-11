package com.dac.charge_manager.business.charge;

import com.dac.chargeproxy.wsdl.*;

import com.dac.charge_manager.business.client.Client;
import com.dac.charge_manager.infra.repository.ChargeRepository;
import com.dac.charge_manager.infra.soap.ChargeProxyClient;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ChargeService {

    private final ChargeRepository repository;
    private final ChargeProxyClient proxyClient;

    public ChargeService(
            ChargeRepository repository,
            ChargeProxyClient proxyClient
    ) {
        this.repository = repository;
        this.proxyClient = proxyClient;
    }

    @Transactional
    public Charge create(Client client, Double value, ChargeType type) {
        Charge charge = new Charge();
        charge.setClient(client);
        charge.setValue(value);
        charge.setType(type);
        charge.setStatus(ChargeStatus.PENDING);
        charge = repository.save(charge);

        System.out.println(charge);

        CreateChargeResponse response = proxyClient.createCharge(
                charge.getId(),
                value,
                type.name(),
                client.getName(),
                client.getEmail(),
                client.getCpfCnpj()
        );

        String asaasId = response.getAsaasId();

        charge.setAsaasId(asaasId);
        charge.setStatus(ChargeStatus.REGISTERED);

        return repository.save(charge);
    }

    @Transactional
    public void cancel(Long chargeId) {
        Charge charge = repository.findById(chargeId).orElseThrow();
        CancelChargeResponse response = proxyClient.cancelCharge(charge.getAsaasId());
        charge.setStatus(ChargeStatus.CANCELED);
        repository.save(charge);
    }

    @Transactional
    public void markAsPaid(Long chargeId) {
        Charge charge = repository.findById(chargeId).orElseThrow();
        charge.setStatus(ChargeStatus.PAID);
        repository.save(charge);
    }
}
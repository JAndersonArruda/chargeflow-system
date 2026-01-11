package com.dac.charge_proxy.soap;

import com.dac.charge_proxy.asaas.AsaasRestClient;
import com.dac.chargeproxy.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.Map;


@Endpoint
public class ChargeProxyEndpoint {
    private static final String NAMESPACE = "http://dac.com/chargeproxy";

    private final AsaasRestClient asaasClient;
    private final ObjectFactory objectFactory;

    public ChargeProxyEndpoint(AsaasRestClient asaasClient) {
        this.asaasClient = asaasClient;
        this.objectFactory = new ObjectFactory();
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "CreateChargeRequest")
    @ResponsePayload
    public CreateChargeResponse createCharge(@RequestPayload CreateChargeRequest request) {
        String clientName = request.getClientName();
        String clientEmail = request.getClientEmail();
        String clientCpfCnpj = request.getClientCpfCnpj();
        double value = request.getValue();
        String type = request.getType();

        Map<String, Object> customer = asaasClient.createCustomer(clientName, clientEmail, clientCpfCnpj);
        String customerId = customer.get("id").toString();

        Map<String, Object> payment = asaasClient.createPayment(
                customerId,
                value,
                type
        );

        String asaasId = payment.get("id").toString();

        CreateChargeResponse response = objectFactory.createCreateChargeResponse();
        response.setAsaasId(asaasId);
        response.setStatus("REGISTERED");

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "CancelChargeRequest")
    @ResponsePayload
    public CancelChargeResponse cancelCharge(@RequestPayload CancelChargeRequest request) {
        String asaasId = request.getAsaasId();

        asaasClient.cancelPayment(asaasId);

        CancelChargeResponse response = objectFactory.createCancelChargeResponse();
        response.setStatus("CANCELED");

        return response;
    }
}

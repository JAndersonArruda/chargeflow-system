package com.dac.charge_proxy.soap;

import jakarta.annotation.PostConstruct;
import jakarta.xml.ws.BindingProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dac.chargemanager.wsdl.*;


@Service
public class AsaasSoapClient {

    private AsaasPort port;
    @Value("${manager.soap.url:http://charge-manager:8081/ws}")
    private String managerSoapUrl;

    @PostConstruct
    public void init() {
        AsaasPortService service = new AsaasPortService();
        
        this.port = service.getAsaasPortSoap11();
        
        if (port instanceof BindingProvider) {
            BindingProvider bindingProvider = (BindingProvider) port;
            bindingProvider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, managerSoapUrl);
        }
    }

    public String sendEvent(String event, String payload) {
        AsaasEventRequest request = new AsaasEventRequest();
        request.setEvent(event);
        request.setPayload(payload);

        try {
            AsaasEventResponse response = port.asaasEvent(request);
            return response != null ? response.getStatus() : "No response";
        } catch (Exception e) {
            throw new RuntimeException("Erro ao chamar serviço SOAP: " + e.getMessage(), e);
        }
    }
}
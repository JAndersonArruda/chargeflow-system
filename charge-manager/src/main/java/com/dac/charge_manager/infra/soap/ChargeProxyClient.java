package com.dac.charge_manager.infra.soap;

import org.springframework.stereotype.Service;

import com.dac.chargeproxy.wsdl.*;

import jakarta.annotation.PostConstruct;
import jakarta.xml.ws.BindingProvider;
import org.springframework.beans.factory.annotation.Value;

@Service
public class ChargeProxyClient {

    private ChargeProxyPort port;
    @Value("${proxy.soap.url:http://charge-proxy:8080/ws}")
    private String proxySoapUrl;

    @PostConstruct
    public void init() {
        ChargeProxyPortService service = new ChargeProxyPortService();
        
        this.port = service.getChargeProxyPortSoap11();
        
        if (port instanceof BindingProvider) {
            BindingProvider bindingProvider = (BindingProvider) port;
            bindingProvider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, proxySoapUrl);
        }
    }

    public CreateChargeResponse createCharge(
            Long chargeId,
            Double value,
            String type,
            String clientName,
            String clientEmail,
            String clientCpfCnpj
    ) {
        System.out.println("Chegou no cliente proxy");
        CreateChargeRequest request = new CreateChargeRequest();
        request.setChargeId(chargeId);
        request.setValue(value);
        request.setType(type);
        request.setClientName(clientName);
        request.setClientEmail(clientEmail);
        request.setClientCpfCnpj(clientCpfCnpj);

        System.out.println(proxySoapUrl);
        System.out.println(port);
        return port.createCharge(request);
    }

    public CancelChargeResponse cancelCharge(String asaasId) {
        CancelChargeRequest request = new CancelChargeRequest();
        request.setAsaasId(asaasId);
        
        return port.cancelCharge(request);
    }
}
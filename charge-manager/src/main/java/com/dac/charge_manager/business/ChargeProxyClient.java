package com.dac.charge_manager.business;

import com.dac.chargeproxy.wsdl.*;
import jakarta.xml.ws.BindingProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class ChargeProxyClient {
    private ChargeProxyPort port;

    @Value("${proxy.soap.url:http://192.168.56.10:8080/ws}")
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

    public String test(String message) {
        TestRequest req = new TestRequest();
        req.setMessage(message);

        TestResponse resp = port.test(req);
        return resp.getResult();
    }
}

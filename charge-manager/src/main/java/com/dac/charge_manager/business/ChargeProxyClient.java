package com.dac.charge_manager.business;

import com.dac.chargeproxy.wsdl.*;
import org.springframework.stereotype.Component;

@Component
public class ChargeProxyClient {
    private final ChargeProxyPort port;

    public ChargeProxyClient() {
        ChargeProxyPortService service = new ChargeProxyPortService();
        this.port = service.getChargeProxyPortSoap11();
    }

    public String test(String message) {
        TestRequest req = new TestRequest();
        req.setMessage(message);

        TestResponse resp = port.test(req);
        return resp.getResult();
    }
}

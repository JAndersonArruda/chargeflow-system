package com.dac.charge_proxy.endpoint;

import com.dac.chargeproxy.TestRequest;
import com.dac.chargeproxy.TestResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


@Endpoint
public class ChargeProxyEndpoint {
    private static final String NAMESPACE = "http://dac.com/chargeproxy";

    @PayloadRoot(namespace = NAMESPACE, localPart = "TestRequest")
    @ResponsePayload
    public TestResponse test(@RequestPayload TestRequest request) {
        TestResponse response = new TestResponse();
        response.setResult("Proxy received hot reload: " + request.getMessage());
        return response;
    }
}

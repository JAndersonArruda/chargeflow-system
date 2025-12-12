package com.dac.charge_manager.api;

import com.dac.charge_manager.business.ChargeProxyClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/charge-manager")
public class TestController {
    private final ChargeProxyClient soapClient;

    public TestController(ChargeProxyClient soapClient) {
        this.soapClient = soapClient;
    }

    @GetMapping("/test-route")
    public ResponseEntity<String> testRoute() {
        String proxyResult = soapClient.test("Hello Manager...");

        return ResponseEntity.ok(
                "Manager OK -> " + proxyResult
        );
    }
}

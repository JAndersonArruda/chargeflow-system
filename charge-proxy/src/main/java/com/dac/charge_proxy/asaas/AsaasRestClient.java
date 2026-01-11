package com.dac.charge_proxy.asaas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class AsaasRestClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public AsaasRestClient(
            RestTemplate restTemplate,
            @Value("${asaas.base-url}") String baseUrl,
            @Value("${asaas.api-key}") String apiKey
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("access_token", apiKey);
        return headers;
    }

    public Map createCustomer(String name, String email, String cpfCnpj) {
        System.out.println(apiKey);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(
                        Map.of(
                                "name", name,
                                "email", email,
                                "cpfCnpj", cpfCnpj
                        ),
                        headers()
                );

        return restTemplate.postForObject(
                baseUrl + "/customers",
                request,
                Map.class
        );
    }

    public Map createPayment(String customerId, Double value, String type) {

        String billingType = switch (type) {
            case "PIX" -> "PIX";
            case "CARTAO_CREDITO" -> "CREDIT_CARD";
            default -> "BOLETO";
        };

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(
                        Map.of(
                                "customer", customerId,
                                "billingType", billingType,
                                "value", value,
                                "dueDate", "2026-01-10"
                        ),
                        headers()
                );

        return restTemplate.postForObject(
                baseUrl + "/payments",
                request,
                Map.class
        );
    }

    public void cancelPayment(String paymentId) {
        HttpEntity<Void> request = new HttpEntity<>(headers());

        restTemplate.exchange(
                baseUrl + "/payments/" + paymentId,
                HttpMethod.DELETE,
                request,
                Void.class
        );
    }
}
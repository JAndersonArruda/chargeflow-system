package com.dac.charge_proxy.business.observer;


public interface WebhookEventObserver {
    void onWebhookEvent(String event, String payload);
}







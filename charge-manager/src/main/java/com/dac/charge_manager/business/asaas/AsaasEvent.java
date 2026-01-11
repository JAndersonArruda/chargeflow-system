package com.dac.charge_manager.business.asaas;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "asaas_event")
public class AsaasEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String event;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public void setEvent(String event) {
        this.event = event;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
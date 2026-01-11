package com.dac.charge_manager.business.charge;

import com.dac.charge_manager.business.client.Client;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "charge")
public class Charge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double value;

    @Enumerated(EnumType.STRING)
    private ChargeType type;

    @Enumerated(EnumType.STRING)
    private ChargeStatus status;

    @Column(name = "asaas_id")
    private String asaasId;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public void setStatus(ChargeStatus status) {
        this.status = status;
    }

    public void setAsaasId(String asaasId) {
        this.asaasId = asaasId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public ChargeType getType() {
        return type;
    }

    public void setType(ChargeType type) {
        this.type = type;
    }

    public ChargeStatus getStatus() {
        return status;
    }

    public String getAsaasId() {
        return asaasId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
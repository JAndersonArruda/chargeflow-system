package com.dac.charge_manager.infra.repository;

import com.dac.charge_manager.business.asaas.AsaasEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsaasEventRepository extends JpaRepository<AsaasEvent, Long> {
}
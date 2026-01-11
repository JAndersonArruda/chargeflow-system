package com.dac.charge_manager.infra.repository;

import com.dac.charge_manager.business.charge.Charge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChargeRepository extends JpaRepository<Charge, Long> {
    Optional<Charge> findByAsaasId(String asaasId);
}
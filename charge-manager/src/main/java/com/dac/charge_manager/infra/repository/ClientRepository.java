package com.dac.charge_manager.infra.repository;

import com.dac.charge_manager.business.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
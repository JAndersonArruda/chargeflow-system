package com.dac.charge_manager.api;

import com.dac.charge_manager.business.client.Client;
import com.dac.charge_manager.infra.repository.ClientRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientRepository repository;

    public ClientController(ClientRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Client create(@RequestBody Client client) {
        return repository.save(client);
    }
}
package com.dac.charge_manager.api;

import com.dac.charge_manager.business.charge.Charge;
import com.dac.charge_manager.business.charge.ChargeService;
import com.dac.charge_manager.business.charge.ChargeType;
import com.dac.charge_manager.business.client.Client;
import com.dac.charge_manager.infra.repository.ChargeRepository;
import com.dac.charge_manager.infra.repository.ClientRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/charges")
public class ChargeController {

    private final ChargeService chargeService;
    private final ClientRepository clientRepository;
    private final ChargeRepository chargeRepository;

    public ChargeController(
            ChargeService chargeService,
            ClientRepository clientRepository,
            ChargeRepository chargeRepository
    ) {
        this.chargeService = chargeService;
        this.clientRepository = clientRepository;
        this.chargeRepository = chargeRepository;
    }

    @PostMapping
    public Charge create(
            @RequestParam Long clientId,
            @RequestParam Double value,
            @RequestParam ChargeType type
    ) {
        System.out.println("Chegou a req");
        Client client = clientRepository.findById(clientId).orElseThrow();
        System.out.println(client);
        return chargeService.create(client, value, type);
    }

    @PostMapping("/{id}/cancel")
    public Charge cancel(@PathVariable Long id) {
        chargeService.cancel(id);
        return chargeRepository.findById(id).orElseThrow();
    }
}
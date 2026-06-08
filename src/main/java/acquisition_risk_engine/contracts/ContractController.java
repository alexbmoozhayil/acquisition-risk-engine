package acquisition_risk_engine.contracts;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ContractController {

    private final ContractRepository contractRepository;

    public ContractController(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    @GetMapping("/contracts")
    public List<ContractResponse> getContracts() {
        return contractRepository.findAll();
    }
}
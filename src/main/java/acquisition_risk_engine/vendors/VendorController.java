package acquisition_risk_engine.vendors;

import acquisition_risk_engine.contracts.ContractRepository;
import acquisition_risk_engine.contracts.ContractResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class VendorController {

    private final VendorRepository vendorRepository;
    private final ContractRepository contractRepository;

    public VendorController(
            VendorRepository vendorRepository,
            ContractRepository contractRepository
    ) {
        this.vendorRepository = vendorRepository;
        this.contractRepository = contractRepository;
    }

    @GetMapping("/vendors")
    public List<VendorResponse> getVendors() {
        return vendorRepository.findAll();
    }

    @GetMapping("/vendors/{vendorId}/contracts")
    public List<ContractResponse> getVendorContracts(@PathVariable Long vendorId) {
        return contractRepository.findByVendorId(vendorId);
    }
}
package acquisition_risk_engine.risk;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RiskController {

    private final RiskService riskService;

    public RiskController(RiskService riskService) {
        this.riskService = riskService;
    }

    @GetMapping("/vendors/{vendorId}/risk")
    public VendorRiskResponse getVendorRisk(@PathVariable Long vendorId) {
        return riskService.calculateVendorRisk(vendorId);
    }
}
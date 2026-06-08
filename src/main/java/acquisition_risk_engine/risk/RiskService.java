package acquisition_risk_engine.risk;

import acquisition_risk_engine.contracts.ContractRepository;
import acquisition_risk_engine.contracts.ContractResponse;
import acquisition_risk_engine.risk.rules.RiskRule;
import acquisition_risk_engine.vendors.VendorRepository;
import acquisition_risk_engine.vendors.VendorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class RiskService {

    private final VendorRepository vendorRepository;
    private final ContractRepository contractRepository;
    private final List<RiskRule> riskRules;

    public RiskService(
            VendorRepository vendorRepository,
            ContractRepository contractRepository,
            List<RiskRule> riskRules
    ) {
        this.vendorRepository = vendorRepository;
        this.contractRepository = contractRepository;
        this.riskRules = riskRules;
    }

    public VendorRiskResponse calculateVendorRisk(Long vendorId) {
        VendorResponse vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vendor not found"
                ));

        List<ContractResponse> contracts = contractRepository.findByVendorId(vendorId);
        List<RiskSignalResponse> signals = new ArrayList<>();

        for (RiskRule rule : riskRules) {
            signals.addAll(rule.evaluate(contracts));
        }

        int overallRiskScore = signals.stream()
                .mapToInt(RiskSignalResponse::scoreImpact)
                .sum();

        overallRiskScore = Math.min(overallRiskScore, 100);

        String riskLevel = determineRiskLevel(overallRiskScore);

        return new VendorRiskResponse(
                vendor.id(),
                vendor.name(),
                overallRiskScore,
                riskLevel,
                signals
        );
    }

    private String determineRiskLevel(int score) {
        if (score >= 70) {
            return "HIGH";
        }

        if (score >= 40) {
            return "MEDIUM";
        }

        return "LOW";
    }
}
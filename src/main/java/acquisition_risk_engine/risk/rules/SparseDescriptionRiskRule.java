package acquisition_risk_engine.risk.rules;

import acquisition_risk_engine.contracts.ContractResponse;
import acquisition_risk_engine.risk.RiskSignalResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(4)
public class SparseDescriptionRiskRule implements RiskRule {

    @Override
    public List<RiskSignalResponse> evaluate(List<ContractResponse> contracts) {
        return contracts.stream()
                .filter(contract -> contract.description() == null
                        || contract.description().trim().split("\\s+").length < 6)
                .findFirst()
                .map(contract -> List.of(new RiskSignalResponse(
                        "SPARSE_DESCRIPTION",
                        "LOW",
                        10,
                        "Vendor has at least one contract with a short description.",
                        contract.awardId() + " has a sparse description."
                )))
                .orElse(List.of());
    }
}
package acquisition_risk_engine.risk.rules;

import acquisition_risk_engine.contracts.ContractResponse;
import acquisition_risk_engine.risk.RiskSignalResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
public class MultipleContractsRiskRule implements RiskRule {

    @Override
    public List<RiskSignalResponse> evaluate(List<ContractResponse> contracts) {
        if (contracts.size() < 2) {
            return List.of();
        }

        return List.of(new RiskSignalResponse(
                "MULTIPLE_CONTRACTS",
                "MEDIUM",
                15,
                "Vendor has multiple contracts in the database.",
                "Vendor has " + contracts.size() + " contracts."
        ));
    }
}
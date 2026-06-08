package acquisition_risk_engine.risk.rules;

import acquisition_risk_engine.contracts.ContractResponse;
import acquisition_risk_engine.risk.RiskSignalResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Order(3)
public class EndedContractRiskRule implements RiskRule {

    @Override
    public List<RiskSignalResponse> evaluate(List<ContractResponse> contracts) {
        LocalDate today = LocalDate.now();

        return contracts.stream()
                .filter(contract -> contract.endDate() != null)
                .filter(contract -> contract.endDate().isBefore(today))
                .findFirst()
                .map(contract -> List.of(new RiskSignalResponse(
                        "ENDED_CONTRACT",
                        "MEDIUM",
                        20,
                        "Vendor has at least one contract with an end date in the past.",
                        contract.awardId() + " ended on " + contract.endDate() + "."
                )))
                .orElse(List.of());
    }
}
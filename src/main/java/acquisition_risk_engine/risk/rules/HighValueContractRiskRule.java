package acquisition_risk_engine.risk.rules;

import acquisition_risk_engine.contracts.ContractResponse;
import acquisition_risk_engine.risk.RiskSignalResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Component
@Order(1)
public class HighValueContractRiskRule implements RiskRule {

    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("200000000");

    @Override
    public List<RiskSignalResponse> evaluate(List<ContractResponse> contracts) {
        return contracts.stream()
                .filter(contract -> contract.awardAmount() != null)
                .filter(contract -> contract.awardAmount().compareTo(HIGH_VALUE_THRESHOLD) >= 0)
                .max(Comparator.comparing(ContractResponse::awardAmount))
                .map(contract -> List.of(new RiskSignalResponse(
                        "HIGH_VALUE_CONTRACT",
                        "HIGH",
                        35,
                        "Vendor has at least one contract above $200M.",
                        contract.awardId() + " is worth " + formatCurrency(contract.awardAmount()) + "."
                )))
                .orElse(List.of());
    }

    private String formatCurrency(BigDecimal amount) {
        return NumberFormat
                .getCurrencyInstance(Locale.US)
                .format(amount);
    }
}
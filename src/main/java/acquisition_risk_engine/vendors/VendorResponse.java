package acquisition_risk_engine.vendors;

import java.math.BigDecimal;

public record VendorResponse(
        Long id,
        String name,
        Long contractCount,
        BigDecimal totalAwardAmount
) {
}
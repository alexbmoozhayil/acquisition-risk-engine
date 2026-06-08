package acquisition_risk_engine.contracts;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractResponse(
        Long id,
        String awardId,
        String vendorName,
        String agencyName,
        BigDecimal awardAmount,
        LocalDate startDate,
        LocalDate endDate,
        String description
) {
}
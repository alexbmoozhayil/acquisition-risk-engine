package acquisition_risk_engine.ingestion;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UsaSpendingContractRecord(
        String awardId,
        String vendorName,
        String agencyName,
        BigDecimal awardAmount,
        LocalDate startDate,
        LocalDate endDate,
        String description
) {
}
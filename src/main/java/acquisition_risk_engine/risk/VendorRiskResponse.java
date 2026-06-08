package acquisition_risk_engine.risk;

import java.util.List;

public record VendorRiskResponse(
        Long vendorId,
        String vendorName,
        int overallRiskScore,
        String riskLevel,
        List<RiskSignalResponse> signals
) {
}
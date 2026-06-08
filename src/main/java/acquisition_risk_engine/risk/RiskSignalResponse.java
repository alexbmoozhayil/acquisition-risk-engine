package acquisition_risk_engine.risk;

public record RiskSignalResponse(
        String name,
        String severity,
        int scoreImpact,
        String explanation,
        String evidence
) {
}
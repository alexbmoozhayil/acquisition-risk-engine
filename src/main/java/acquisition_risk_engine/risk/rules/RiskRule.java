package acquisition_risk_engine.risk.rules;

import acquisition_risk_engine.contracts.ContractResponse;
import acquisition_risk_engine.risk.RiskSignalResponse;

import java.util.List;

public interface RiskRule {

    List<RiskSignalResponse> evaluate(List<ContractResponse> contracts);
}
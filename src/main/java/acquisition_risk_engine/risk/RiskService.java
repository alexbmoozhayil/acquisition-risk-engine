package acquisition_risk_engine.risk;

import acquisition_risk_engine.contracts.ContractRepository;
import acquisition_risk_engine.contracts.ContractResponse;
import acquisition_risk_engine.vendors.VendorRepository;
import acquisition_risk_engine.vendors.VendorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class RiskService {

    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("200000000");

    private final VendorRepository vendorRepository;
    private final ContractRepository contractRepository;

    public RiskService(
            VendorRepository vendorRepository,
            ContractRepository contractRepository
    ) {
        this.vendorRepository = vendorRepository;
        this.contractRepository = contractRepository;
    }

    public VendorRiskResponse calculateVendorRisk(Long vendorId) {
        VendorResponse vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vendor not found"
                ));

        List<ContractResponse> contracts = contractRepository.findByVendorId(vendorId);
        List<RiskSignalResponse> signals = new ArrayList<>();

        addHighValueContractSignal(contracts, signals);
        addMultipleContractsSignal(contracts, signals);
        addExpiredContractSignal(contracts, signals);
        addSparseDescriptionSignal(contracts, signals);

        int overallRiskScore = signals.stream()
                .mapToInt(RiskSignalResponse::scoreImpact)
                .sum();

        overallRiskScore = Math.min(overallRiskScore, 100);

        String riskLevel = determineRiskLevel(overallRiskScore);

        return new VendorRiskResponse(
                vendor.id(),
                vendor.name(),
                overallRiskScore,
                riskLevel,
                signals
        );
    }

    private void addHighValueContractSignal(
            List<ContractResponse> contracts,
            List<RiskSignalResponse> signals
    ) {
        contracts.stream()
                .filter(contract -> contract.awardAmount() != null)
                .filter(contract -> contract.awardAmount().compareTo(HIGH_VALUE_THRESHOLD) >= 0)
                .max(Comparator.comparing(ContractResponse::awardAmount))
                .ifPresent(contract -> signals.add(new RiskSignalResponse(
                        "HIGH_VALUE_CONTRACT",
                        "HIGH",
                        35,
                        "Vendor has at least one contract above $200M.",
                        contract.awardId() + " is worth " + formatCurrency(contract.awardAmount()) + "."
                )));
    }

    private void addMultipleContractsSignal(
            List<ContractResponse> contracts,
            List<RiskSignalResponse> signals
    ) {
        if (contracts.size() >= 2) {
            signals.add(new RiskSignalResponse(
                    "MULTIPLE_CONTRACTS",
                    "MEDIUM",
                    15,
                    "Vendor has multiple contracts in the database.",
                    "Vendor has " + contracts.size() + " contracts."
            ));
        }
    }

    private void addExpiredContractSignal(
            List<ContractResponse> contracts,
            List<RiskSignalResponse> signals
    ) {
        LocalDate today = LocalDate.now();

        contracts.stream()
                .filter(contract -> contract.endDate() != null)
                .filter(contract -> contract.endDate().isBefore(today))
                .findFirst()
                .ifPresent(contract -> signals.add(new RiskSignalResponse(
                        "ENDED_CONTRACT",
                        "MEDIUM",
                        20,
                        "Vendor has at least one contract with an end date in the past.",
                        contract.awardId() + " ended on " + contract.endDate() + "."
                )));
    }

    private void addSparseDescriptionSignal(
            List<ContractResponse> contracts,
            List<RiskSignalResponse> signals
    ) {
        contracts.stream()
                .filter(contract -> contract.description() == null
                        || contract.description().trim().split("\\s+").length < 6)
                .findFirst()
                .ifPresent(contract -> signals.add(new RiskSignalResponse(
                        "SPARSE_DESCRIPTION",
                        "LOW",
                        10,
                        "Vendor has at least one contract with a short description.",
                        contract.awardId() + " has a sparse description."
                )));
    }

    private String determineRiskLevel(int score) {
        if (score >= 70) {
            return "HIGH";
        }

        if (score >= 40) {
            return "MEDIUM";
        }

        return "LOW";
    }

    private String formatCurrency(BigDecimal amount) {
        return NumberFormat
                .getCurrencyInstance(Locale.US)
                .format(amount);
    }
}
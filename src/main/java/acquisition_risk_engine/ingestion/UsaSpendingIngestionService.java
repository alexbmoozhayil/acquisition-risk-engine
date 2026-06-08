package acquisition_risk_engine.ingestion;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsaSpendingIngestionService {

    private final UsaSpendingClient usaSpendingClient;
    private final ContractIngestionRepository contractIngestionRepository;

    public UsaSpendingIngestionService(
            UsaSpendingClient usaSpendingClient,
            ContractIngestionRepository contractIngestionRepository
    ) {
        this.usaSpendingClient = usaSpendingClient;
        this.contractIngestionRepository = contractIngestionRepository;
    }

    public UsaSpendingIngestionResult ingestDodContracts() {
        List<UsaSpendingContractRecord> contracts = usaSpendingClient.fetchDodContracts();

        for (UsaSpendingContractRecord contract : contracts) {
            contractIngestionRepository.save(contract);
        }

        return new UsaSpendingIngestionResult(
                contracts.size(),
                contracts.size()
        );
    }
}

package acquisition_risk_engine.ingestion;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsaSpendingIngestionController {

    private final UsaSpendingIngestionService ingestionService;

    public UsaSpendingIngestionController(UsaSpendingIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/ingest/usaspending")
    public UsaSpendingIngestionResult ingestUsaSpending() {
        return ingestionService.ingestDodContracts();
    }
}
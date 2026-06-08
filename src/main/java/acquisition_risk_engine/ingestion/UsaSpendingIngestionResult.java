package acquisition_risk_engine.ingestion;

public record UsaSpendingIngestionResult(
        int recordsFetched,
        int recordsSaved
) {
}
package acquisition_risk_engine.ingestion;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ContractIngestionRepository {

    private final JdbcTemplate jdbcTemplate;

    public ContractIngestionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(UsaSpendingContractRecord contract) {
        Long agencyId = upsertAgency(contract.agencyName());
        Long vendorId = upsertVendor(contract.vendorName());

        String sql = """
                INSERT INTO contracts (
                    award_id,
                    vendor_id,
                    agency_id,
                    award_amount,
                    start_date,
                    end_date,
                    description
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (award_id)
                DO UPDATE SET
                    vendor_id = EXCLUDED.vendor_id,
                    agency_id = EXCLUDED.agency_id,
                    award_amount = EXCLUDED.award_amount,
                    start_date = EXCLUDED.start_date,
                    end_date = EXCLUDED.end_date,
                    description = EXCLUDED.description
                """;

        jdbcTemplate.update(
                sql,
                contract.awardId(),
                vendorId,
                agencyId,
                contract.awardAmount(),
                contract.startDate(),
                contract.endDate(),
                contract.description()
        );
    }

    private Long upsertAgency(String agencyName) {
        String sql = """
                INSERT INTO agencies (name)
                VALUES (?)
                ON CONFLICT (name)
                DO UPDATE SET name = EXCLUDED.name
                RETURNING id
                """;

        return jdbcTemplate.queryForObject(sql, Long.class, agencyName);
    }

    private Long upsertVendor(String vendorName) {
        String sql = """
                INSERT INTO vendors (name)
                VALUES (?)
                ON CONFLICT (name)
                DO UPDATE SET name = EXCLUDED.name
                RETURNING id
                """;

        return jdbcTemplate.queryForObject(sql, Long.class, vendorName);
    }
}
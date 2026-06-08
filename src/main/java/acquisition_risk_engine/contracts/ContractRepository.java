package acquisition_risk_engine.contracts;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ContractRepository {

    private final JdbcTemplate jdbcTemplate;

    public ContractRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ContractResponse> findAll() {
        String sql = """
                SELECT
                    c.id,
                    c.award_id,
                    v.name AS vendor_name,
                    a.name AS agency_name,
                    c.award_amount,
                    c.start_date,
                    c.end_date,
                    c.description
                FROM contracts c
                JOIN vendors v ON c.vendor_id = v.id
                JOIN agencies a ON c.agency_id = a.id
                ORDER BY c.award_amount DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new ContractResponse(
                rs.getLong("id"),
                rs.getString("award_id"),
                rs.getString("vendor_name"),
                rs.getString("agency_name"),
                rs.getBigDecimal("award_amount"),
                rs.getObject("start_date", java.time.LocalDate.class),
                rs.getObject("end_date", java.time.LocalDate.class),
                rs.getString("description")
        ));
    }
}
package acquisition_risk_engine.vendors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class VendorRepository {

    private final JdbcTemplate jdbcTemplate;

    public VendorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<VendorResponse> findAll() {
        String sql = """
                SELECT
                    v.id,
                    v.name,
                    COUNT(c.id) AS contract_count,
                    COALESCE(SUM(c.award_amount), 0) AS total_award_amount
                FROM vendors v
                LEFT JOIN contracts c ON c.vendor_id = v.id
                GROUP BY v.id, v.name
                ORDER BY total_award_amount DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new VendorResponse(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getLong("contract_count"),
                rs.getBigDecimal("total_award_amount")
        ));
    }

    public Optional<VendorResponse> findById(Long vendorId) {
        String sql = """
                SELECT
                    v.id,
                    v.name,
                    COUNT(c.id) AS contract_count,
                    COALESCE(SUM(c.award_amount), 0) AS total_award_amount
                FROM vendors v
                LEFT JOIN contracts c ON c.vendor_id = v.id
                WHERE v.id = ?
                GROUP BY v.id, v.name
                """;

        List<VendorResponse> vendors = jdbcTemplate.query(sql, (rs, rowNum) -> new VendorResponse(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getLong("contract_count"),
                rs.getBigDecimal("total_award_amount")
        ), vendorId);

        return vendors.stream().findFirst();
    }
}
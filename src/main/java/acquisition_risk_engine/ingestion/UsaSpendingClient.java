package acquisition_risk_engine.ingestion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsaSpendingClient {

    private static final String USA_SPENDING_URL =
            "https://api.usaspending.gov/api/v2/search/spending_by_award/";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public UsaSpendingClient(ObjectMapper objectMapper) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
    }

    public List<UsaSpendingContractRecord> fetchDodContracts() {
        String requestBody = """
                {
                  "filters": {
                    "time_period": [
                      {
                        "start_date": "2024-01-01",
                        "end_date": "2024-12-31"
                      }
                    ],
                    "agencies": [
                      {
                        "type": "awarding",
                        "tier": "toptier",
                        "name": "Department of Defense"
                      }
                    ],
                    "award_type_codes": ["A", "B", "C", "D"]
                  },
                  "fields": [
                    "Award ID",
                    "Recipient Name",
                    "Award Amount",
                    "Awarding Agency",
                    "Start Date",
                    "End Date",
                    "Description"
                  ],
                  "page": 1,
                  "limit": 10,
                  "sort": "Award Amount",
                  "order": "desc"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(USA_SPENDING_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "USAspending API returned status " + response.statusCode()
                );
            }

            return parseContracts(response.body());
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Failed to call USAspending API",
                    exception
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "USAspending API call was interrupted",
                    exception
            );
        }
    }

    private List<UsaSpendingContractRecord> parseContracts(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode results = root.path("results");

        List<UsaSpendingContractRecord> contracts = new ArrayList<>();

        for (JsonNode result : results) {
            String awardId = text(result, "Award ID");
            String vendorName = text(result, "Recipient Name");
            String agencyName = text(result, "Awarding Agency");

            if (isBlank(awardId) || isBlank(vendorName) || isBlank(agencyName)) {
                continue;
            }

            contracts.add(new UsaSpendingContractRecord(
                    awardId,
                    vendorName,
                    agencyName,
                    decimal(result, "Award Amount"),
                    date(result, "Start Date"),
                    date(result, "End Date"),
                    text(result, "Description")
            ));
        }

        return contracts;
    }

    private String text(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);

        if (value == null || value.isNull()) {
            return null;
        }

        return value.asText();
    }

    private BigDecimal decimal(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);

        if (value == null || value.isNull() || value.asText().isBlank()) {
            return null;
        }

        return new BigDecimal(value.asText());
    }

    private LocalDate date(JsonNode node, String fieldName) {
        String value = text(node, fieldName);

        if (isBlank(value)) {
            return null;
        }

        return LocalDate.parse(value);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
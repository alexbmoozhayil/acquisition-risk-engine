package acquisition_risk_engine.risk;

import acquisition_risk_engine.contracts.ContractRepository;
import acquisition_risk_engine.contracts.ContractResponse;
import acquisition_risk_engine.vendors.VendorRepository;
import acquisition_risk_engine.vendors.VendorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RiskServiceTest {

    @Test
    void calculateVendorRisk_returnsHighRiskWhenMultipleSignalsApply() {
        VendorRepository vendorRepository = mock(VendorRepository.class);
        ContractRepository contractRepository = mock(ContractRepository.class);

        RiskService riskService = new RiskService(vendorRepository, contractRepository);

        VendorResponse vendor = new VendorResponse(
                2L,
                "KRATOS DEFENSE & SECURITY SOLUTIONS INC",
                2L,
                new BigDecimal("358082440.78")
        );

        List<ContractResponse> contracts = List.of(
                new ContractResponse(
                        2L,
                        "N0001923C0021",
                        "KRATOS DEFENSE & SECURITY SOLUTIONS INC",
                        "Department of Defense",
                        new BigDecimal("238798157.30"),
                        LocalDate.of(2023, 1, 1),
                        LocalDate.of(2028, 12, 31),
                        "Defense systems and services contract."
                ),
                new ContractResponse(
                        4L,
                        "FA867818C0002",
                        "KRATOS DEFENSE & SECURITY SOLUTIONS INC",
                        "Department of Defense",
                        new BigDecimal("119284283.48"),
                        LocalDate.of(2018, 1, 1),
                        LocalDate.of(2024, 12, 31),
                        "Aircraft and weapons systems support."
                )
        );

        when(vendorRepository.findById(2L)).thenReturn(Optional.of(vendor));
        when(contractRepository.findByVendorId(2L)).thenReturn(contracts);

        VendorRiskResponse response = riskService.calculateVendorRisk(2L);

        assertEquals(2L, response.vendorId());
        assertEquals("KRATOS DEFENSE & SECURITY SOLUTIONS INC", response.vendorName());
        assertEquals(80, response.overallRiskScore());
        assertEquals("HIGH", response.riskLevel());
        assertEquals(4, response.signals().size());

        assertTrue(response.signals().stream()
                .anyMatch(signal -> signal.name().equals("HIGH_VALUE_CONTRACT")));

        assertTrue(response.signals().stream()
                .anyMatch(signal -> signal.name().equals("MULTIPLE_CONTRACTS")));

        assertTrue(response.signals().stream()
                .anyMatch(signal -> signal.name().equals("ENDED_CONTRACT")));

        assertTrue(response.signals().stream()
                .anyMatch(signal -> signal.name().equals("SPARSE_DESCRIPTION")));

        verify(vendorRepository).findById(2L);
        verify(contractRepository).findByVendorId(2L);
    }

    @Test
    void calculateVendorRisk_returnsLowRiskWhenNoSignalsApply() {
        VendorRepository vendorRepository = mock(VendorRepository.class);
        ContractRepository contractRepository = mock(ContractRepository.class);

        RiskService riskService = new RiskService(vendorRepository, contractRepository);

        VendorResponse vendor = new VendorResponse(
                5L,
                "LOW RISK VENDOR",
                1L,
                new BigDecimal("5000000.00")
        );

        List<ContractResponse> contracts = List.of(
                new ContractResponse(
                        10L,
                        "LOW123",
                        "LOW RISK VENDOR",
                        "Department of Defense",
                        new BigDecimal("5000000.00"),
                        LocalDate.of(2025, 1, 1),
                        LocalDate.of(2030, 12, 31),
                        "This contract has a clear and detailed description."
                )
        );

        when(vendorRepository.findById(5L)).thenReturn(Optional.of(vendor));
        when(contractRepository.findByVendorId(5L)).thenReturn(contracts);

        VendorRiskResponse response = riskService.calculateVendorRisk(5L);

        assertEquals(5L, response.vendorId());
        assertEquals("LOW RISK VENDOR", response.vendorName());
        assertEquals(0, response.overallRiskScore());
        assertEquals("LOW", response.riskLevel());
        assertTrue(response.signals().isEmpty());

        verify(vendorRepository).findById(5L);
        verify(contractRepository).findByVendorId(5L);
    }

    @Test
    void calculateVendorRisk_throwsNotFoundWhenVendorDoesNotExist() {
        VendorRepository vendorRepository = mock(VendorRepository.class);
        ContractRepository contractRepository = mock(ContractRepository.class);

        RiskService riskService = new RiskService(vendorRepository, contractRepository);

        when(vendorRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> riskService.calculateVendorRisk(999L)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(vendorRepository).findById(999L);
        verifyNoInteractions(contractRepository);
    }
}
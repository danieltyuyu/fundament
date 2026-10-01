package com.forkdevs.driveos.platform.billing.infrastructure.outbound.facthub;

import com.forkdevs.driveos.platform.billing.application.outboundservices.FacthubGateway;
import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.VoucherType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FacthubGatewayImplTests {

    @Test
    void issueVoucherReturnsInvoiceIdOnSuccess() {
        var restTemplate = mock(org.springframework.web.client.RestTemplate.class);
        var gateway = new FacthubGatewayImpl("http://facthub-dummy-api.com", restTemplate);

        var invoiceId = UUID.randomUUID();
        var dummyInvoice = new FacthubIssueInvoiceResponse.InvoiceData(invoiceId, "B001", 1, "12345678", "Juan Perez", new BigDecimal("120.00"), "2026-09-04");
        var dummyResponse = new FacthubIssueInvoiceResponse(true, "Issued successfully", dummyInvoice, null);

        when(restTemplate.postForObject(eq("http://facthub-dummy-api.com"), any(), eq(FacthubIssueInvoiceResponse.class)))
                .thenReturn(dummyResponse);

        var items = List.of(new FacthubGateway.FacthubItem("Cambio de Aceite", 1, new BigDecimal("120.00")));
        Optional<UUID> result = gateway.issueVoucher("20123456789", VoucherType.RECEIPT, "DNI", "12345678", "Juan Perez", items);

        assertTrue(result.isPresent());
        assertEquals(invoiceId, result.get());
    }
}

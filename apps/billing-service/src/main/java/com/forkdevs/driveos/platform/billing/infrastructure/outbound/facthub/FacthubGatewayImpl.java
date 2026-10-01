package com.forkdevs.driveos.platform.billing.infrastructure.outbound.facthub;

import com.forkdevs.driveos.platform.billing.application.outboundservices.FacthubGateway;
import com.forkdevs.driveos.platform.billing.domain.model.aggregates.Quote;
import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.VoucherType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the FacthubGateway outbound service interface.
 * Acts as an Anti-Corruption Layer (ACL) client connecting to the external Facthub REST API.
 * This integration enables electronic voucher emission to SUNAT.
 */
@Service
public class FacthubGatewayImpl implements FacthubGateway {

    private final String facthubApiUrl;
    private final RestTemplate restTemplate;

    public FacthubGatewayImpl(
            @org.springframework.beans.factory.annotation.Value("${facthub.api.url}") String facthubApiUrl,
            RestTemplate restTemplate) {
        this.facthubApiUrl = facthubApiUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<UUID> issueVoucher(
            String issuerRuc, 
            VoucherType documentType, 
            String customerDocumentType, 
            String customerDocumentNumber, 
            String customerName, 
            List<FacthubGateway.FacthubItem> items
    ) {
        try {
            // Map FacthubItem list to Facthub API items DTO
            List<FacthubIssueInvoiceRequest.Item> apiItems = items.stream()
                    .map(item -> new FacthubIssueInvoiceRequest.Item(
                            item.description(),
                            item.quantity(),
                            item.unitPrice()
                    ))
                    .toList();

            var requestDto = new FacthubIssueInvoiceRequest(
                    issuerRuc,
                    documentType.name(),
                    customerDocumentType,
                    customerDocumentNumber,
                    customerName,
                    apiItems
            );

            System.out.println("=== ENVIANDO PETICION A FACTHUB (JSON) ===");
            try {
                var objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestDto);
                System.out.println(json);
            } catch (Exception e) {
                System.out.println("Error serializing requestDto: " + e.getMessage());
                System.out.println(requestDto);
            }
            System.out.println("==========================================");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<FacthubIssueInvoiceRequest> requestEntity = new HttpEntity<>(requestDto, headers);

            var response = restTemplate.postForObject(
                    facthubApiUrl, 
                    requestEntity, 
                    FacthubIssueInvoiceResponse.class
            );

            if (response != null && response.success() && response.invoice() != null) {
                return Optional.of(response.invoice().id());
            }

            return Optional.empty();
        } catch (RestClientException e) {
            System.err.println("Error calling Facthub Service: " + e.getMessage());
            return Optional.empty();
        }
    }
}

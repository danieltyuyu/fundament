package com.forkdevs.driveos.platform.gateway.interfaces.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Enumeration;

@RestController
public class GatewayRouterController {

    private final RestTemplate restTemplate;

    @Value("${services.iam.url:http://localhost:8081}")
    private String iamServiceUrl = "http://localhost:8081";

    @Value("${services.billing.url:http://localhost:8082}")
    private String billingServiceUrl = "http://localhost:8082";

    @Value("${services.iot.url:http://localhost:8083}")
    private String iotServiceUrl = "http://localhost:8083";

    @Value("${services.operations.url:http://localhost:8084}")
    private String operationsServiceUrl = "http://localhost:8084";

    @Value("${services.inventory.url:http://localhost:8085}")
    private String inventoryServiceUrl = "http://localhost:8085";

    @Value("${services.core.url:http://localhost:8086}")
    private String coreServiceUrl = "http://localhost:8086";

    @Value("${services.fleet.url:http://localhost:8087}")
    private String fleetServiceUrl = "http://localhost:8087";

    public GatewayRouterController() {
        this.restTemplate = new RestTemplate();
    }

    @RequestMapping("/api/v1/**")
    public ResponseEntity<byte[]> routeRequest(HttpServletRequest request, byte[] body) {
        String path = request.getRequestURI();
        String targetBaseUrl = resolveTargetUrl(path);

        if (targetBaseUrl == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";
        URI targetUri = URI.create(targetBaseUrl + path + queryString);

        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames != null && headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (!headerName.equalsIgnoreCase(HttpHeaders.HOST)) {
                headers.add(headerName, request.getHeader(headerName));
            }
        }

        HttpEntity<byte[]> httpEntity = new HttpEntity<>(body, headers);

        try {
            return restTemplate.exchange(
                    targetUri,
                    HttpMethod.valueOf(request.getMethod()),
                    httpEntity,
                    byte[].class
            );
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .headers(ex.getResponseHeaders())
                    .body(ex.getResponseBodyAsByteArray());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

    public String resolveTargetUrl(String path) {
        if (path.startsWith("/api/v1/authentication") || path.startsWith("/api/v1/iam")) {
            return iamServiceUrl;
        } else if (path.startsWith("/api/v1/quotes") || path.startsWith("/api/v1/vouchers")
                || path.startsWith("/api/v1/checkouts") || path.startsWith("/api/v1/payments")) {
            return billingServiceUrl;
        } else if (path.startsWith("/api/v1/obd2-devices") || path.startsWith("/api/v1/telemetry-batches")
                || path.startsWith("/api/v1/dtc-alerts") || path.startsWith("/api/v1/vehicles")) {
            return iotServiceUrl;
        } else if (path.startsWith("/api/v1/work-orders") || path.startsWith("/api/v1/services")) {
            return operationsServiceUrl;
        } else if (path.startsWith("/api/v1/products")) {
            return inventoryServiceUrl;
        } else if (path.startsWith("/api/v1/workshops") || path.startsWith("/api/v1/branches")
                || path.startsWith("/api/v1/owners") || path.startsWith("/api/v1/customers")
                || path.startsWith("/api/v1/employees") || path.startsWith("/api/v1/profiles")) {
            return coreServiceUrl;
        } else if (path.startsWith("/api/v1/appointments") || path.startsWith("/api/v1/customer-registrations")
                || path.startsWith("/api/v1/employee-registrations")) {
            return fleetServiceUrl;
        }
        return null;
    }
}

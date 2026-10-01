package com.forkdevs.driveos.platform.gateway.interfaces.rest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GatewayRouterControllerTests {

    @Test
    void resolveTargetUrl_RoutesCorrectlyToMicroservices() {
        GatewayRouterController router = new GatewayRouterController();

        assertNotNull(router.resolveTargetUrl("/api/v1/authentication/sign-in"));
        assertNotNull(router.resolveTargetUrl("/api/v1/quotes"));
        assertNotNull(router.resolveTargetUrl("/api/v1/telemetry-batches"));
        assertNotNull(router.resolveTargetUrl("/api/v1/work-orders"));
        assertNotNull(router.resolveTargetUrl("/api/v1/products"));
        assertNotNull(router.resolveTargetUrl("/api/v1/workshops"));
        assertNotNull(router.resolveTargetUrl("/api/v1/appointments"));
        assertNull(router.resolveTargetUrl("/api/v1/unknown-endpoint"));
    }
}

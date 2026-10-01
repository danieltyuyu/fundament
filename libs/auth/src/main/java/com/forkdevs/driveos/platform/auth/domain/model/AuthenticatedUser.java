package com.forkdevs.driveos.platform.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUser {
    private UUID id;
    private String username;
    private String email;
    private List<String> roles;
    private Long branchId;
}

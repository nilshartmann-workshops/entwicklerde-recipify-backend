package nh.recipify.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum ApprovalStatus {
    PENDING,
    APPROVED,
    REJECTED
}

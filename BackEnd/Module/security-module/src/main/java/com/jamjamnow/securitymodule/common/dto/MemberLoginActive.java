package com.jamjamnow.securitymodule.common.dto;

import com.jamjamnow.securitymodule.common.enums.SecurityRole;
import lombok.Builder;

@Builder
public record MemberLoginActive(
    Long id,
    SecurityRole role
) {

}

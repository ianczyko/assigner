package com.anczykowski.assigner.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class VerifyRequest {
    @NotBlank
    String verifier;
}

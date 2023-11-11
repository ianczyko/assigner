package com.anczykowski.assigner.teams.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TeamAccessTokenDto {
    Integer accessToken;
    LocalDateTime accessTokenExpirationDate;
}

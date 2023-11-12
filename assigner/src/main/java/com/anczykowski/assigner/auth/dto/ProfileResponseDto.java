package com.anczykowski.assigner.auth.dto;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ProfileResponseDto {
    String id;

    String first_name;

    String middle_names;

    String last_name;

    Integer student_status;

    Integer staff_status;
}

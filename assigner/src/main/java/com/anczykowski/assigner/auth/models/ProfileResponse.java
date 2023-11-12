package com.anczykowski.assigner.auth.models;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ProfileResponse {
    String id;

    String first_name;

    String middle_names;

    String last_name;

    Integer student_status;

    Integer staff_status;
}

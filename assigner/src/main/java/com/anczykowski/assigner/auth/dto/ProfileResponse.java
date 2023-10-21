package com.anczykowski.assigner.auth.dto;

import lombok.Data;

@Data
public class ProfileResponse {
    String id;

    String first_name;

    String middle_names;

    String last_name;

    Integer student_status;

    Integer staff_status;
}

package com.anczykowski.assigner.projects.dto;

import com.anczykowski.assigner.users.dto.UserSimpleDto;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ProjectForumCommentDto {

    Integer id;

    @NotBlank
    String content;

    Date createdDate;

    UserSimpleDto author;

}

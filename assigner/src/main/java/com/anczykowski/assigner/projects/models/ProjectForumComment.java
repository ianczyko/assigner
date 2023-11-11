package com.anczykowski.assigner.projects.models;

import com.anczykowski.assigner.users.models.User;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public final class ProjectForumComment {
    private Integer id;
    private String content;
    private Date createdDate;
    private Project project;
    private User author;
}

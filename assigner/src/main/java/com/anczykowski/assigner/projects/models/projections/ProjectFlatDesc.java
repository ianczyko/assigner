package com.anczykowski.assigner.projects.models.projections;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProjectFlatDesc {
    private Integer id;
    private String name;
    private String description;
    private Integer teamLimit;
    private String projectManager;
}

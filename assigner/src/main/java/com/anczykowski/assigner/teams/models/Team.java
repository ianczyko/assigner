package com.anczykowski.assigner.teams.models;

import com.anczykowski.assigner.courses.models.CourseEdition;
import com.anczykowski.assigner.projects.models.Project;
import com.anczykowski.assigner.users.models.User;
import com.google.common.math.IntMath;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"members", "preferences"})
@ToString(exclude = {"members", "preferences"})
public final class Team {
    private Integer id;
    private String name;
    private CourseEdition courseEdition;
    private Integer accessToken;
    private LocalDateTime accessTokenExpirationDate;
    private Project assignedProject;
    private User leader;
    @Builder.Default
    private Set<User> members = new HashSet<>();
    @Builder.Default
    private List<ProjectPreference> preferences = new ArrayList<>();


    public void regenerateAccessToken(Integer tokenDigits, Integer validDays) {
        var origin = IntMath.pow(10, tokenDigits - 1);
        accessToken = ThreadLocalRandom.current().nextInt(origin, origin * 10);
        accessTokenExpirationDate = LocalDateTime.now().plusDays(validDays);
    }

    public void addMember(User member) {
        members.add(member);
    }
}

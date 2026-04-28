package com.Hoseo.CapstoneDesign.project.event;

import java.util.List;

public record ProjectMembershipChangedEvent(
        Long projectId,
        List<Long> userIds
) {
}

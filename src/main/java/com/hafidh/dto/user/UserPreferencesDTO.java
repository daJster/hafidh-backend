package com.hafidh.dto.user;

import com.hafidh.entity.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserPreferencesDTO {
    private Long id;
    private User user;
    private String language;
    private String theme;
    private Boolean emailNotifications;
    private Boolean pushNotifications;
    private Boolean classroomUpdates;
    private Boolean parentalNotifications;
    private String dateFormat;
    private String timeFormat;
}

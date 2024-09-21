package com.friendly.commons.models.reports;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum UserActivityType {

    USER_LOGIN("User_login", "User login"),
    USER_LOGOUT("User_logout", "User logout"),
    CONFIGURING_EMAIL("Configuring_email", "Configuring email"),
    CONFIGURING_DATABASE("Configuring_database", "Configuring database"),
    CONFIGURING_ALERTS("Configuring_alerts", "Configuring alerts"),
    CONFIGURING_SNMP("Configuring_snmp", "Configuring snmp"),
    CONFIGURING_NOTIFICATIONS("Configuring_notifications", "Configuring notifications"),
    CREATE_NEW_USER("Create_new_user", "Create a new user"),
    DELETE_USER("Delete_user", "Delete user"),
    EDIT_USER_SETTING("Edit_user_setting", "Edit user setting"),
    ADD_USER_GROUP("Add_user_group", "Add user group"),
    DELETE_USER_GROUP("Delete_user_group", "Delete user group"),
    EDIT_USER_GROUP("Edit_user_group", "Edit user group"),
    FORGOT_PASSWORD("Forgot_password", "Forgot password"),
    PASSWORD_CHANGE("Password_change", "Password change");

    private static final UserActivityType[] ALL_USER_ACTIVITY_TYPES = UserActivityType.values();

    @JsonValue
    private final String name;
    private final String description;

    UserActivityType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static UserActivityType byName(String name) {
        if(StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("UserActivityType cannot be null");
        }

        Optional<UserActivityType> userActivityType = Arrays.stream(ALL_USER_ACTIVITY_TYPES)
                .filter(t -> name.equalsIgnoreCase(t.getName()))
                .findFirst();

        return userActivityType.orElseThrow(
                () -> new IllegalArgumentException("No matching enum constant for the name: " + name));
    }
}

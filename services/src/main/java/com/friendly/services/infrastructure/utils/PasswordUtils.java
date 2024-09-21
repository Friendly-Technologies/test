package com.friendly.services.infrastructure.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collections;

public class PasswordUtils {

    public static String getHiddenPassword(final String password) {
        return String.join("", Collections.nCopies(password.length(), "*"));
    }
}

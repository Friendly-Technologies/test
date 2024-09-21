package com.friendly.commons.models.settings.bootstrap;

import lombok.Getter;

public enum SecurityType {

        PSK(0),
        PUBLIC_KEY(1),
        X509(2),
        NO_SEC(3);

        @Getter
        private int value;

        SecurityType(int value) {
            this.value = value;
        }

        public static SecurityType fromValue(int value) {
            for (SecurityType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            throw new IllegalArgumentException("There is no security type with the given identifier " + value);
        }
}

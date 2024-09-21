package com.friendly.services.infrastructure.config.jpa;

import com.friendly.commons.models.auth.ClientType;

public class DBContextHolder {
    private static final ThreadLocal<ClientType> contextHolder = new ThreadLocal<>();

    public static void setCurrentDb(ClientType clientType) {
        contextHolder.set(clientType);
    }

    public static ClientType getCurrentDb() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }
}

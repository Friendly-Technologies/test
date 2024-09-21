package com.friendly.commons.models.auth;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ClientType {
    sc(-1L), //Support center
    mc(-2L), //Management center
    def(0);

    public final long templateId;

}

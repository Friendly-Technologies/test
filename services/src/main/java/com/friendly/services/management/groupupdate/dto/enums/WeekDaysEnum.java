package com.friendly.services.management.groupupdate.dto.enums;

public enum WeekDaysEnum {
    Su,
    Mo,
    Tu,
    We,
    Th,
    Fr,
    Sa;

    public static WeekDaysEnum fromInt(int day) {
        for (WeekDaysEnum e : WeekDaysEnum.values()) {
            if (e.ordinal() == day) {
                return e;
            }
        }
        return Mo;
    }
}
package com.friendly.services.management.profiles.entity;

import lombok.Data;

import java.util.List;

@Data
public class GetDeviceProfilesBody {
    private String manufacturer;
    private String model;
    private ProfileStatus profileStatus;
    private List<Integer> pageNumbers;
    private Integer pageSize;
}

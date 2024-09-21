package com.friendly.services.infrastructure.utils.websocket.model;

import com.friendly.services.infrastructure.utils.websocket.model.base.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by alexandr.kaygorodov (12.11.2020)
 * */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoUpdateEvent implements Event {

    private String serial;
    private String loginName;
    private String name;
    private String telephone;
    private String creator;
    private String updator;
    private String zip;
    private String location;
    private String userTag;
    private String userStatus;
    private String userId;

    private String cust1;
    private String cust2;
    private String cust3;
    private String cust4;
    private String cust5;
    private String cust6;
    private String cust7;
    private String cust8;
    private String cust9;
    private String cust10;

}

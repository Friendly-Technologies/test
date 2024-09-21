package com.friendly.commons.models.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.io.Serializable;

/**
 * Model that represents API version of Subscriber Info
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo implements Serializable {

    private Integer domainId;
    private String domainName;

    private String userLogin;
    private String userName;
    private String phone;
    private String zip;
    private String userLocation;
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
    private String cust11;
    private String cust12;
    private String cust13;
    private String cust14;
    private String cust15;
    private String cust16;
    private String cust17;
    private String cust18;
    private String cust19;
    private String cust20;
    @JsonInclude
    private Double latitude;
    @JsonInclude
    private Double longitude;

    public static AccountInfo createEmpty(final Integer domainId, final String domainName) {
        return AccountInfo.builder()
                .domainId(domainId)
                .domainName(domainName)
                .userLogin(Strings.EMPTY)
                .userName(Strings.EMPTY)
                .phone(Strings.EMPTY)
                .zip(Strings.EMPTY)
                .userLocation(Strings.EMPTY)
                .userTag(Strings.EMPTY)
                .userStatus(Strings.EMPTY)
                .userId(Strings.EMPTY)
                .cust1(Strings.EMPTY)
                .cust2(Strings.EMPTY)
                .cust3(Strings.EMPTY)
                .cust4(Strings.EMPTY)
                .cust5(Strings.EMPTY)
                .cust6(Strings.EMPTY)
                .cust7(Strings.EMPTY)
                .cust8(Strings.EMPTY)
                .cust9(Strings.EMPTY)
                .cust10(Strings.EMPTY)
                .cust11(Strings.EMPTY)
                .cust12(Strings.EMPTY)
                .cust13(Strings.EMPTY)
                .cust14(Strings.EMPTY)
                .cust15(Strings.EMPTY)
                .cust16(Strings.EMPTY)
                .cust17(Strings.EMPTY)
                .cust18(Strings.EMPTY)
                .cust19(Strings.EMPTY)
                .cust20(Strings.EMPTY)
                .latitude(null)
                .longitude(null)
                .build();
    }

}

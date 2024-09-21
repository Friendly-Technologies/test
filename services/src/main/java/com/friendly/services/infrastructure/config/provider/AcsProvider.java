package com.friendly.services.infrastructure.config.provider;

import com.friendly.commons.models.auth.ClientType;
import com.ftacs.ACSWebService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@DependsOn("acsConfig")
@RequiredArgsConstructor
public class AcsProvider {
    private final  ACSWebService acsWebService;

    private static ACSWebService acsWebServiceSt;

    @PostConstruct
    public void init() {
        acsWebServiceSt = acsWebService;
    }
    public static ACSWebService getAcsWebService(ClientType clientType) {
        return acsWebServiceSt;
    }
}

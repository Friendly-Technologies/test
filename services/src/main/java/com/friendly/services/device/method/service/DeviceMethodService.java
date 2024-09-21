package com.friendly.services.device.method.service;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.device.method.DeviceMethodSResponse;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.device.method.orm.acs.repository.CpeMethodRepository;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.DEVICE_NOT_FOUND;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.FIELD_CAN_NOT_BE_EMPTY;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Service
public class DeviceMethodService {
    CpeMethodRepository cpeMethodRepository;
    CpeRepository cpeRepository;
    JwtService jwtService;

    public DeviceMethodSResponse getDeviceMethods(Long deviceId, String token){
        jwtService.getSession(token);
        if(deviceId == null){
            throw new FriendlyIllegalArgumentException(FIELD_CAN_NOT_BE_EMPTY, "deviceId");
        }
        if (!cpeRepository.findById(deviceId).isPresent()){
            throw new FriendlyEntityNotFoundException(DEVICE_NOT_FOUND, deviceId);
        }
        List<String> methodNames = cpeMethodRepository.findMethodNamesByCpeId(deviceId).stream()
                .map(s -> s.replaceAll(" ", ""))
                .collect(Collectors.toList());
        return new DeviceMethodSResponse(methodNames);
    }

}
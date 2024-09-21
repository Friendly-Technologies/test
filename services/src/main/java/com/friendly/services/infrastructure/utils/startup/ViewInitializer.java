package com.friendly.services.infrastructure.utils.startup;

import com.friendly.services.uiservices.frame.orm.iotw.model.FrameEntity;
import com.friendly.services.uiservices.frame.orm.iotw.repository.FrameRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewInitializer {

    @NonNull
    private final FrameRepository frameRepository;

    @PostConstruct
    public void init() {
        fillFrameFrames();
    }

    private void fillFrameFrames() {
        fillFrames(Arrays.asList(
                "Device status", "Wireless status", "Device resources", "Tools", "Diagnostics",
                "Connection troubleshooter", "Ports", "Device history", "Device log", "Configuration backup/restore",
                "LAN password", "Neighboring Wi-Fi diagnostics", "WAN", "DHCP parameters", "Network map",
                "Software upgrade", "Mesh", "Mesh network info", "Mesh hosts", "Mesh diagnostics", "MAC filtering",
                "WAN/LAN ports status", "Neighbor network status", "Device environment","Mobile environment", "Summary environment",
                "Hardware performance", "Internet connectivity", "Network score", "Software manager",
                "Wi-Fi Clients", "Wi-Fi Mesh", "Wi-Fi Quality"));
    }

    private void fillFrames(final List<String> names) {
        final List<String> framesInDb = frameRepository.findAllByNameInAndIsDefaultTrue(names)
                .stream()
                .map(FrameEntity::getName)
                .collect(Collectors.toList());

        final List<FrameEntity> frames = names.stream()
                .filter(name -> !framesInDb.contains(name))
                .map(name -> FrameEntity.builder()
                        .name(name)
                        .isDefault(true)
                        .build())
                .collect(Collectors.toList());
        frameRepository.saveAll(frames);
    }

}

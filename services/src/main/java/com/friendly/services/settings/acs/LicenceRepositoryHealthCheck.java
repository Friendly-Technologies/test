package com.friendly.services.settings.acs;

import com.friendly.services.settings.acs.orm.acs.repository.LicenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LicenceRepositoryHealthCheck {

    public final LicenceRepository licenceRepository;

    public LicenceRepositoryHealthCheck(LicenceRepository licenceRepository) {
        this.licenceRepository = licenceRepository;
    }

    public boolean isHealth() {
        try {
            licenceRepository.isHealth();
            return true;
        } catch (Exception e) {
            log.error("Error connect to database healthCheck using", e);
            return false;
        }
    }
}

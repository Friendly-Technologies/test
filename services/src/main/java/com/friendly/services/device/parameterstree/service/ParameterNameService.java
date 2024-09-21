package com.friendly.services.device.parameterstree.service;

import com.friendly.commons.cache.CpeParameterNameCache;
import com.friendly.services.device.parameterstree.orm.acs.model.CpeParameterNameEntity;
import com.friendly.services.device.parameterstree.orm.acs.model.projections.ParameterNameIdTypeProjection;
import com.friendly.services.device.parameterstree.orm.acs.repository.DeviceParameterNameRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParameterNameService implements InitializingBean {
    final CpeParameterNameCache cpeParameterNameCache;
    final DeviceParameterNameRepository deviceParameterNameRepository;
    @Getter
    public static List<Long> nameIdsForCpeProps;

    public Long getIdByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        Integer id = cpeParameterNameCache.getIdByName(name);
        if (id != null) {
            return Long.valueOf(id);
        }
        Optional<CpeParameterNameEntity> opEnt = deviceParameterNameRepository.findByName(name);
        if (opEnt.isPresent()) {
            putEntityToCache(opEnt.get());
            return Long.valueOf(cpeParameterNameCache.getIdByName(name));
        }
        return null;
    }

    public void putEntityToCache(CpeParameterNameEntity e) {
        cpeParameterNameCache.putObjToCache(e.getId().intValue(), e.getName(), e.getType(), e.getEncrypted());
    }

    public String getNameById(Integer nameId) {
        return getNameById(Long.valueOf(nameId));
    }

    public String getNameById(Long nameId) {
        if (nameId == null) {
            return null;
        }
        String name = cpeParameterNameCache.getNameById(nameId.intValue());
        if (!StringUtils.isEmpty(name)) {
            return name;
        }
        log.info(" getNameById NAME_ID {} is unknown", nameId);
        Optional<CpeParameterNameEntity> opEnt = deviceParameterNameRepository.findById(nameId);
        if (opEnt.isPresent()) {
            putEntityToCache(opEnt.get());
            return cpeParameterNameCache.getNameById(nameId.intValue());
        }
        return null;
    }

    public Map<Long, String> getNamesByIds(List<Long> ids) {
        Map<Long, String> result = new HashMap<>();
        List<Long> idsToFetch = new ArrayList<>();

        for (Long id : ids) {
            String name = cpeParameterNameCache.getNameById(id.intValue());
            if (!StringUtils.isEmpty(name)) {
                result.put(id, name);
            } else {
                idsToFetch.add(id);
            }
        }

        if (!idsToFetch.isEmpty()) {
            log.info("getNamesByIds NAME_ID {} is unknown", org.springframework.util.StringUtils.collectionToCommaDelimitedString(idsToFetch));
            List<CpeParameterNameEntity> entities = deviceParameterNameRepository.findAllById(idsToFetch);
            for (CpeParameterNameEntity entity : entities) {
                putEntityToCache(entity);
                result.put(entity.getId(), entity.getName());
            }
        }

        return result;
    }

    public CpeParameterNameEntity findFirstByNameLike(String name) {
        return deviceParameterNameRepository.findFirstByNameLike(name).orElse(null);
    }

    public String getTypeById(Long nameId) {
        if (nameId == null) {
            return null;
        }
        String type = cpeParameterNameCache.getType(nameId.intValue());
        if (!StringUtils.isEmpty(type)) {
            return type;
        }
        if (!cpeParameterNameCache.isCachedType(nameId.intValue())) {
            log.info("getTypeById NAME_ID {} is unknown", nameId);
            Optional<CpeParameterNameEntity> opEnt = deviceParameterNameRepository.findById(nameId);
            if (opEnt.isPresent()) {
                putEntityToCache(opEnt.get());
                return cpeParameterNameCache.getType(nameId.intValue());
            }
        }
        return null;
    }

    public List<Long> getIdsByNames(List<String> names) {
        return names.stream().map(this::getIdByName).collect(Collectors.toList());
    }

    public List<String> getMethodNames() {
        return deviceParameterNameRepository.getMethodNames();
    }

    public List<ParameterNameIdTypeProjection> getIdNameTypeByNameMask(String name) {
        return deviceParameterNameRepository.findAllByNameLike(name);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (cpeParameterNameCache.isEmpty()) {
            deviceParameterNameRepository.findAll().forEach(this::putEntityToCache);
        }
    }

    public List<CpeParameterNameEntity> findAll(Specification<CpeParameterNameEntity> spec) {
        return deviceParameterNameRepository.findAll(spec);
    }
}
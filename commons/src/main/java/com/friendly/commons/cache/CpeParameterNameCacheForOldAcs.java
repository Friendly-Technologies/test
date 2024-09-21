package com.friendly.commons.cache;

import com.hazelcast.collection.ISet;
import com.hazelcast.map.IMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

//@Component("CpeParameterNameCache")
@RequiredArgsConstructor
public class CpeParameterNameCacheForOldAcs {

    private static final Logger log = LoggerFactory.getLogger(CpeParameterNameCacheForOldAcs.class);

    public static final String TYPE_BASE64 = "base64";
    public static final String TYPE_STRING = "string";
    public static final String TYPE_OBJECT = "object";
    public static final String TYPE_INT = "int";
    public static final String TYPE_UNSIGNED_INT = "unsignedInt";
    public static final String TYPE_DATE_TIME = "dateTime";
    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_OPAQUE = "opaque";

    public static final List<String> TYPES = new ArrayList<String>();

    static {
        TYPES.add(TYPE_BASE64);
        TYPES.add(TYPE_STRING);
        TYPES.add(TYPE_OBJECT);
        TYPES.add(TYPE_INT);
        TYPES.add(TYPE_UNSIGNED_INT);
        TYPES.add(TYPE_DATE_TIME);
        TYPES.add(TYPE_BOOLEAN);
        TYPES.add(TYPE_OPAQUE);
    }

    private IMap<String, Integer> nameIdCache;
    private IMap<Integer, String> idNameCache;
    private IMap<Integer, String> idTypeCache;
    private ISet<Integer> idEncryptedCache;
    final ExecutorService exec = Executors.newFixedThreadPool(10);

    @NonNull
    final HazelcastCacheFactory cacheFactory;

    static CpeParameterNameCacheForOldAcs cpeParameterNameCache;

    @PostConstruct
    public void afterPropertiesSet() {
        idEncryptedCache = cacheFactory.getHazelcastInstance().getSet("CpeParameterIdEncryptedCache");
        nameIdCache = cacheFactory.getCache("CpeParameterNameIdCache");
        idNameCache = cacheFactory.getCache("CpeParameterIdNameCache");
        idTypeCache = cacheFactory.getCache("CpeParameterIdTypeCache");

        if (idNameCache.isEmpty()) {
            log.info("Cache for CpeParameterName is empty.");
        } else {
            log.info("Cache for CpeParameterName is already filled.");
        }
        log.info("Cache for CpeParameterName size {}", idNameCache.size());
        cpeParameterNameCache = this;
    }

    public static void initCaches() {
        cpeParameterNameCache.afterPropertiesSet();
    }

    public Integer getIdByName(String name) {
        return nameIdCache.get(name);
    }

    public String getNameById(Integer id) {
        return idNameCache.get(id);
    }

    public Set<String> getAllNames() {
        return nameIdCache.keySet();
    }

    public Integer putObjToCache(Integer id, String name, String type, Boolean encrypted) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        nameIdCache.set(name, id);
        idNameCache.set(id, name);
        if (type != null) {
            idTypeCache.set(id, type);
        }
        if (encrypted != null && encrypted) {
            log.info("Parameter: " + name + " is encrypted");
            idEncryptedCache.add(id);
        }
        return id;
    }

    public Set<String> getNamesFromIds(Collection<Integer> ids) {
        return ids.stream().map(this::getNameById).collect(Collectors.toSet());
    }

    public Set<Integer> getIdsFromNames(Set<String> names) {
        return names.stream().map(this::getIdByName).collect(Collectors.toSet());
    }

    public boolean isParameterEncrypted(Integer nameId, String name) {
        if (idEncryptedCache.isEmpty()) {
            return false;
        }

        nameId = nameId == null ? getIdByName(name) : nameId;
        if (nameId == null) {
            return false;
        }
        if (isEncrypted(nameId)) {
            log.info("parameter: " + name + " encrypted");
            return true;
        }
        for (Integer id : idEncryptedCache) {
            String n = getNameById(id);
            if (n != null && n.endsWith(".") && name.startsWith(n)) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getEncryptedNames() {
        if (idEncryptedCache == null || idEncryptedCache.isEmpty()) {
            return Collections.emptySet();
        }
        return getNamesFromIds(idEncryptedCache);
    }

    private boolean isEncrypted(Integer nameId, String parameterName) {
        boolean isParameterEncrypted = false;
        if (!idEncryptedCache.isEmpty()) { //Is it correct to have empty cache if encryption disabled? See method isParameterEncrypted
            isParameterEncrypted = isEncrypted(nameId);
            if (!isParameterEncrypted) {
                for (Integer id : idEncryptedCache) {
                    String n = getNameById(id);
                    if (n != null && n.endsWith(".") && parameterName.startsWith(n)) {
                        isParameterEncrypted = true;
                        break;
                    }
                }
            }
        }
        return isParameterEncrypted;
    }

    public boolean isEncrypted(Integer nameId) {
        return idEncryptedCache.contains(nameId);
    }

    public String getType(Integer nameId) {
        return idTypeCache.get(nameId);
    }

    public boolean isCachedType(Integer nameId) {
        return idTypeCache.containsKey(nameId);
    }


    public boolean isCached(String name) {
        return nameIdCache.containsKey(name);
    }

    public String getType(String parameterName) {
        Integer parameterNameId = getIdByName(parameterName);
        return parameterNameId == null ? null : getType(parameterNameId);
    }

    public boolean isEmpty() {
        return idNameCache.isEmpty();
    }

    public static void printStatsAndReset() {
    }
}

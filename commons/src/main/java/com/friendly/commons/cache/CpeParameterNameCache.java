package com.friendly.commons.cache;

import com.hazelcast.collection.ISet;
import com.hazelcast.map.IMap;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component("CpeParameterNameCache")
@RequiredArgsConstructor
public class CpeParameterNameCache {

    private static final Logger log = LoggerFactory.getLogger(CpeParameterNameCache.class);

    public static final String TYPE_BASE64 = "base64";
    public static final String TYPE_STRING = "string";
    public static final String TYPE_OBJECT = "object";
    public static final String TYPE_INT = "int";
    public static final String TYPE_UNSIGNED_INT = "unsignedInt";
    public static final String TYPE_DATE_TIME = "dateTime";
    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_OPAQUE = "opaque";

    public static final List<String> TYPES = new ArrayList<>();
    public static final String REGEX = "\\.(\\d+)\\.";
    public static final Pattern PATTERN = Pattern.compile(REGEX);

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

    private IMap<Integer, Integer> idToMaskId;
    private IMap<Integer, Map<Integer, List<Integer>>> maskIdToIdToInstances;
    private IMap<Integer, Map<List<Integer>, Integer>> maskIdToInstancesToId;
    private ISet<Integer> idEncryptedCache;
    final ExecutorService exec = Executors.newFixedThreadPool(10);

    @NonNull
    final HazelcastCacheFactory cacheFactory;

    static CpeParameterNameCache cpeParameterNameCache;

    @PostConstruct
    public void afterPropertiesSet() {
        idEncryptedCache = cacheFactory.getHazelcastInstance().getSet("CpeParameterIdEncryptedCache");
        nameIdCache = cacheFactory.getCache("CpeParameterNameIdCache");
        idNameCache = cacheFactory.getCache("CpeParameterIdNameCache");
        idTypeCache = cacheFactory.getCache("CpeParameterIdTypeCache");
        idToMaskId =  cacheFactory.getCache("CpeParameterIdToMaskIdCache");
        maskIdToIdToInstances = cacheFactory.getCache("CpeParameterMaskIdToIdToInstances");
        maskIdToInstancesToId = cacheFactory.getCache("CpeParameterMaskIdToInstancesToId");

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

    static long getIdByName = 0;

    public Integer getIdByName(String name) {
        long t = System.currentTimeMillis();
        if (nameIdCache.containsKey(name)) {
            return nameIdCache.get(name);
        }
        try {
            String mask = getMaskByName(name);
            Integer maskId = nameIdCache.get(mask);

            if (maskId != null) {
                if (mask.contains("*")) {
                    List<Integer> instance = getInstanceNumbersFromName(name);
                    if (!instance.isEmpty()) {
                        Map<List<Integer>, Integer> map = maskIdToInstancesToId.get(maskId);
                        Integer id = map.get(instance);
                        if (id != null) {
                            return id;
                        }
                    }
                } else {
                    return maskId;
                }
            }
            return null;
        } finally {
            getIdByName += (System.currentTimeMillis() - t);
        }
    }

    static long getNameById = 0;

    public String getNameById(Integer id) {
        long t = System.currentTimeMillis();
        try {
            String name = idNameCache.get(id);
            if (name != null && !name.contains(".*.")) {
                return name;
            }

            Map<Integer, List<Integer>> maskIdAndInstance = getMaskIdAndInstanceByNameId(id);
            if (maskIdAndInstance != null && !maskIdAndInstance.isEmpty()) {
                Map.Entry<Integer, List<Integer>> entry = maskIdAndInstance.entrySet().iterator().next();
                String mask = idNameCache.get(entry.getKey());

                if (mask != null) {
                    List<Integer> numbers = entry.getValue();
                    if (numbers != null && !numbers.isEmpty() && mask.contains("*")) {
                        return replaceStars(mask, numbers);
                    }
                    return mask;
                }
            }
            return null;
        } finally {
            getNameById += (System.currentTimeMillis() - t);
        }
    }

    // TODO: remove method usage, we need to prevent getting all collection members
    public Set<String> getAllNames() {
        return nameIdCache.keySet();
    }

    static long putObjToCache = 0;

    public Integer putObjToCache(Integer id, String name, String type, Boolean encrypted) {
        long t = System.currentTimeMillis();
        try {
            if (StringUtils.isEmpty(name)) {
                return null;
            }
            String mask = getMaskByName(name);
            boolean isInstanceParameter = mask.contains("*");

            Integer maskId = nameIdCache.get(mask);
            if (maskId == null) {
                maskId = id;
                idNameCache.put(id, mask);
                if (type != null) {
                    idTypeCache.put(id, type);
                }
                nameIdCache.put(mask, id);
            }else {
                if (type != null) {
                    idTypeCache.put(maskId, type);
                }
            }

            if (isInstanceParameter) {
                List<Integer> instance = CpeParameterNameCache.getInstanceNumbersFromName(name);
                addInstance(maskId, id, instance, maskIdToIdToInstances, maskIdToInstancesToId);
                idToMaskId.put(id, maskId);
            }


            if (Boolean.TRUE.equals(encrypted)) {
                log.info("Parameter: " + name + " is encrypted");
                idEncryptedCache.add(id);
            }
            return id;
        } finally {
            putObjToCache += (System.currentTimeMillis() - t);
        }
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
        if (idTypeCache.containsKey(nameId)) {
            return idTypeCache.get(nameId);
        }
        Integer maskId = getMaskIdByNameId(nameId);
        return maskId == null ? null : idTypeCache.get(maskId);
    }

    public boolean isCachedType(Integer nameId) {
        if (idTypeCache.containsKey(nameId)) {
            return true;
        }
        Integer maskId = getMaskIdByNameId(nameId);
        return maskId != null && idTypeCache.containsKey(maskId);
    }

    public String getTypeByMaskId(Integer maskId) {
        return idTypeCache.get(maskId);
    }

    public boolean isCached(String name) {
        String mask = getMaskByName(name);
        return nameIdCache.containsKey(mask);
    }

    public String getType(String parameterName) {
        Integer nameId = getIdByName(parameterName);
        if (idTypeCache.containsKey(nameId)) {
            return idTypeCache.get(nameId);
        }
        Integer maskId = getMaskIdByName(parameterName);
        return maskId == null ? null : getTypeByMaskId(maskId);
    }

    public boolean isEmpty() {
        return idNameCache.isEmpty();
    }

    static long getInstanceNumbersFromName = 0;

    public static List<Integer> getInstanceNumbersFromName(String name) {
        long t = System.currentTimeMillis();
        try {
            Matcher matcher = PATTERN.matcher(name);

            List<Integer> numbersList = new ArrayList<>();
            while (matcher.find()) {
                numbersList.add(Integer.parseInt(matcher.group(1)));
            }

            return numbersList;
        } finally {
            getInstanceNumbersFromName += (System.currentTimeMillis() - t);
        }
    }

    static long addInstance = 0;

    public static void addInstance(int maskId, int id, List<Integer> instances,
                                   Map<Integer, Map<Integer, List<Integer>>> maskIdToIdToInstances,
                                   Map<Integer, Map<List<Integer>, Integer>> maskIdToInstancesToId) {
        long t = System.currentTimeMillis();
        try {
            Map<Integer, List<Integer>> m = maskIdToIdToInstances.computeIfAbsent(maskId, k -> new HashMap<>());
            m.put(id, instances);
            maskIdToIdToInstances.put(maskId, m);

            Map<List<Integer>, Integer> m1 = maskIdToInstancesToId.computeIfAbsent(maskId, k -> new HashMap<>());
            m1.put(instances, id);
            maskIdToInstancesToId.put(maskId, m1);
        } finally {
            addInstance += (System.currentTimeMillis() - t);
        }
    }

    public Integer getMaskIdByName(String paramName) {
        String mask = getMaskByName(paramName);
        return nameIdCache.get(mask);
    }

    static long getMaskByName = 0;

    public String getMaskByName(String paramName) {
        long t = System.currentTimeMillis();
        try {

            return paramName.trim().replaceAll("\\.\\d+\\.", ".*.");
        } finally {
            getMaskByName += (System.currentTimeMillis() - t);
        }
    }

    static long getMaskIdByNameId = 0;

    public Integer getMaskIdByNameId(Integer nameId) {

        long t = System.currentTimeMillis();
        try {
            String name = idNameCache.get(nameId);
            if (name != null && !name.matches("\\.\\*\\.")) {
                return nameId;
            }
            return idToMaskId.get(nameId);
//            for (Map.Entry<Integer, Map<Integer, List<Integer>>> entry : maskIdToIdToInstances.entrySet()) {
//                Map<Integer, List<Integer>> innerMap = entry.getValue();
//                if (innerMap.containsKey(nameId)) {
//                    return entry.getKey();
//                }
//            }
//            return null;
        } finally {
            getMaskIdByNameId += (System.currentTimeMillis() - t);
        }
    }

    static long getMaskIdAndInstanceByNameId = 0;

    public Map<Integer, List<Integer>> getMaskIdAndInstanceByNameId(Integer nameId) {
        long t = System.currentTimeMillis();
        try {
            Integer maskId = idToMaskId.get(nameId);
            if (maskId == null || !maskIdToIdToInstances.containsKey(maskId)
                    || !maskIdToIdToInstances.get(maskId).containsKey(nameId)) {
                return Collections.emptyMap();
            }
            List<Integer> instance = maskIdToIdToInstances.get(maskId).get(nameId);

            return instance == null ? Collections.emptyMap() :
                    Collections.singletonMap(maskId, instance);

        } finally {
            getMaskIdAndInstanceByNameId += (System.currentTimeMillis() - t);
        }
    }

    static long replaceStars = 0;
    static Pattern p = Pattern.compile("\\*");

    public static String replaceStars(String parameterMaskName, List<Integer> numbers) {
        long t = System.currentTimeMillis();
        try {

            int lastIndex = 0, i = 0;
            StringBuilder output = new StringBuilder();
            Matcher matcher = p.matcher(parameterMaskName);
            while (matcher.find()) {
                output.append(parameterMaskName, lastIndex, matcher.start())
                        .append(numbers.get(i++));

                lastIndex = matcher.end();
            }
            if (lastIndex < parameterMaskName.length()) {
                output.append(parameterMaskName, lastIndex, parameterMaskName.length());
            }
            return output.toString();
        } finally {
            replaceStars += (System.currentTimeMillis() - t);
        }
    }


    public static void printStatsAndReset() {
        log.info("getIdByName: " + getIdByName);
        getIdByName = 0;

        log.info("getIdByName: " + getNameById);
        getNameById = 0;

        log.info("putObjToCache: " + putObjToCache);
        putObjToCache = 0;

        log.info("replaceStars: " + replaceStars);
        replaceStars = 0;

        log.info("getMaskIdAndInstanceByNameId: " + getMaskIdAndInstanceByNameId);
        getMaskIdAndInstanceByNameId = 0;

        log.info("getMaskIdByNameId: " + getMaskIdByNameId);
        getMaskIdByNameId = 0;

        log.info("getMaskByName: " + getMaskByName);
        getMaskByName = 0;

        log.info("addInstance: " + addInstance);
        addInstance = 0;

        log.info("getInstanceNumbersFromName: " + getInstanceNumbersFromName);
        getInstanceNumbersFromName = 0;


    }

}

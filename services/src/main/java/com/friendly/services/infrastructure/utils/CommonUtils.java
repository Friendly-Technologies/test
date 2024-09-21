package com.friendly.services.infrastructure.utils;

import com.ftacs.ObjectFactory;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@UtilityClass
public class CommonUtils {
    public static final String SUPER_DOMAIN_NAME = "Super domain";

    public static ObjectFactory ACS_OBJECT_FACTORY = new ObjectFactory();

    public static boolean isSuperDomain(Integer domainId) {
        return domainId == null || domainId == 0 || domainId == -1;
    }

    public static <E> Object[] getObjectArray(E person) {
        try {
            Method[] methods = person.getClass().getMethods();

            List<Method> getters = new ArrayList<>();
            for (Method m : methods) {
                if (isGetter(m)) {
                    getters.add(m);
                }
            }

            Object[] objArr = new Object[getters.size()];
            for (int i = 0; i < getters.size(); i++) {
                Method m = getters.get(i);
                objArr[i] = m.invoke(person);
            }

            return objArr;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Object[0];
        }
    }

    public static boolean isGetter(Method method) {
        if (!method.getName().toLowerCase().startsWith("get")) return false;
        if (method.getParameterTypes().length != 0) return false;
        if (void.class.equals(method.getReturnType())) return false;
        return true;
    }
}
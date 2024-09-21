package com.friendly.services.infrastructure.utils.websocket.model;

import com.friendly.services.infrastructure.utils.websocket.model.base.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CpeParameterChangedEvent implements Event {

    private Integer cpeId;

    /* One of these fields is optional */
    private Integer paramNameId;
    private String paramName;
    private String identifier;

    private String value;
    private boolean writable;


    public static Map<String, String> resCounterMap = new ConcurrentHashMap<>();
    public static Map<String, String> dupParamsMap = new ConcurrentHashMap<>();


    public CpeParameterChangedEvent(Integer cpeId, Integer paramNameId, String value, Boolean writable, String identifier) {
        this.cpeId = cpeId;
        this.paramNameId = paramNameId;
        this.value = value;
        this.writable = writable == null ? Boolean.FALSE : writable;
        this.identifier = identifier;

        String key = "" + cpeId + "--" + paramNameId;
        String prevValue = resCounterMap.get(key);
        if (prevValue == null) {
            resCounterMap.put(key, "" + paramNameId + "-" + value);
        } else {
//            atomicInteger.incrementAndGet();
            String dupParam = dupParamsMap.get(key);
            if(dupParam == null || dupParam.trim().isEmpty()){
                dupParam = prevValue + "\\\r" + paramNameId + "-" + value;
            }else{
                dupParam += "\\\r" + paramNameId + " - " + value;
            }
            dupParamsMap.put(key, dupParam);
        }
    }

    public CpeParameterChangedEvent(Integer cpeId, String cpeParameterName, String value, Boolean writable, String identifier) {
        this.cpeId = cpeId;
        this.paramName = cpeParameterName;
        this.value = value;
        this.writable = writable == null ? Boolean.FALSE : writable;
        this.identifier = identifier;
    }

}

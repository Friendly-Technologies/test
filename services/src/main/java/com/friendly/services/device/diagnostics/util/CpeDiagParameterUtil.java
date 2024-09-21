package com.friendly.services.device.diagnostics.util;

import com.ftacs.CpeDiagParameterWS;

public class CpeDiagParameterUtil {
    public static CpeDiagParameterWS createCpeDiagParameterWS(String name) {
        return createCpeDiagParameterWS(name, null);
    }

    public static CpeDiagParameterWS createCpeDiagParameterWS(String name, String value) {
        CpeDiagParameterWS parameterWS = new CpeDiagParameterWS();
        parameterWS.setParamName(name);
        if (value != null) {
            parameterWS.setParamValue(value);
        }
        return parameterWS;
    }
}


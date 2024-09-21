package com.friendly.services.infrastructure.utils;


import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import org.apache.xml.security.utils.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.List;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.DECRYPT_ERROR;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.ENCRYPT_ERROR;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.INIT_LICENSE_UTILS_ERROR;
import static java.nio.charset.StandardCharsets.UTF_8;

public class LicenseUtils {

    private LicenseUtils () {
        throw new IllegalStateException("Utility class");
    }

    private static final Cipher ecipher;
    private static final Cipher decipher;
    private static final byte[] keyByte = new byte[]{0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd,
            (byte) 0xef};
    static SecretKeySpec key = new SecretKeySpec(keyByte, "DES");

    static {
        try {
            ecipher = Cipher.getInstance("DES/ECB/ISO10126Padding");
            decipher = Cipher.getInstance("DES/ECB/ISO10126Padding");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            decipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            throw new FriendlyIllegalArgumentException(INIT_LICENSE_UTILS_ERROR);
        }
    }

    private static final List<String> MARKS = Arrays.asList("PSKAuthentication", "Infrastructure", "AESEncryption",
            "Disconnected", "Unconfigured", "LANInterface", "Synchronized", "AutoConfigured", "TELCOMITMSDHCPAA",
            "PrefixDelegation", "device_params_sub");

    public static boolean checkIfValueShouldBeDecrypted(String value) {
        boolean isTooShort = value.length() < 12;
        if (isTooShort) {
            return false;
        }

        boolean containsValidCharacters = !(value.contains(":") || value.contains(";") ||
                value.contains(".") || value.contains(",") ||
                value.contains("_") || value.contains("-") ||
                value.contains(" ") || value.contains("("));

        boolean isNotInMarks = !MARKS.contains(value);

        return isNotInMarks && containsValidCharacters;
    }

    public static String decryptLicenseWithCheck(String value) {
        return checkIfValueShouldBeDecrypted(value)
                ? decryptLicense(value)
                : value;
    }

    public static String decryptLicense(String value) {
        String result;
        try {
            byte[] dec = org.apache.xml.security.utils.Base64.decode(value);
            byte[] utf8 = decipher.doFinal(dec);
            result = new String(utf8, UTF_8);
        } catch (Exception e) {
            throw new FriendlyIllegalArgumentException(DECRYPT_ERROR);
        }

        return result;
    }

    public static String encryptLicense(String value) {
        String result;
        try {
            byte[] utf8 = value.getBytes(UTF_8);
            byte[] enc = ecipher.doFinal(utf8);
            result = Base64.encode(enc);
        } catch (Exception e) {
            throw new FriendlyIllegalArgumentException(ENCRYPT_ERROR);
        }

        return result;
    }
}

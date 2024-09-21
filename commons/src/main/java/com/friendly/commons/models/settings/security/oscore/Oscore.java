package com.friendly.commons.models.settings.security.oscore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Oscore implements Serializable {

    private String masterSecret;
    private String senderId;
    private String recipientId;
    private AeadAlgorithmType aeadAlgorithm;
    private HmacAlgorithmType hmacAlgorithm;
    private String masterSalt;

}

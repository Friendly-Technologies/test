package com.friendly.commons.models.device.file;

public enum DeliveryProtocolType {
    NotSet,// = -1,
    CoAP,//= 0, // CoAP (as defined in RFC 7252) with the additional support for block-wise transfer. CoAP is the default setting.
    CoAPS,// = 1, // CoAPS (as defined in RFC 7252) with the additional support for block-wise transfer
    HTTP,// = 2, // HTTP 1.1 (as defined in RFC 7230)
    HTTPS,// = 3, //  – HTTPS 1.1 (as defined in RFC 7230)
    CoAPoverTCP,// = 4, // CoAP over TCP  // 10438
    CoAPoverTLS,// = 5
}

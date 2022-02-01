package org.harrel.bitcom.client;

import org.harrel.bitcom.model.msg.Header;

import java.util.ArrayList;
import java.util.List;

public class Validator {

    public List<String> validateMessageIntegrity(Header header, byte[] payload) {
        List<String> res = new ArrayList<>();
        if (header.length() != payload.length) {
            res.add("Length declared in header was not equal to payload bytes length. header=%d, payload=%d"
                    .formatted(header.length(), payload.length));
        }
        if (header.checksum() != Hashes.getPayloadChecksum(payload)) {
            res.add("Checksum declared in header was not correct. checksum=%d expected=%d"
                    .formatted(header.checksum(), Hashes.getPayloadChecksum(payload)));
        }
        return res;
    }
}

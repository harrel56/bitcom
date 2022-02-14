package org.harrel.bitcom.client;

import org.harrel.bitcom.model.msg.Message;
import org.harrel.bitcom.model.msg.payload.Payload;

class Validator {

    public void assertMessageIntegrity(Message<Payload> msg, byte[] payload) throws MessageIntegrityException {
        if (msg.header().length() != payload.length) {
            throw new MessageIntegrityException(msg, "Length declared in header was not equal to payload bytes length. header=%d, payload=%d"
                    .formatted(msg.header().length(), payload.length));
        }
        if (msg.header().checksum() != Hashes.getPayloadChecksum(payload)) {
            throw new MessageIntegrityException(msg, "Checksum declared in header was not correct. checksum=0x%s expected=0x%s"
                    .formatted(Integer.toHexString(msg.header().checksum()), Integer.toHexString(Hashes.getPayloadChecksum(payload))));
        }
    }
}

package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.NetworkAddress;
import org.harrel.bitcom.model.msg.payload.Addr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AddrSerializer extends PayloadSerializer<Addr> {

    @Override
    public void serialize(Addr payload, OutputStream out) throws IOException {
        writeVarInt(payload.addresses().size(), out);
        for (NetworkAddress address : payload.addresses()) {
            writeNetworkAddress(address, out);
        }
    }

    @Override
    public Addr deserialize(InputStream in) throws IOException {
        int count = (int) readVarInt(in);
        if(count > Addr.SIZE_RANGE.max()) {
            throw new IllegalArgumentException("Addr message must contain valid number of addresses " + Addr.SIZE_RANGE);
        }
        var addresses = new ArrayList<NetworkAddress>(count);
        for (int i = 0; i < count; i++) {
            addresses.add(readNetworkAddress(in));
        }
        return new Addr(List.copyOf(addresses));
    }
}

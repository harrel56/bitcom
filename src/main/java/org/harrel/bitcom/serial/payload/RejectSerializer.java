package org.harrel.bitcom.serial.payload;

import org.harrel.bitcom.model.msg.payload.Command;
import org.harrel.bitcom.model.msg.payload.Reject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RejectSerializer extends PayloadSerializer<Reject> {

    @Override
    public void serialize(Reject payload, OutputStream out) throws IOException {
        writeVarString(payload.command().name().toLowerCase(), out);
        out.write(payload.type().getValue());
        writeVarString(payload.reason(), out);
    }


    @Override
    public Reject deserialize(InputStream in) throws IOException {
        String cmdString = readVarString(in);
        int typeValue = in.read();
        String reason = readVarString(in);
        return new Reject(Command.valueOf(cmdString.toUpperCase()), Reject.Type.forValue(typeValue), reason);
    }
}

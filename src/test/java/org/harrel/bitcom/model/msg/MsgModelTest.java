package org.harrel.bitcom.model.msg;

import org.harrel.bitcom.model.msg.payload.Command;
import org.harrel.bitcom.model.msg.payload.Verack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MsgModelTest {

    @Test
    void headerInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new Header(0, null, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Header(0, Command.VERSION, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Header(0, Command.VERSION, -999999, 0));
    }

    @Test
    void messageInvalid() {
        Header header = new Header(0, Command.VERSION, 99, 0);
        Verack payload = new Verack();
        assertThrows(IllegalArgumentException.class, () -> new Message<>(header, null));
        assertThrows(IllegalArgumentException.class, () -> new Message<>(null, payload));
    }
}
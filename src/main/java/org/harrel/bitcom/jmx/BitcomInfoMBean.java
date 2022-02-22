package org.harrel.bitcom.jmx;

import java.util.Date;

public interface BitcomInfoMBean {
    Date getStartDate();
    Date getStopDate();

    long getMessagesReceived();
    long getMessagesSent();
}

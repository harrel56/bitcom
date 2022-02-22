package org.harrel.bitcom.jmx;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class BitcomInfo implements BitcomInfoMBean {

    private final Date startDate = new Date();
    private Date stopDate;
    private final AtomicLong msgsReceived = new AtomicLong(0);
    private final AtomicLong msgsSent = new AtomicLong(0);

    BitcomInfo() {
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public Date getStopDate() {
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

    @Override
    public long getMessagesReceived() {
        return msgsReceived.get();
    }

    public void msgReceived() {
        msgsReceived.incrementAndGet();
    }

    @Override
    public long getMessagesSent() {
        return msgsSent.get();
    }

    public void msgSent() {
        msgsSent.incrementAndGet();
    }
}

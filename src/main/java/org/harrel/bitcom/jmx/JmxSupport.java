package org.harrel.bitcom.jmx;

import org.harrel.bitcom.client.BitcomClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class JmxSupport {

    private static final Logger logger = LoggerFactory.getLogger(JmxSupport.class);
    private static final String NAME_PATTERN = "org.harrel.bitcom:type=BitcomInfo,host=%s,port=%d,magic=%d,hash=%s";

    private JmxSupport() {
    }

    public static BitcomInfo registeredMBean(BitcomClient client) {
        BitcomInfo bean = detachedMBean();
        String name = NAME_PATTERN.formatted(client.getAddress().getHostName(),
                client.getNetworkConfiguration().getPort(),
                client.getNetworkConfiguration().getMagicValue(),
                Integer.toHexString(client.hashCode()));

        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName beanName = new ObjectName(name);
            mbs.registerMBean(bean, beanName);
        } catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e) {
            logger.error("Registering MBean of name=[%s] failed".formatted(name), e);
        }
        return bean;
    }

    public static BitcomInfo detachedMBean() {
        return new BitcomInfo();
    }
}

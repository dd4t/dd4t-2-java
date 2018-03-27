package org.dd4t.caching.jms.impl;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * JMSCacheMessageListenerTest.
 */
public class JMSCacheMessageListenerTest {

    private static ApplicationContext applicationContext;

    @BeforeClass
    public static void setUp() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext("application-context.xml");
    }

    @Test
    public void checkNamespaceAware() {
        JMSCacheMessageListener messageListener = (JMSCacheMessageListener) applicationContext.getBean
                ("cacheMessageListener");
        assertNotNull(messageListener);
    }

    @Test
    public void stripNamespaceIfRequired() {
        String key = "1:233:2222:64";
        JMSCacheMessageListener messageListener = (JMSCacheMessageListener) applicationContext.getBean
                ("cacheMessageListener");
        assertNotNull(messageListener);
        assertEquals("233:2222:64", messageListener.stripNamespaceIfRequired(key));

    }
}

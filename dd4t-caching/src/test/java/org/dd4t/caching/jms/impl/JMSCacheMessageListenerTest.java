package org.dd4t.caching.jms.impl;

import org.junit.Before;
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
    private JMSCacheMessageListener messageListener;

    @BeforeClass
    public static void setUpClass() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext("application-context.xml");
    }

    @Before
    public void setUp() {
        messageListener =
                (JMSCacheMessageListener) applicationContext.getBean("cacheMessageListener");
    }

    @Test
    public void checkNamespaceAware() {
        assertNotNull(messageListener);
    }

    @Test
    public void stripNamespaceIfRequired() {
        String key = "1:233:2222:64";
        assertNotNull(messageListener);
        assertEquals("233:2222:64", messageListener.stripNamespaceIfRequired(key));
    }
}

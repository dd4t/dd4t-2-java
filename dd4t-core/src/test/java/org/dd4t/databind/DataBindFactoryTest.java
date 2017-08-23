package org.dd4t.databind;

import org.apache.commons.io.FileUtils;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.dd4t.contentmodel.impl.PageImpl;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.util.CompressionUtils;
import org.dd4t.databind.builder.json.JsonDataBinder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;

public class DataBindFactoryTest {

    private ApplicationContext applicationContext;

    @Before
    public void setUp () throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
        applicationContext = context;
    }

    @Test
    public void testDataBindFactory () throws SerializationException, URISyntaxException, IOException {
        String page = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("test.json").toURI()));
        Page deserializedPage = DataBindFactory.buildPage(CompressionUtils.decompressGZip(CompressionUtils.decodeBase64(page)), PageImpl.class);
        Assert.notNull(deserializedPage, "page cannot be bound");
        Assert.hasLength(deserializedPage.getTitle(), "page has no valid title");
    }

    @Test
    public void testExtensionDataBindFactory () throws SerializationException, URISyntaxException, IOException {
        String page = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("extensiondatapage.json").toURI()));
        Page deserializedPage = DataBindFactory.buildPage(page, PageImpl.class);
        Assert.notNull(deserializedPage, "page cannot be bound");
        Assert.hasLength(deserializedPage.getTitle(), "page has no valid title");
    }

    @Test
    public void testDcpDeserialization() throws URISyntaxException, IOException, SerializationException {
        String dcp = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("testdcpanimal.json").toURI()));
        Assert.notNull(dcp);
        ComponentPresentation componentPresentation = DataBindFactory.buildDynamicComponentPresentation(dcp, ComponentPresentation.class);
        Assert.notNull(componentPresentation,"DCP cannot be bound");

    }

    @Test
    public void testEmbeddedSerialization() throws URISyntaxException, SerializationException, IOException {
        String page = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("test.json").toURI()));
        Page deserializedPage = DataBindFactory.buildPage(CompressionUtils.decompressGZip(CompressionUtils.decodeBase64(page)), PageImpl.class);

        EmbeddedField field = (EmbeddedField) deserializedPage.getComponentPresentations().get(0).getComponent().getContent().get("embeddedTest");

        String serialized = JsonDataBinder.getGenericMapper().writeValueAsString(field);

        System.out.println(serialized);

        Component component = deserializedPage.getComponentPresentations().get(0).getComponent();

        String serializedComponent = JsonDataBinder.getGenericMapper().writeValueAsString(component);

        assertNotNull(deserializedPage);

    }
}

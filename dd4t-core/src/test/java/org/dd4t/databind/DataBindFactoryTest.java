package org.dd4t.databind;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.io.FileUtils;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.Schema;
import org.dd4t.contentmodel.impl.ComponentImpl;
import org.dd4t.contentmodel.impl.ComponentPresentationImpl;
import org.dd4t.contentmodel.impl.ComponentTemplateImpl;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.dd4t.contentmodel.impl.FieldSetImpl;
import org.dd4t.contentmodel.impl.PageImpl;
import org.dd4t.contentmodel.impl.SchemaImpl;
import org.dd4t.contentmodel.impl.TextField;
import org.dd4t.core.databind.DataBinder;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.util.CompressionUtils;
import org.dd4t.databind.builder.json.JsonDataBinder;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DataBindFactoryTest {

    private static ApplicationContext applicationContext;

    @BeforeClass
    public static void setUp() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext("application-context.xml");
        Class<ApplicationContext> klass = ApplicationContext.class;
        URL location = klass.getResource('/' + klass.getName().replace('.', '/') + ".class");
        System.out.println("Application Context jar: " + location);
    }

    @Test
    public void testDataBindFactory() throws SerializationException, URISyntaxException, IOException {
        DataBinder databinder = applicationContext.getBean(DataBinder.class);

        String page = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("test.json").toURI()));
        Page deserializedPage =
                databinder.buildPage(CompressionUtils.decompressGZip(CompressionUtils.decodeBase64(page)),
                        PageImpl.class);
        Assert.notNull(deserializedPage, "page cannot be bound");
        Assert.hasLength(deserializedPage.getTitle(), "page has no valid title");
    }

    @Test
    public void testExtensionDataBindFactory() throws SerializationException, URISyntaxException, IOException {
        String page =
                FileUtils.readFileToString(new File(ClassLoader.getSystemResource("extensiondatapage.json").toURI()));
        DataBinder databinder = applicationContext.getBean(DataBinder.class);
        Page deserializedPage = databinder.buildPage(page, PageImpl.class);
        Assert.notNull(deserializedPage, "page cannot be bound");
        Assert.hasLength(deserializedPage.getTitle(), "page has no valid title");


        String serialized = JsonDataBinder.getGenericMapper().writeValueAsString(deserializedPage);

        assertNotNull(serialized);

        String rootFolder = new ClassPathResource(".").getFile().getAbsolutePath();
        FileUtils.write(new File(rootFolder + "/testserialized.json"), serialized, "UTF-8");
    }

    @Test
    public void testDcpDeserializationWithUtcDate() throws URISyntaxException, IOException, SerializationException {
        DataBinder databinder = applicationContext.getBean(DataBinder.class);

        String dcp =
                FileUtils.readFileToString(new File(ClassLoader.getSystemResource("testdcp-utc-date.json").toURI()),
                        StandardCharsets.UTF_8);
        Assert.notNull(dcp);
        ComponentPresentation componentPresentation =
                databinder.buildComponentPresentation(dcp, ComponentPresentation.class);
        Assert.notNull(componentPresentation, "DCP cannot be bound");

    }

    @Test
    public void testDcpDeserializationWithUtcDateMetaField() throws URISyntaxException, IOException,
            SerializationException {
        DataBinder databinder = applicationContext.getBean(DataBinder.class);

        String dcp =
                FileUtils.readFileToString(new File(ClassLoader.getSystemResource("json.json").toURI()),
                        StandardCharsets.UTF_8);
        Assert.notNull(dcp);
        ComponentPresentation componentPresentation =
                databinder.buildComponentPresentation(dcp, ComponentPresentation.class);

        Component component =
                (Component) componentPresentation.getComponent().getContent().get("mc").getValues().get(0);

        FieldSet fieldSet = (FieldSet) component.getContent().get("sl").getValues().get(0);

        Component categoryComponent = (Component) fieldSet.getContent().get("il").getValues().get(0);


        List<Object> dateList = categoryComponent.getCategories().get(0).getKeywords().get(0).getMetadata().get(
                "dateTest").getValues();


        assertEquals("2022-12-31T09:00:00.124Z", dateList.get(0).toString());
        assertEquals("2022-12-31T09:00:00.000Z", dateList.get(1).toString());
        assertEquals("2022-12-31T09:00:00.000+01:00", dateList.get(2).toString());
        assertEquals("2022-12-31T09:00:00.124+01:00", dateList.get(3).toString());
        Assert.notNull(componentPresentation, "DCP cannot be bound");

    }

    @Test
    public void testDcpDeserialization() throws URISyntaxException, IOException, SerializationException {
        DataBinder databinder = applicationContext.getBean(DataBinder.class);

        String dcp = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("testdcp.json").toURI()),
                StandardCharsets.UTF_8);
        Assert.notNull(dcp);
        ComponentPresentation componentPresentation =
                databinder.buildComponentPresentation(dcp, ComponentPresentation.class);
        Assert.notNull(componentPresentation, "DCP cannot be bound");

    }

    @Test
    public void testEmbeddedSerialization() throws URISyntaxException, SerializationException, IOException {
        String page =
                FileUtils.readFileToString(new File(ClassLoader.getSystemResource("fulltestencoded.json").toURI()));
        DataBinder databinder = applicationContext.getBean(DataBinder.class);
        Page deserializedPage =
                databinder.buildPage(CompressionUtils.decompressGZip(CompressionUtils.decodeBase64(page)),
                        PageImpl.class);
        assertNotNull(deserializedPage);
        EmbeddedField field =
                (EmbeddedField) deserializedPage.getComponentPresentations().get(0).getComponent().getContent()
                        .get("embeddedTest");

        String serialized = JsonDataBinder.getGenericMapper().writeValueAsString(field);

        System.out.println(serialized);

        Component component = deserializedPage.getComponentPresentations().get(0).getComponent();

        String serializedComponent = JsonDataBinder.getGenericMapper().writeValueAsString(component);

        assertTrue(serializedComponent.contains(serialized));

    }

    @Test
    public void testStaticDataBindFactory() throws SerializationException, URISyntaxException, IOException {


        String page = CompressionUtils.decompressGZip(CompressionUtils.decodeBase64(
                FileUtils.readFileToString(new File(ClassLoader.getSystemResource("test.json").toURI()))));
        DataBinder databinder = applicationContext.getBean(DataBinder.class);
        Page deserializedPage = databinder.buildPage(page, PageImpl.class);
        Assert.notNull(deserializedPage, "page cannot be bound");
        Assert.hasLength(deserializedPage.getTitle(), "page has no valid title");
    }

    @Test
    public void testJsonDataBinder() {
        DataBinder dataBinder = applicationContext.getBean(DataBinder.class);
        assertNotNull(dataBinder);

    }

    @Test
    public void testDcp() throws URISyntaxException, IOException, SerializationException {
        String dcp = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("newitem.json").toURI()));
        DataBinder dataBinder = applicationContext.getBean(DataBinder.class);

        ComponentPresentation componentPresentation =
                dataBinder.buildComponentPresentation(dcp, ComponentPresentation.class);

        assertNotNull(componentPresentation);
    }

    @Test
    public void testUtcDateConversion() throws URISyntaxException, IOException, SerializationException {
        String page = Files.readString(Paths.get(ClassLoader.getSystemResource("dd4t221json-utc-date.json").toURI()));
        DataBinder dataBinder = applicationContext.getBean(DataBinder.class);
        Page deserializedPage = dataBinder.buildPage(page, PageImpl.class);
        Assert.notNull(deserializedPage, "page cannot be bound");

    }


    @Test
    public void testManualPageCreationSerialization()
            throws JsonProcessingException, SerializationException, NoSuchFieldException, IllegalAccessException {
        //given
        PageImpl page = new PageImpl();

        TextField textField = new TextField();
        textField.setTextValues(Collections.singletonList("Subheading 1"));

        FieldSetImpl fieldSet = new FieldSetImpl();
        fieldSet.setFieldSet(getMap("subheading", textField));

        EmbeddedField field = new EmbeddedField();
        field.setEmbeddedValues(Collections.<FieldSet>singletonList(fieldSet));

        ComponentImpl component = new ComponentImpl();
        component.setContent(getMap("articleBody", field));

        Schema schema = new SchemaImpl();
        schema.setId("tcm:1-3-8");
        schema.setTitle("Schema");

        java.lang.reflect.Field rootElement = schema.getClass().getDeclaredField("rootElement");
        rootElement.setAccessible(true);
        rootElement.set(schema, "myrootelement");

        component.setSchema(schema);

        ComponentPresentationImpl presentation = new ComponentPresentationImpl();

        ComponentTemplate componentTemplate = new ComponentTemplateImpl();
        componentTemplate.setRevisionDate(DateTime.now());
        componentTemplate.setId("tcm:1-2-32");
        componentTemplate.setTitle("CT");
        presentation.setComponent(component);
        presentation.setComponentTemplate(componentTemplate);
        page.setComponentPresentations(Collections.<ComponentPresentation>singletonList(presentation));

        //when
        String asString = serialize(page);

        //then
        assertInnerFields(asString);

        DataBinder dataBinder = applicationContext.getBean(DataBinder.class);
        PageImpl afterSerializationPage = dataBinder.buildPage(asString, PageImpl.class);

        assertNotNull(afterSerializationPage);
        FieldSetImpl embedded =
                (FieldSetImpl) afterSerializationPage.getComponentPresentations().get(0).getComponent().getContent()
                        .get("articleBody").getValues().get(0);
        assertNotNull(embedded);
        assertEquals("Subheading 1", embedded.getFieldSet().get("subheading").getValues().get(0));
    }

    private HashMap<String, Field> getMap(String key, org.dd4t.contentmodel.Field value) {
        HashMap<String, org.dd4t.contentmodel.Field> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private String serialize(PageImpl page) throws JsonProcessingException {
        return JsonDataBinder.getGenericMapper().writeValueAsString(page);
    }

    private void assertInnerFields(String asString) {
        assertTrue("Should contain articleBody in: " + asString, asString.contains("articleBody"));
        assertTrue("Should contain subheading in : " + asString, asString.contains("subheading"));
        assertTrue("Should contain embedded text value Subheading 1 in: " + asString,
                asString.contains("Subheading " + "1"));
    }
}

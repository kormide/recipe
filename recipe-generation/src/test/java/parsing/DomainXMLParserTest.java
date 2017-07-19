package parsing;

import ca.derekcormier.recipe.parser.Domain;
import ca.derekcormier.recipe.parser.DomainXMLParser;
import ca.derekcormier.recipe.parser.ParseException;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DomainXMLParserTest {
    @Test
    public void testValidateXml_emptyDomainPasses() {
        DomainXMLParser.validateXml(getStringStream(String.join("\n",
            "<domain>",
            "</domain>"
        )));
    }

    @Test(expected = ParseException.class)
    public void testValidateXml_multipleDomainsFail() {
        DomainXMLParser.validateXml(getStringStream(String.join("\n",
            "<domain>",
            "</domain>",
            "<domain>",
            "</domain>"
        )));
    }

    @Test
    public void testValidateXml_validXmlPasses() {
        DomainXMLParser.validateXml(getStringStream(String.join("\n",
            "<domain>",
                "<entity name='TestEntity'>",
                    "<property name='a' type='int' required='true'/>",
                    "<property name='b' type='string' required='true'/>",
                "</entity>",
                "<enum name='TestEnum'>",
                    "<value>A</value>",
                    "<value>B</value>",
                    "<value>C</value>",
                "</enum>",
            "</domain>"
        )));
    }

    @Test(expected = ParseException.class)
    public void testValidateXml_entityWithNoNameFails() {
        DomainXMLParser.validateXml(getStringStream(String.join("\n",
            "<domain>",
                "<entity></entity>",
            "</domain>"
        )));
    }

    @Test(expected = ParseException.class)
    public void testValidateXml_propertyWithNoNameFails() {
        DomainXMLParser.validateXml(getStringStream(String.join("\n",
            "<domain>",
                "<entity name='TestEntity'>",
                    "<property type='string'/>",
                "</entity>",
            "</domain>"
        )));
    }

    @Test(expected = ParseException.class)
    public void testValidateXml_entitiesWithSameNameFails() {
        DomainXMLParser.validateXml(getStringStream(String.join("\n",
            "<domain>",
                "<entity name='TestEntity'></entity>",
                "<entity name='TestEntity'></entity>",
            "</domain>"
        )));
    }

    @Test(expected = ParseException.class)
    public void testValidateXml_propertiesWithSameNameInEntityFails() {
        DomainXMLParser.validateXml(getStringStream(String.join("\n",
            "<domain>",
                "<entity name='TestEntity'>",
                    "<property name='a' type='int' required='true'/>",
                    "<property name='a' type='string' required='true'/>",
                "</entity>",
            "</domain>"
        )));
    }

    @Test(expected = ParseException.class)
    public void testValidateXml_propertiesWithSameNameInDifferentEntitiesPasses() {
        DomainXMLParser.validateXml(getStringStream(String.join("\n",
            "<domain>",
                "<entity name='A'>",
                    "<property name='a' type='int'/>",
                "</entity>",
                "<entity name='B'>",
                    "<property name='a' type='int'/>",
                "</entity>",
            "</domain>"
        )));
    }

    @Test(expected = ParseException.class)
    public void testValidateXml_enumsWithDuplicateValuesFails() {
        DomainXMLParser.validateXml(getStringStream(String.join("\n",
            "<domain>",
                "<enum name='TestEnum'>",
                    "<value>A</value>",
                    "<value>A</value>",
                "</enum>",
            "</domain>"
        )));
    }

    @Test
    public void testValidateXml_sameEnumValuesAcrossDifferentEnumsPasses() {
        DomainXMLParser.validateXml(getStringStream(String.join("\n",
            "<domain>",
                "<enum name='TestEnum1'>",
                    "<value>A</value>",
                "</enum>",
                "<enum name='TestEnum2'>",
                    "<value>A</value>",
                "</enum>",
            "</domain>"
        )));
    }

    @Test
    public void testParseXml_parsesEmptyDomain() {
        Domain domain = DomainXMLParser.parseXml(getStringStream(String.join("\n",
            "<domain>",
            "</domain>"
        )));

        assertEquals(0, domain.getEntities().size());
    }

    @Test
    public void testParseXml_parsesEntity() {
        Domain domain = DomainXMLParser.parseXml(getStringStream(String.join("\n",
            "<domain>",
                "<entity name='TestEntity'>",
                "</entity>",
            "</domain>"
        )));

        assertEquals(1, domain.getEntities().size());
        assertEquals("TestEntity", domain.getEntities().get(0).getName());
        assertEquals(0, domain.getEntities().get(0).getProperties().size());
    }

    @Test
    public void testParseXml_parsesProperty() {
        Domain domain = DomainXMLParser.parseXml(getStringStream(String.join("\n",
            "<domain>",
                "<entity name='TestEntity'>",
                    "<property name='TestProperty' type='string'/>",
                "</entity>",
            "</domain>"
        )));

        assertEquals(1, domain.getEntities().get(0).getProperties().size());
        assertEquals("TestProperty", domain.getEntities().get(0).getProperties().get(0).getName());
        assertEquals("string", domain.getEntities().get(0).getProperties().get(0).getType());
        assertEquals(false, domain.getEntities().get(0).getProperties().get(0).isRequired());
    }

    @Test
    public void testParseXml_parsesProperty_required() {
        Domain domain = DomainXMLParser.parseXml(getStringStream(String.join("\n",
            "<domain>",
                "<entity name='TestEntity'>",
                    "<property name='TestProperty' type='string' required='true'/>",
                "</entity>",
            "</domain>"
        )));

        assertEquals(1, domain.getEntities().get(0).getProperties().size());
        assertEquals(true, domain.getEntities().get(0).getProperties().get(0).isRequired());
    }

    @Test
    public void testParseXml_parsesEnum() {
        Domain domain = DomainXMLParser.parseXml(getStringStream(String.join("\n",
            "<domain>",
                "<enum name='TestEnum'>",
                "</enum>",
            "</domain>"
        )));

        assertEquals(1, domain.getEnums().size());
        assertEquals("TestEnum", domain.getEnums().get(0).getName());
        assertEquals(0, domain.getEnums().get(0).getValues().size());
    }

    @Test
    public void testParseXml_parsesEnumValue() {
        Domain domain = DomainXMLParser.parseXml(getStringStream(String.join("\n",
            "<domain>",
                "<enum name='TestEnum'>",
                    "<value>A</value>",
                "</enum>",
            "</domain>"
        )));

        assertEquals(1, domain.getEnums().get(0).getValues().size());
        assertEquals("A", domain.getEnums().get(0).getValues().get(0));
    }

    private InputStream getStringStream(String string) {
        return new ByteArrayInputStream(string.getBytes());
    }
}

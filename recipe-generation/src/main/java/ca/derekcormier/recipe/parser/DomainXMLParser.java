package ca.derekcormier.recipe.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.net.URL;

public class DomainXMLParser {
    public static void validateXml(InputStream xmlStream) {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL url = DomainXMLParser.class.getClassLoader().getResource("domain-schema.xsd");
            Schema schema = schemaFactory.newSchema(url);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlStream));
        } catch (SAXException e) {
            throw new ParseException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Domain parseXml(InputStream xmlStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlStream);
            doc.getDocumentElement().normalize();

            Domain domain = new Domain();

            Element domainElement = (Element)doc.getElementsByTagName("domain").item(0);
            NodeList entities = domainElement.getElementsByTagName("entity");
            for (int i = 0; i < entities.getLength(); i++) {
                Entity entity = parseEntity((Element)entities.item(i));
                domain.addEntity(entity);
            }

            NodeList enums = domainElement.getElementsByTagName("enum");
            for (int i = 0; i < enums.getLength(); i++) {
                Enum enumEntity = parseEnum((Element)enums.item(i));
                domain.addEnum(enumEntity);
            }

            return domain;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new ParseException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Entity parseEntity(Element entityElement) {
        String name = entityElement.getAttribute("name");
        Entity entity = new Entity(name);


        NodeList properties = entityElement.getElementsByTagName("property");
        for (int i = 0; i < properties.getLength(); i++) {
            Element propertyElement = (Element)properties.item(i);
            Property property = parseProperty(propertyElement);
            entity.addProperty(property);
        }

        return entity;
    }

    private static Property parseProperty(Element propertyElement) {
        String name = propertyElement.getAttribute("name");
        String type = propertyElement.getAttribute("type");
        boolean required = propertyElement.hasAttribute("required") && propertyElement.getAttribute("required").equals("true");

        return new Property(name, type, required);
    }

    private static Enum parseEnum(Element enumElement) {
        String name = enumElement.getAttribute("name");
        Enum enumeration = new Enum(name);

        NodeList values = enumElement.getElementsByTagName("value");
        for (int i = 0; i < values.getLength(); i++) {
            enumeration.addValue(values.item(i).getTextContent());
        }

        return enumeration;
    }
}

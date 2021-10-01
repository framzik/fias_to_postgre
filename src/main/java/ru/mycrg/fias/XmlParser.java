package ru.mycrg.fias;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class XmlParser {

    public static final String SCHEMA = "public";
    public static final String TABLE_NAME = "fias_address";

    private final DocumentBuilder documentBuilder;

    private final Logger log = LoggerFactory.getLogger(XmlParser.class);

    public XmlParser() throws ParserConfigurationException {
        this.documentBuilder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
    }

    public Optional<Map<String, String>> parse(File xmlFile) {

        try (InputStream inputStream = new FileInputStream(xmlFile) {
        }) {
            Document doc = documentBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("*");
            String name = nodeList.item(0).getNodeName();

            switch (name) {
                case "ADDRESSOBJECTS":
                case "ITEMS":
                case "HOUSES":
                    return parse(nodeList);
                case "APARTMENTS":
            }
        } catch (IOException | SAXException e) {
            log.error("Can't parse file: {} ", e.getMessage());
        }

        return Optional.empty();
    }

    private Optional<Map<String, String>> parse(NodeList nodeList) {
        Map<String, String> result = new HashMap<>();
        String name = nodeList.item(0).getNodeName();
        for (int i = 1; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            NamedNodeMap attributes = element.getAttributes();

            // если не актуальная запись(isactive=0) - пропускаем
            if (attributes.getNamedItem("ISACTIVE").getNodeValue().equalsIgnoreCase("0")) {
                continue;
            }

            String objectId = attributes.getNamedItem("OBJECTID").getNodeValue().toLowerCase();
            String query = initQuery(attributes, name);
            result.put(objectId, query);
        }

        return Optional.of(result);
    }

    private String initQuery(NamedNodeMap attributes, String entityName) {
        String columnNames = "";
        String values = "";
        for (int j = 0; j < attributes.getLength(); j++) {
            Node node = attributes.item(j);
            String nodeName = node.getNodeName();
            String nodeValue = node.getNodeValue();

            if (checkColumnName(nodeName)) {
                if (nodeName.equalsIgnoreCase("number") && entityName.equalsIgnoreCase("APARTMENTS")) {
                    nodeName = "apart_number";
                } else if (nodeName.equalsIgnoreCase("number") && entityName.equalsIgnoreCase("STEADS")) {
                    nodeName = "steads_number";
                }

                columnNames = String.join(",", columnNames, nodeName);

                values = String.join(",'", values, nodeValue + "'");
            }
        }
        columnNames = columnNames.substring(1);
        values = values.substring(1);

        return String.format("insert into %s.%s (%s) values (%s) ", SCHEMA, TABLE_NAME, columnNames,
                             values);
    }

    private boolean checkColumnName(String nodeName) {
        return nodeName.equalsIgnoreCase("objectid") || nodeName.equalsIgnoreCase("objectguid")
                || nodeName.equalsIgnoreCase("name") || nodeName.equalsIgnoreCase("typename")
                || nodeName.equalsIgnoreCase("level") || nodeName.equalsIgnoreCase("isactive")
                || nodeName.equalsIgnoreCase("oktmo") || nodeName.equalsIgnoreCase("parentobjid")
                || nodeName.equalsIgnoreCase("number") || nodeName.equalsIgnoreCase("aparttype")
                || nodeName.equalsIgnoreCase("housenum") || nodeName.equalsIgnoreCase("addnum1")
                || nodeName.equalsIgnoreCase("housetype") || nodeName.equalsIgnoreCase("addnum2")
                || nodeName.equalsIgnoreCase("addtype1") || nodeName.equalsIgnoreCase("addtype2");
    }
}

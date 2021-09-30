package ru.mycrg.fias;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
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

    public Optional<Map<Integer, String>> parse(File xmlFile) {

        try (InputStream inputStream = new FileInputStream(xmlFile) {
        }) {
            Document doc = documentBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("*");
            String name = nodeList.item(0).getNodeName();

            switch (name) {
                case "ADDRESSOBJECTS":
                    return parseAddrObj(nodeList);
                case "ITEMS":
                    return parseMunHierarchy(nodeList);
            }
        } catch (IOException | SAXException e) {
            log.error("Can't parse file: {} ", e.getMessage());
        }

        return Optional.empty();
    }

//    private String getTableName(Node node) {
//        String tagName = ((Element) node).getTagName().toLowerCase(Locale.ROOT);
//
//        if ("ITEMS".equalsIgnoreCase(tagName)) {
//            NamedNodeMap attributes = node.getFirstChild().getAttributes();
//            if (attributes.getLength() == 16) {
//                return "adm_hierarchy";
//            } else if (attributes.getLength() == 11) {
//                return "mun_hierarchy";
//            }
//        }
//
//        return tagName;
//    }

    private Optional<Map<Integer, String>> parseMunHierarchy(NodeList nodeList) {
        Map<Integer, String> result = new HashMap<>();
        for (int i = 1; i < nodeList.getLength(); i++) {
            String columnNames = "";
            String values = "";

            Element element = (Element) nodeList.item(i);
            NamedNodeMap attributes = element.getAttributes();

            for (int j = 1; j < attributes.getLength(); j++) {
                String nodeName = attributes.item(j).getNodeName();
                if (checkColumnName(nodeName)) {
                    String nodeValue = attributes.item(j).getNodeValue().toLowerCase();
                    // если не актуальная запись(isactive=0) - пропускаем
                    if (nodeName.equalsIgnoreCase("isactive") && nodeValue.equalsIgnoreCase("0")) {
                        columnNames = "";
                        values = "";

                        continue;
                    }
                    columnNames = String.join(",", columnNames,
                                              nodeName);

                    values = String.join(",'", values,
                                         nodeValue + "'");
                }
            }
            columnNames = columnNames.substring(1);
            values = values.substring(1);
            String query = String.format("insert into %s.%s (%s) values (%s) ", SCHEMA, TABLE_NAME, columnNames,
                                         values);

            result.put(i, query);
        }
        return Optional.of(result);
    }

    private Optional<Map<Integer, String>> parseAddrObj(NodeList nodeList) {
        Map<Integer, String> result = new HashMap<>();
        for (int i = 1; i < nodeList.getLength(); i++) {
            String columnNames = "";
            String values = "";

            Element element = (Element) nodeList.item(i);
            NamedNodeMap attributes = element.getAttributes();

            for (int j = 1; j < attributes.getLength(); j++) {
                String nodeName = attributes.item(j).getNodeName();
                if (checkColumnName(nodeName)) {
                    String nodeValue = attributes.item(j).getNodeValue().toLowerCase();
                    // если не актуальная запись(isactive=0) - пропускаем
                    if (nodeName.equalsIgnoreCase("isactive") && nodeValue.equalsIgnoreCase("0")) {
                        columnNames = "";
                        values = "";

                        continue;
                    }
                    columnNames = String.join(",", columnNames,
                                              nodeName);

                    values = String.join(",'", values,
                                         nodeValue + "'");
                }
            }
            columnNames = columnNames.substring(1);
            values = values.substring(1);
            String query = String.format("insert into %s.%s (%s) values (%s) ", SCHEMA, TABLE_NAME, columnNames,
                                         values);

            result.put(i, query);
        }

        return Optional.of(result);
    }

    private boolean checkColumnName(String nodeName) {
        return nodeName.equalsIgnoreCase("objectid") || nodeName.equalsIgnoreCase("objectguid")
                || nodeName.equalsIgnoreCase("name") || nodeName.equalsIgnoreCase("typename")
                || nodeName.equalsIgnoreCase("level") || nodeName.equalsIgnoreCase("isactive")
                || nodeName.equalsIgnoreCase("okato") || nodeName.equalsIgnoreCase("parentobjid") ;
    }
}

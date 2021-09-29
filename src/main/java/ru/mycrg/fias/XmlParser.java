package ru.mycrg.fias;

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
import java.util.Locale;
import java.util.Map;

public class XmlParser {

    private final DocumentBuilder documentBuilder;

    private String schema = "public";

    public XmlParser() throws ParserConfigurationException {
        this.documentBuilder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
    }

    public Map<Integer, String> parse(File xmlFile) {
        Map<Integer, String> result = new HashMap<>();

        try (InputStream inputStream = new FileInputStream(xmlFile) {
        }) {
            Document doc = documentBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("*");
            String tableName = getTableName(nodeList.item(0));

            for (int i = 1; i < nodeList.getLength(); i++) {
                String columnNames = "";
                String values = "";
                // Get element
                Element element = (Element) nodeList.item(i);
                NamedNodeMap attributes = element.getAttributes();
                for (int j = 1; j < attributes.getLength(); j++) {
                    columnNames = String.join(",", columnNames,
                                              attributes.item(j).getNodeName());

                    values = String.join(",'", values,
                                         attributes.item(j).getNodeValue().toLowerCase(Locale.ROOT) + "'");
                }
                columnNames = columnNames.substring(1);
                values = values.substring(1);
                String query = String.format("insert into %s.%s (%s) values (%s) ", schema, tableName, columnNames,
                                             values);

                result.put(i, query);
            }
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e.getMessage());
        }

        return result;
    }

    private String getTableName(Node node) {
        String tagName = ((Element) node).getTagName().toLowerCase(Locale.ROOT);

        if ("ITEMS".equalsIgnoreCase(tagName)) {
            NamedNodeMap attributes = node.getFirstChild().getAttributes();
            if (attributes.getLength() == 16) {
                return "adm_hierarchy";
            } else if (attributes.getLength() == 11) {
                return "mun_hierarchy";
            }
        }

        return tagName;
    }
}

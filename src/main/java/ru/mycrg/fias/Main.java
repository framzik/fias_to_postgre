package ru.mycrg.fias;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, SAXException {
        XmlParser xmlParser = new XmlParser();
        WriterDb writerDb = new WriterDb();
        Map<Integer, String> info = xmlParser.parse(
                new File("/home/framzik/Загрузки/фиас/91/AS_ADDR_OBJ_20210923_d5b58e2e-fb4e-4602-ac63" +
                                 "-a0d02df24fab.XML"));
        writerDb.write(info);
    }
}

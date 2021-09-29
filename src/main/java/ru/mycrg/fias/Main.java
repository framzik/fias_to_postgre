package ru.mycrg.fias;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException {
        XmlParser xmlParser = new XmlParser();
        Writer writer = new Writer();
        Map<Integer, String> info = xmlParser.parse(
                new File("/home/framzik/Загрузки/фиас/91/AS_REESTR_OBJECTS_20210923_6c05c97f-fe5c-4c26-a98f-fb200d5eeaf4.XML"));
        System.out.println("Было записано, строк:  " + writer.writeValue(info));
    }
}

package ru.mycrg.fias;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException {
        String folderPath = args[0];
        XmlParser xmlParser = new XmlParser();
        Writer writer = new Writer();
        File xmlFile = new File(folderPath);
        if (xmlFile.isDirectory()) {
            if (xmlFile.listFiles() != null && xmlFile.listFiles().length != 0) {
                List<File> fileNames = Arrays.asList(xmlFile.listFiles());

                writer.truncateDb();

                fileNames.forEach(file -> {
                    Optional<Map<String, String>> oParse = xmlParser.parse(file);
                    oParse.ifPresent(info -> {
                        writer.writeValue(info, file);
                    });
                });
            }
        } else {
            Optional<Map<String, String>> oParse = xmlParser.parse(xmlFile);
            oParse.ifPresent(info -> {
                writer.writeValue(info, xmlFile);
            });
        }
    }
}

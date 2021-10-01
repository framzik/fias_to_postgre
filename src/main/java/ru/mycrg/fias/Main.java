package ru.mycrg.fias;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException {
        String folderPath = "/home/framzik/Загрузки/фиас/91/xmls";
        XmlParser xmlParser = new XmlParser();
        Writer writer = new Writer();
        File folderWithXmlFiles = new File(folderPath);

        if (folderWithXmlFiles.listFiles() != null && folderWithXmlFiles.listFiles().length != 0) {
            List<File> fileNames = Arrays.asList(folderWithXmlFiles.listFiles());

//            writer.truncateDb();

            fileNames.forEach(file -> {
                Optional<Map<String, String>> oParse = xmlParser.parse(file);
                oParse.ifPresent(writer::writeValue);
            });
        }
    }
}

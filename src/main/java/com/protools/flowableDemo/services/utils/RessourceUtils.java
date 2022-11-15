package com.protools.flowableDemo.services.utils;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
public class RessourceUtils {
    private static Logger logger = LogManager.getLogger(RessourceUtils.class);
    public static String getResourceFileAsString(String fileName) throws IOException, TransformerException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        String fileNameFinal = "processes/"+fileName+".bpmn20.xml";
        logger.info("\t >> Getting file : "+fileNameFinal);

        InputStream file = classloader.getResourceAsStream(fileNameFinal);
        logger.info("\t >> File found : "+ file.toString());
        String lines = new String();
        Document doc = null;

        try (InputStreamReader streamReader =
                     new InputStreamReader(file, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                lines += line;
            }
            lines = lines.replace("\n", "").replace("\r", "");
            logger.info("lines: " + lines);
            DocumentBuilder builder = dbf.newDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(lines)));


        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        // Convert to String (even tho we apparently want a xml file)
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        Writer out = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(out));
        String outString = out.toString().replaceAll("[\\\r]+", "");

        //logger.info("\t >> File converted to String : "+ outString);
        return outString;

    }
}

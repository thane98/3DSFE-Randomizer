package randomizer.common.fs.data;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Hashtable;

public class ScriptSingleton {
    private static ScriptSingleton instance;

    private Hashtable<String, Byte[]> tags = new Hashtable<>();
    private Hashtable<Integer, Byte[]> subheaders = new Hashtable<>();

    private ScriptSingleton() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(this.getClass().getResourceAsStream("Commands.xml"));
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("Command");
        for (int x = 0; x < nList.getLength(); x++) {
            Node node = nList.item(x);
            if (!node.getAttributes().getNamedItem("name").getNodeValue().equals("")) {
                if (node.getAttributes().getNamedItem("tag").getNodeValue().equals("")) {
                    tags.put(node.getAttributes().getNamedItem("name").getNodeValue(), new Byte[0]);
                } else {
                    String[] splitString = node.getAttributes().getNamedItem("tag").getNodeValue().split(",");
                    Byte[] bytes = new Byte[splitString.length];
                    for (int y = 0; y < bytes.length; y++) {
                        bytes[y] = Byte.parseByte(splitString[y], 16);
                    }
                    tags.put(node.getAttributes().getNamedItem("name").getNodeValue(), bytes);
                }
            }
        }

        doc = dBuilder.parse(this.getClass().getResourceAsStream("Subheaders.xml"));
        doc.getDocumentElement().normalize();
        nList = doc.getElementsByTagName("Subheader");
        for (int x = 0; x < nList.getLength(); x++) {
            Node node = nList.item(x);
            if (!node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                int id = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
                String[] unparsedValues = node.getAttributes().getNamedItem("layout").getNodeValue().split(",");
                Byte[] values = new Byte[unparsedValues.length];
                for (int y = 0; y < values.length; y++)
                    values[y] = Byte.parseByte(unparsedValues[y]);
                subheaders.put(id, values);
            }
        }
    }

    public static ScriptSingleton getInstance() throws Exception {
        if (instance == null)
            instance = new ScriptSingleton();
        return instance;
    }

    public Hashtable<String, Byte[]> getTags() {
        return tags;
    }

    public Hashtable<Integer, Byte[]> getSubheaders() {
        return subheaders;
    }
}

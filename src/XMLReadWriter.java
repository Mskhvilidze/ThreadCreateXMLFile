import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.UUID;


public class XMLReadWriter{
    private boolean isReady;


    public XMLReadWriter() throws Exception {
        isReady = false;
    }

    public boolean isReady() {
        return isReady;
    }
    /**
     * XPaht wird zum Erzeugen der XPaht-Objekten erstellt. Danach wird XPath-Objekt aus XPathFactory erstellt.
     * XPhat-Ausdruck wird kompiliert.
     *
     * @param expression XPhat-Ausdruck
     * @param files      Files
     * @return Gibt die Document zurück
     * @throws Exception
     */
    public Document merge(String expression, File... files) throws Exception {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();
        XPathExpression compiledExpression = xpath.compile(expression);
        return merge(compiledExpression, files);
    }

    /**
     * Wird neue DocumentBuildFactory erstellt, um das Document geparst zu werden.
     *
     * @param expression
     * @param files
     * @return
     * @throws Exception
     */
    public Document merge(XPathExpression expression, File... files) throws Exception {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document base = docBuilder.parse(files[0]);

        Node results = (Node) expression.evaluate(base, XPathConstants.NODE);
        if (results == null) {
            throw new IOException(files[0] + ": Fehlschlag" + files.length);
        }

        for (int i = 1; i < files.length; i++) {
            Document merge = docBuilder.parse(files[i]);
            Node nextResults = (Node) expression.evaluate(merge, XPathConstants.NODE);
            while (nextResults.hasChildNodes()) {
                Node kid = nextResults.getFirstChild();
                nextResults.removeChild(kid);
                kid = base.importNode(kid, true);
                results.appendChild(kid);
            }
            if (calculate() > 85) {
                // Arrays.stream(files).toList().clear();
                // JOptionPane.showMessageDialog(null, "List cleared");
            }
        }

        return base;
    }

    /**
     * Zeigt Start- und Maxwerten
     *
     * @return
     */
    private long calculate() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();
        long usedMemory = heapUsage.getUsed() / (1024 * 1024);
        long maxMemory = heapUsage.getMax() / (1024 * 1024);
        long percentageUsed = (long) (100 * ((1.0 * usedMemory) / (1.0 * maxMemory)));
        System.out.println("Used vs. Max Memory: " + usedMemory + "M/" + maxMemory + "M " + percentageUsed);
        return percentageUsed;
    }

    /**
     * Document anzeigen lassen
     *
     * @param doc
     * @throws Exception
     */
    public void print(Document doc) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        Result result = new StreamResult(System.out);
        transformer.transform(source, result);
    }

    /**
     * Das erstellte Document wird in neue Datei geschrieben.
     *
     * @param doc
     * @throws TransformerConfigurationException
     * @throws IOException
     */
    public void write(Document doc) throws TransformerConfigurationException, IOException {
        String path = "C:/Beka/OMP/IDEA/Zusatzaufgaben/ThreadXMLDateien/";
        String uuid = UUID.randomUUID().toString();
        File file = new File(path);

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new StringWriter());
        try {
            transformer.transform(source, result);
            Writer output = new BufferedWriter(new FileWriter("C:/Beka/OMP/IDEA/Zusatzaufgaben/ThreadXMLDateien/" + file.getName() + "_" + uuid + ".xml"));
            String xmlOutput = result.getWriter().toString();
            if(doc != null){
                output.write(xmlOutput);
                output.close();
                System.out.println("Ready");
                this.isReady = true;
            }else{
                throw new IOException("Document ist leer, daher Datei wurde nicht geschrieben");
            }

        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public File[] findAllFilesInFolder(File... folder) {
        return folder;
    }

    /**
     * create XML files
     *
     * @param files
     * @throws Exception
     */
    public void createForXMLFile(File[] files) throws Exception {

        Thread thread = new Thread(() -> {
            File[] files1 = new File[files.length / 4];
            int count = 0;
            for (int j = 0; j < files.length / 4; j++) {
                files1[count++] = files[j];
                if (count == files1.length) {
                    break;
                }

            }

            Document doc = null;
            try {
                doc = merge("/STEP-ProductInformation/Products", files1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                write(doc);
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Thread thread1 = new Thread(() -> {
            File[] files2 = new File[files.length / 4];
            int count = 0;
            for (int j = files.length / 4; j < files.length / 2; j++) {
                files2[count++] = files[j];
                if (count == files2.length) {
                    break;
                }
            }

            Document doc = null;
            try {
                doc = merge("/STEP-ProductInformation/Products", files2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                write(doc);
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Thread thread2 = new Thread(() -> {
            File[] files3 = new File[files.length / 4];
            int count = 0;
            for (int j = files.length / 2; j < files.length; j++) {
                files3[count++] = files[j];
                if (count == files3.length) {
                    break;
                }
            }
            Document doc = null;
            try {
                doc = merge("/STEP-ProductInformation/Products", files3);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                write(doc);
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Thread thread3 = new Thread(() -> {
            int help = 0;
            final int i = files.length - (files.length / 4) * 3;
            File[] files4 = new File[i];
            help = files.length - i;
            int count = 0;
            for (int j = help; j < files.length; j++) {
                files4[count++] = files[j];
                if (count == files4.length) {
                    break;
                }
            }
            Document doc = null;
            try {
                doc = merge("/STEP-ProductInformation/Products", files4);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                write(doc);
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        thread1.start();
        thread2.start();
        thread3.start();
        thread.join();
        thread1.join();
        thread2.join();
        thread3.join();
    }

    /**
     * Erstellen zwi XML-Dateien
     *
     * @param files
     * @throws Exception
     */
    public void createTwoXMLFile(File[] files) throws Exception {
        int count = 0;

        File[] files1 = new File[files.length / 2];
        File[] files2 = new File[files.length - (files.length / 2)];

        boolean isStartA = true;
        boolean isStartB = false;

        if (isStartA) {
            for (int j = 0; j < files.length / 2; j++) {
                files1[count++] = files[j];
                if (count == files1.length) {
                    break;
                }
            }
            Document doc = merge("/STEP-ProductInformation/Products", files1);
            write(doc);
            count = 0;
            isStartB = true;
            isStartA = false;
        }

        if (isStartB) {
            for (int j = files.length / 2; j < files.length; j++) {
                files2[count++] = files[j];
                if (count == files2.length) {
                    break;
                }
            }
            Document doc = merge("/STEP-ProductInformation/Products", files2);
            write(doc);
            count = 0;
            isStartB = false;
        }
    }

    /**
     * Erstellen Eine XML-Datei
     *
     * @param files
     * @throws Exception
     */
    public void createOneXMLFile(File[] files) throws Exception {
        int count = 0;

        File[] files1 = new File[files.length];

        for (int j = 0; j < files.length; j++) {
            files1[count++] = files[j];
            if (count == files1.length) {
                break;
            }
        }
        Document doc = merge("/STEP-ProductInformation/Products", files1);
        write(doc);
        count = 0;
    }

}

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Test {

    static String path = "C:/Beka/OMP/IDEA/Zusatzaufgaben/ThreadXMLDateien/XML/";
    static String path1 = "C:/Beka/OMP/IDEA/Zusatzaufgaben/ThreadXMLDateien/help/";
    public static void main(String[] args) throws Exception {

        XMLReadWriter xmlReadWriter = new XMLReadWriter();
        File file = new File(path);

        File[] files = file.listFiles();

        if(files.length > 0) {
            xmlReadWriter.createForXMLFile(files);

            boolean isReady = xmlReadWriter.isReady();

            if (isReady) {
                Files.move(Paths.get(path), Paths.get(path1), StandardCopyOption.REPLACE_EXISTING);
            }

            if (file.mkdir()) {
                file.createNewFile();
            }
        }else{
            throw new IOException("Fehlschlag: Array ist leer!");
        }
    }
}

/**
 Thread[] threads = new Thread[4];
 int m = files.length / threads.length;
 for (int i = 0; i < threads.length; i++) {
 int from = m*i;
 int to = (i+1 == threads.length) ? files.length : from + m;
 threads[i] = new Thread(new XMLReadWriter(files, from, to));
 threads[i].start();
 }

 for (int i = 0; i < threads.length; i++) {
 threads[i].join();
 }
 */
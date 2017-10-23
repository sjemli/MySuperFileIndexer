import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

public class FileProcessorTask implements Runnable {

    private Path filePath;
    private ConcurrentMap<String, Integer> map;
    private  CountDownLatch countDownLatch;

    public FileProcessorTask(Path p, ConcurrentMap<String, Integer> resultMap, CountDownLatch countDownLatch) {

        filePath = p;
        this.map = resultMap;
        this.countDownLatch = countDownLatch;
    }

    public void run() {

        Charset charset = Charset.forName("ASCII");

        try (BufferedReader reader = Files.newBufferedReader(filePath, charset)) {
            String line ;
            while ((line = reader.readLine()) != null) {
                processLine(line);
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
            countDownLatch.countDown();
        }
        countDownLatch.countDown();

    }

    public void processLine(String line) {
        StringBuilder word = new StringBuilder();
        int i = 0;
        String foundWord = "";
        Character c;
        while (i < line.length()) {
            c = line.charAt(i);
            if (Character.isAlphabetic(c) || c.isDigit(c)) {
                word.append(c);
                i++;
            } else {
                foundWord = word.toString();
                if (!foundWord.isEmpty()) {
                    int value = map.get(foundWord) == null ? 0 : map.get(foundWord);
                    map.put(foundWord, ++value);
                    word = new StringBuilder();
                }
                i++;
            }
        }
        foundWord = word.toString();
        if (!foundWord.isEmpty()) {
            int value = map.get(foundWord) == null ? 0 : map.get(foundWord);
            map.put(foundWord, ++value);
        }

    }
}
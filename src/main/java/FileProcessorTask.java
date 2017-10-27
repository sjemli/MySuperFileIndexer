import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class FileProcessorTask implements Callable<Map<String, Integer>> {

    private Path filePath;

    public FileProcessorTask(Path p) {

        filePath = p;

    }

    public Map<String, Integer> call() {

        Map<String, Integer> map = new HashMap<>();
        Charset charset = Charset.forName("ASCII");

        try (BufferedReader reader = Files.newBufferedReader(filePath, charset)) {
            String line ;
            while ((line = reader.readLine()) != null) {
                processLine(line, map);
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        return map;
    }

    public void processLine(String line, Map<String, Integer> map) {
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
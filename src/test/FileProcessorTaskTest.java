import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileProcessorTaskTest {


    private FileProcessorTask fileProcessorTask;
    private ConcurrentHashMap<String, Integer> resultMap;

    @BeforeAll
    public void init() {
        resultMap = new ConcurrentHashMap<>();
        fileProcessorTask = new FileProcessorTask(Paths.get(""),resultMap, new CountDownLatch(0));
    }

    @Test
    public void should_map_contains_occurrences_of_words_in_line() {

        //Given
        String line = "aaa b 5dsds ** df";

        //when
        fileProcessorTask.processLine(line);

        //then
        ConcurrentSkipListMap map = new ConcurrentSkipListMap<>();
        map.put("aaa", 1);
        map.put("b", 1);
        map.put("5dsds", 1);
        map.put("df", 1);

        //then
        Assertions.assertEquals(map, resultMap);



    }

}
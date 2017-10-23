import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchTask implements Callable<ConcurrentHashMap<String, Integer>> {

    private static final Integer THREAD_POOL_SIZE = 3;

    private ConcurrentHashMap<String, Integer> occurrenceMap;
    private ExecutorService executor;
    private String inputPath;

    public SearchTask(String inputPath) {

        this.inputPath = inputPath;
        executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        occurrenceMap = new ConcurrentHashMap<>();

    }

    @Override
    public ConcurrentHashMap<String, Integer> call() throws Exception {

        List<Path> textFilePathsList = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(inputPath))) {

            textFilePathsList = paths.filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".txt")).collect(Collectors.toList());


        }

        CountDownLatch countDownLatch = new CountDownLatch(textFilePathsList.size());

        textFilePathsList.forEach(p -> {
            System.out.println("Submitting Processor task..");
            executor.submit(new FileProcessorTask(p, occurrenceMap, countDownLatch));
        });

        executor.shutdown();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return occurrenceMap;
    }


}

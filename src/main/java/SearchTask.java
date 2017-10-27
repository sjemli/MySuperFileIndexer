import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

public class SearchTask implements Callable<Map<String, Integer>> {

    private static final Integer THREAD_POOL_SIZE = 3;

    private ConcurrentHashMap<String, Integer> occurrenceMap;
    private ExecutorService executor;
    private String inputPath;

    public SearchTask(String inputPath) {

        this.inputPath = inputPath;
        executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        occurrenceMap = new ConcurrentHashMap<>();

    }



    public List<Future<Map<String, Integer>>> submitProcessorTasks(Path folder) throws IOException {

        List<Future<Map<String, Integer>>> futuresList = new CopyOnWriteArrayList<>();

        DirectoryStream<Path> stream = Files.newDirectoryStream(folder);
        Set<Path> visitedPaths = new HashSet<>();
        for (Path path : stream) {
            Path tempPath = path;
            if (Files.isSymbolicLink(path)) {

                tempPath = Files.readSymbolicLink(path);
                System.out.println("Symbolic Link = " + path.toString() + " To = " + tempPath.toString());

            }
            if (!visitedPaths.contains(tempPath)) { // to check if symbolicLink doesn't point to already visited files/directory
                System.out.println(tempPath.getFileName());
                if (Files.isRegularFile(tempPath) && tempPath.toString().endsWith(".txt")) {
                    System.out.println("Submitting Processor task..");
                    futuresList.add(executor.submit(new FileProcessorTask(tempPath)));

                } else if (Files.isDirectory(tempPath)) {

                    futuresList.addAll(submitProcessorTasks(tempPath));

                }
            }
        }

        stream.close();
        return futuresList;
    }

    @Override
    public Map<String, Integer> call() {

        List<Future<Map<String, Integer>>> futures = null;
        try {
            futures = submitProcessorTasks(Paths.get(inputPath));
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            executor.shutdown();
        }
        List<Map<String, Integer>> mapsList = new ArrayList<>();
        for (Future<Map<String, Integer>> future : futures) {

            Map<String, Integer> map = new HashMap<>();
            try {
                map = future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            mapsList.add(map);
        }

        return merge(mapsList);
    }

    public Map<String, Integer> merge(List<Map<String, Integer>> mapsList) {

        return mapsList.stream().flatMap(map -> map.entrySet().stream()).collect(groupingBy(Map.Entry::getKey, summingInt(Map.Entry::getValue)));
    }
}
//    @Override
//    public ConcurrentHashMap<String, Integer> call() throws Exception {
//
//        List<Path> textFilePathsList = new ArrayList<>();
//        try (Stream<Path> paths = Files.walk(Paths.get(inputPath))) {
//
//            textFilePathsList = paths.filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".txt")).collect(Collectors.toList());
//
//
//        }
//
//        CountDownLatch countDownLatch = new CountDownLatch(textFilePathsList.size());
//
//        textFilePathsList.forEach(p -> {
//            System.out.println("Submitting Processor task..");
//            executor.submit(new FileProcessorTask(p, occurrenceMap, countDownLatch));
//        });
//
//        executor.shutdown();
//        try {
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return occurrenceMap;
//    }
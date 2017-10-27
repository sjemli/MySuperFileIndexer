import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class FileIndexer {

    public static LinkedHashMap sortByValue(Map<String, Integer> map) {

        return map.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (key1, key2) -> key1,//merge function to ignore duplicates
                        LinkedHashMap::new
                ));

    }

    public static void printMap(Map<String, Integer> map, int numberToPrint) {

        int counter = 0;
        for (String key : map.keySet()) {
            System.out.println(key + " - " + map.get(key));
            if (++counter == 10) break;
        }
    }
    public static void main(String[] args) {

//        if (args == null || args.length == 0 ||  args.length > 1){
//            throw new IllegalArgumentException();
//        }
//
//        String inputPath = args[0];

        String inputPath = "C:\\Users\\Seif_PC\\Desktop\\fileIndexer";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Map<String, Integer>> result = executorService.submit(new SearchTask(inputPath));
        Map<String, Integer> resultMap = new HashMap<>();

        try {
            resultMap = result.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
        printMap(sortByValue(resultMap), 10);


    }
}

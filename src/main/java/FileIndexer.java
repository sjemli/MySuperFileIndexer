import java.util.concurrent.*;

public class FileIndexer {


    public static void main(String[] args) {

//        if (args == null || args.length == 0 ||  args.length > 1){
//            throw new IllegalArgumentException();
//        }
//
//        String inputPath = args[0];

        String inputPath = "C:\\Users\\Seif_PC\\Desktop\\fileIndexer";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<ConcurrentHashMap<String, Integer>> result = executorService.submit(new SearchTask(inputPath));
        ConcurrentHashMap<String, Integer> resultMap = new ConcurrentHashMap<>();

        try {
            resultMap = result.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
        System.out.println("Size" + resultMap.size());
        System.out.println(resultMap);

    }
}

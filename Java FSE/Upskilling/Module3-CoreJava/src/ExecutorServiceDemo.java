import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorServiceDemo {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<Integer>> results = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            Callable<Integer> task = () -> {
                int sum = 0;
                for (int j = 1; j <= taskId * 10; j++) {
                    sum += j;
                }
                return sum;
            };
            results.add(executor.submit(task));
        }

        try {
            for (int i = 0; i < results.size(); i++) {
                System.out.println("Result of Task " + (i + 1) + ": " + results.get(i).get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}

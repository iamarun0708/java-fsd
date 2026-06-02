import java.util.concurrent.atomic.AtomicInteger;

public class VirtualThreadsDemo {
    public static void main(String[] args) throws InterruptedException {
        int numThreads = 100_000;
        AtomicInteger counter = new AtomicInteger(0);

        long start = System.currentTimeMillis();

        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = Thread.startVirtualThread(() -> {
                counter.incrementAndGet();
            });
        }

        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }

        long end = System.currentTimeMillis();
        System.out.println("Launched and joined " + counter.get() + " virtual threads.");
        System.out.println("Time taken: " + (end - start) + " ms");
    }
}

class MyRunnable implements Runnable {
    private String name;

    public MyRunnable(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(name + " is running: count " + i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println(name + " was interrupted.");
            }
        }
    }
}

public class ThreadDemo {
    public static void main(String[] args) {
        Thread thread1 = new Thread(new MyRunnable("Thread-1"));
        Thread thread2 = new Thread(new MyRunnable("Thread-2"));

        thread1.start();
        thread2.start();
    }
}

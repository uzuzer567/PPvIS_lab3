package controller;
import java.util.concurrent.ConcurrentLinkedQueue;
import static java.lang.Thread.currentThread;

public class CalculationThread implements Runnable {
    private Double x;
    private final Double rightThreshold;
    private final ConcurrentLinkedQueue<Double> queue;

    public CalculationThread(Double leftThreshold, Double rightThreshold, ConcurrentLinkedQueue<Double> queue) {
        this.x = leftThreshold;
        this.rightThreshold = rightThreshold;
        this.queue = queue;
    }

    public void run() {
        while (x <= rightThreshold && !Thread.interrupted()) {
            double y = 2 * x - 5;
            queue.add(y);
            try {
                Thread.sleep(20);
            }
            catch (InterruptedException e) {
                break;
            }
            x += 0.01;
        }
        currentThread().interrupt();
    }
}

package controller;

import view.Table;
import model.TableRecord;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Thread.currentThread;

public class SearchValueFunctionsThread implements Runnable {
    private Double x;
    private Double n;
    private final ConcurrentLinkedQueue<Double> queue;
    private final Table table;

    public SearchValueFunctionsThread(Double n, Double leftThreshold, ConcurrentLinkedQueue<Double> queue, Table table) {
        this.x = leftThreshold;
        this.n = n;
        this.queue = queue;
        this.table = table;
    }

    public void run() {
        while (x <= n && !Thread.interrupted()) {
            double y = 2 * x - 5;
            table.updateTable(new TableRecord(x, y));
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

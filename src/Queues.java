package src;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class Queues {
    private ArrayList<Semaphore> queues;

    public Queues(int transiciones){
        queues = new ArrayList<>(transiciones);
        for (int i = 0; i < transiciones; i++) {
            queues.add(new Semaphore(0));
        }
    }

    public RealVector quienesEstan() {
        int n = queues.size();
        double[] hilosEsperando = new double[n];
        for (int i = 0; i < n; i++) {
            Semaphore semaphore = queues.get(i);
            hilosEsperando[i] = semaphore.hasQueuedThreads() ? 1.0 : 0.0;
        }
        return new ArrayRealVector(hilosEsperando);
    }

    public void acquire(int transition) {
        try {
            queues.get(transition).acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void release(double maxValue) {
       queues.get((int)maxValue).release();
    }

}

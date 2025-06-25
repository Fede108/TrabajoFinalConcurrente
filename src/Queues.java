package src;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

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

    /**
     * Despierta un hilo cualquiera cuyo semáforo esté marcado con 1 en el vector resultado.
     * Si hay varias posiciones con 1, elige una al azar y hace release() de su semáforo.
     * Si no hay ninguna con valor 1, no hace nada.
     */
    public void release(RealVector resultado) {
        List<Integer> candidatos = new ArrayList<>();
        for (int i = 0; i < resultado.getDimension(); i++) {
            if (resultado.getEntry(i) == 1.0) {
                candidatos.add(i);
            }
        }
        if (!candidatos.isEmpty()) {
            // Elige índice aleatorio entre los candidatos
            int elegido   = candidatos.get(ThreadLocalRandom.current().nextInt(candidatos.size()));
            Semaphore sem = queues.get(elegido);
            sem.release();
            // Opcional: imprimir o log para depuración
            System.out.printf("Release en semáforo de T%d%n", elegido);
        }
    }

    /**
     * Libera todos los hilos que estén bloqueados en espera en cada semáforo de la cola. 
     */
    public void releaseAll() {
        for (int i = 0; i < queues.size(); i++) {
            Semaphore sem = queues.get(i);
        
            int esperando = sem.getQueueLength();
            for (int k = 0; k < esperando; k++) {
                sem.release();
            }
            if (esperando > 0) {
                System.out.printf("ReleaseAll: liberados %d hilos en semáforo T%d%n", esperando, i);
            }
        }
    }


}

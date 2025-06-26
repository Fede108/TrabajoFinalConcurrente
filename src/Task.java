package src;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Task implements Runnable{
    private Monitor monitor;
    private ArrayList<Integer> transiciones;
    private boolean segmentoFinal;
    static private AtomicInteger nro_invariante = new AtomicInteger(0);

    public Task(Monitor monitor, List<Integer> transiciones, boolean segmentoFinal){
        this.monitor = monitor;
        this.transiciones = new ArrayList<>(transiciones);
        this.segmentoFinal = segmentoFinal;
    }

    @Override
    public void run() {
        // Bucle: recorre la lista de transiciones y las intenta disparar.
        int idx = 0;
        while (true) {

            int t = transiciones.get(idx);
            if(!monitor.fireTransition(t)){
                break;
            }

            if (segmentoFinal) {
                nro_invariante.getAndIncrement();
                if (nro_invariante.get()==200) {
                    monitor.fireTransition(-1);
                    break;
                }
            }
            // Avanzar al siguiente en la lista (cíclico)
            idx = (idx + 1) % transiciones.size();
        }

       System.out.println("Hilo " + Thread.currentThread().getName() + " finalizado.");
    }
}

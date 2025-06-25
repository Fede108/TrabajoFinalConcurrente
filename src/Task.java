package src;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Task implements Runnable{
    private Monitor monitor;
    private ArrayList<Integer> transiciones;
    private boolean hiloFinal;
    static private AtomicInteger nro_invariante = new AtomicInteger(0);

    public Task(Monitor monitor, List<Integer> transiciones, boolean hiloFinal){
        this.monitor = monitor;
        this.transiciones = (ArrayList<Integer>) transiciones;
        this.hiloFinal = hiloFinal;
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

            if (hiloFinal) {
                nro_invariante.getAndIncrement();
                if (nro_invariante.get()==10) {
                    monitor.fireTransition(-1);
                    break;
                }
            }
            // Avanzar al siguiente en la lista (cíclico)
            idx = (idx + 1) % transiciones.size();
        }
    }
}

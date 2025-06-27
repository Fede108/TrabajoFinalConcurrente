package src;

import java.util.ArrayList;
import java.util.List;

public class Task implements Runnable{
    private Monitor monitor;
    private ArrayList<Integer> transiciones;

    public Task(Monitor monitor, List<Integer> transiciones){
        this.monitor = monitor;
        this.transiciones = new ArrayList<>(transiciones);
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
            // Avanzar al siguiente en la lista (cíclico)
            idx = (idx + 1) % transiciones.size();
        }

       System.out.println("Hilo " + Thread.currentThread().getName() + " finalizado.");
    }
}

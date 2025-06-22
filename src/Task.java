package src;

import java.util.ArrayList;
import java.util.List;

public class Task implements Runnable{
    private Monitor monitor;
    private ArrayList<Integer> transiciones;

    public Task(Monitor monitor, List<Integer> transiciones){
        this.monitor = monitor;
        this.transiciones = (ArrayList<Integer>) transiciones;
    }

    @Override
    public void run() {
        // Bucle: recorre la lista de transiciones y las intenta disparar.
        int contador = 0;
        int idx = 0;
        while (contador < 20) {
            int t = transiciones.get(idx);
            monitor.fireTransition(t);
            
            if (transiciones.get(idx) == transiciones.getLast()) {
                contador++;    // Numero de invariantes completados
            }
            // Avanzar al siguiente en la lista (cíclico)
            idx = (idx + 1) % transiciones.size();
        }
    }
}

package src;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class Monitor implements MonitorInterface {
    
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition[] conds;
    
    private RedDePetri red;
    private boolean disparoExitoso;
    private RealVector sensibilizadas;
    private RealVector quienesEstan;
    private boolean terminarEjecucion;
    
    public Monitor(RedDePetri red) {
        this.red = red;
        disparoExitoso = true;
        terminarEjecucion = false;

        quienesEstan = new ArrayRealVector(red.getNumTransiciones());

        conds = new Condition[red.getNumTransiciones()];
        for (int i = 0; i < red.getNumTransiciones(); i++) {
            conds[i] = lock.newCondition();
        }
    }

    private void quienesEstan() {
        for (int i = 0; i < red.getNumTransiciones(); i++) {
            quienesEstan.setEntry(i, lock.hasWaiters(conds[i]) ? 1.0 : 0.0);
        }
    }

    @Override
    public boolean fireTransition(int t) {
        lock.lock();
        System.out.println("monitor ocupado " + t);
        // Si t es -1, se indica que se debe terminar la ejecución
        try {
        
            if (t == -1){
                    // terminar ejecución: despertar a todos
                    for (Condition c : conds) c.signalAll();
                    terminarEjecucion = true;
                    return true;
            }

            while (true){

                if (terminarEjecucion) {
                    for (Condition c : conds) c.signalAll();
                    return false;
                }

                disparoExitoso = red.EcuacionDeEstado(t);
                if (disparoExitoso) {
                   // mostrar marcado actual
                    System.out.println(t + " disparo exitoso");
                    red.imprimirMarcado();
                
                    sensibilizadas = red.getSensibilizadas();
                    System.out.println("sensibilizadas " +  sensibilizadas);
                   
                    quienesEstan();
                    System.out.println("quienesEstan " + quienesEstan);

                    RealVector resultado = sensibilizadas.ebeMultiply(quienesEstan);
                    // resultado es la AND de las transiciones sensibilizadas y las q tienen hilos
                    // para disparar esperando

                    if (resultado.getMaxValue() > 0) {
                        // Elegir un índice aleatorio donde resultado es mayor que 0
                        List<Integer> indices = new ArrayList<>();
                        for (int i = 0; i < resultado.getDimension(); i++) {
                            if (resultado.getEntry(i) > 0) {
                                indices.add(i);
                            }
                        }
                        if (!indices.isEmpty()) {
                            int elegido = indices.get((int) (Math.random() * indices.size()));
                            conds[elegido].signal();
                        }
                    }

                    return true;
                } else {
                    System.out.println(t + " disparo fallido");
                    System.out.println(t + " monitor liberado");
                    // disparo fallido se espera en la variable de condición t
                    try {
                        conds[t].await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }   
                    // al volver de await tenemos el lock de nuevo y repetimos
                    System.out.println("monitor ocupado " + t);  
                }
            }
        } finally {
            System.out.println(t + " monitor liberado");
            lock.unlock();
        }
    }
}
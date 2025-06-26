package src;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class Monitor implements MonitorInterface {
    
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition[] conds;
    private final Condition[] esperas;
    
    private RedDePetri red;
    private Politica politica;
    private RealVector sensibilizadas;
    private RealVector quienesEstan;
    private boolean terminarEjecucion;
    
    public Monitor(RedDePetri red, Politica politica) {
        this.red = red;
        this.politica = politica;
        terminarEjecucion = false;

        quienesEstan = new ArrayRealVector(red.getNumTransiciones());

        conds   = new Condition[red.getNumTransiciones()];
        esperas = new Condition[red.getNumTransiciones()];
        for (int i = 0; i < red.getNumTransiciones(); i++) {
            conds[i]   = lock.newCondition();
            esperas[i] = lock.newCondition();
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
        System.out.printf("T%d monitor ocupado \n", t);
        // Si t es -1, se indica que se debe terminar la ejecución
        try {
        
            if (t == -1){
                    // terminar ejecución: despertar a todos
                    for (Condition e : esperas) e.signalAll();
                    for (Condition c : conds) c.signalAll();
                    terminarEjecucion = true;
                    return true;
            }

            while (true){

                if (terminarEjecucion) {
                    for (Condition e : esperas) e.signalAll();
                    for (Condition c : conds) c.signalAll();
                    return false;
                }

                if (red.estaSensibilizada(t) && red.testVentanadeTiempo(t)) {

                    red.EcuacionDeEstado(t);
                    red.imprimirMarcado();
                
                    sensibilizadas = red.getSensibilizadas();
                    System.out.println("Transiciones Sensibilizadas: " +  sensibilizadas);
                   
                    quienesEstan();
                    System.out.println("Transiciones con Hilos esperando: " + quienesEstan);

                    RealVector resultado = sensibilizadas.ebeMultiply(quienesEstan);
                    // resultado es la AND de las transiciones sensibilizadas y las q tienen hilos
                    // para disparar esperando

                    Integer elegido = politica.seleccionarTransicion(resultado);
                    if(elegido != null) {
                        conds[elegido].signal();
                    }  
                    return true;
                   
                } else {
                    if (red.estaSensibilizada(t)) {
                       
                        int hilosEsperando = red.hilosEsperando(t);
                            if(hilosEsperando>0){
                                System.out.printf("T%d con hilos esperando (Thread: %s)\n", t, Thread.currentThread().getName());
                                try {
                                    esperas[t].await(hilosEsperando, TimeUnit.MILLISECONDS);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    conds[t].await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }   
                            } else{
                                System.out.printf("T%d fuera de ventana de tiempo (Thread: %s)\n", t, Thread.currentThread().getName());
                                try {
                                    esperas[t].await(red.getSleepTime(t), TimeUnit.MILLISECONDS);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            
                    } else {
                        System.out.printf("T%d no sensibilizada \n", t);
                        System.out.printf("T%d monitor liberado \n", t);
                        // disparo fallido se espera en la variable de condición t
                        try {
                            conds[t].await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }   
                        // al volver de await tenemos el lock de nuevo y repetimos
                        System.out.printf("T%d monitor ocupado \n", t);
                    }
                }
            }
        } finally {
            System.out.printf("T%d monitor liberado \n", t);
            lock.unlock();
        }
    }
}
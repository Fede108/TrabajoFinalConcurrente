package src;


import java.util.concurrent.Semaphore;
import org.apache.commons.math3.linear.RealVector;

public class Monitor implements MonitorInterface {

    private final Semaphore mutex = new Semaphore(1, false);
    private RedDePetri red;
    private Queues queues;
    private boolean disparoExitoso;
    private RealVector sensibilizadas;
    private RealVector quienesEstan;
    private boolean terminarEjecucion;
    private boolean loocked =  false;

    public Monitor(RedDePetri red, Queues queues) {
        this.red = red;
        this.queues = queues;
        disparoExitoso = true;
        terminarEjecucion = false;
    }

    @Override
    public boolean fireTransition(int transition) {
        RealVector resultado;
        
        try {
            mutex.acquire();
            loocked = true;
            System.out.println("monitor ocupado");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
        
            while (true) {

                if (transition == -1) {
                    terminarEjecucion = true;
                    queues.releaseAll();
                    return true;
                }

                if (terminarEjecucion) {
                    return false;
                }

                disparoExitoso = red.EcuacionDeEstado(transition);
                if (disparoExitoso) {
                   // mostrar marcado actual
                   // System.out.println(transition);
                   // System.out.println("disparo exitoso");
                   // red.imprimirMarcado();
                
                    sensibilizadas = red.getSensibilizadas();
                    quienesEstan = queues.quienesEstan();
                    resultado = sensibilizadas.ebeMultiply(quienesEstan);
                    // resultado es la AND de las transiciones sensibilizadas y las q tienen hilos
                    // para disparar esperando

                    if (resultado.getMaxValue() > 0) {
                        queues.release(resultado);
                        return true;
                    }
                    return true;
                } else {
                    if (loocked) {
                        loocked = false;
                        System.out.println("monitor liberado");
                        mutex.release();
                    }
                    
                    queues.acquire(transition);
                    try {
                        mutex.acquire();
                        loocked = true;
                        System.out.println("monitor ocupado");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            System.out.println("monitor liberado");
            if (loocked) {
                loocked = false;
                mutex.release();
                
            }
        }
    }
}
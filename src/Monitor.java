package src;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class Monitor implements MonitorInterface {

    private final Semaphore mutex = new Semaphore(1,false);
    private RedDePetri red;
    private Queues queues;
    private boolean disparoExitoso;
    private RealVector sensibilizadas;
    private RealVector quienesEstan;

    public Monitor(RedDePetri red, Queues queues){
        this.red = red;
        this.queues = queues;
        disparoExitoso = true;
    }
    
    @Override
    public boolean fireTransition(int transition) {
        RealVector resultado;
        
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        try {

            while (true) {
                disparoExitoso = red.EcuacionDeEstado(transition);
                if(disparoExitoso)
                { 
                    System.out.println("Transicion " + transition);
                    // mostrar marcado actual
                    red.imprimirMarcado();
                    
                    sensibilizadas = red.getSensibilizadas();
                    quienesEstan   = queues.quienesEstan();
                    resultado = sensibilizadas.ebeMultiply(quienesEstan);

                    if (resultado.getMaxValue() > 0) {
                        queues.release(resultado);
                        return true;
                    } 

                    return true;
                } 
                else 
                {
                    mutex.release();
                    queues.acquire(transition);
                    try {
                        mutex.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        finally {
            mutex.release();
        } 
    }
}
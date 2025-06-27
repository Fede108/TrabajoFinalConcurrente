package src;
import java.io.IOException;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;


public class RedDePetri {
    private final RealMatrix matrizIncidencia;
    private RealVector marcado;
    private RealVector vectorDisparos;
    private Log logger;
    private RealVector timeAlfa;
    private RealVector timeStamp;
    private RealVector flagEspera;
    private double inicioPrograma;
    private int completados = 0; // Variable to track completed invariants

    /*
     * Constructor de la clase
     * 
     * @param matrizIncidencia matriz de incidencia de la RdP
     * @param marcado marcado incial de la RdP
     */

    public RedDePetri(double[][] matrizIncidencia, double[] marcado, double[] sensibilizadasConTiempo)
    {
        this.matrizIncidencia = MatrixUtils.createRealMatrix(matrizIncidencia);
        this.marcado = MatrixUtils.createRealVector(marcado);
        this.vectorDisparos = new ArrayRealVector(this.matrizIncidencia.getColumnDimension());


        timeAlfa   = MatrixUtils.createRealVector(sensibilizadasConTiempo);
        timeStamp  = new ArrayRealVector(matrizIncidencia[0].length);
        flagEspera = new ArrayRealVector(matrizIncidencia[0].length);
        inicioPrograma = System.currentTimeMillis();


        try {
            logger = new Log("transiciones.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNumTransiciones()
    {
        return matrizIncidencia.getColumnDimension();
    }


    public RealVector getSensibilizadas()
    {
        int numTrans = matrizIncidencia.getColumnDimension(); 
        RealVector sensibilizadas = new ArrayRealVector(numTrans);

        for(int transicion = 0; transicion<numTrans; transicion++)
        {
            RealVector columna   = matrizIncidencia.getColumnVector(transicion);
            RealVector resultado = marcado.add(columna);
            boolean    valido    = true;
            for(double valor: resultado.toArray())
            {
                if(valor<0)
                {
                    valido = false;
                    break;
                }
            }

            sensibilizadas.setEntry(transicion, valido ? 1.0 : 0.0);
        }
        return sensibilizadas;
    }

    public void imprimirMarcado()
    {
        System.out.print("Marcado actual:[ ");
        for(double token: marcado.toArray())
        {
            System.out.print((int) token + " ");
        }
        System.out.println("]");
    }

    public boolean EcuacionDeEstado(int transicion)
    {
        RealVector ecuacion, sensibilizadas, nuevasSen;
        vectorDisparos.setEntry(transicion, 1);
        ecuacion = matrizIncidencia.operate(vectorDisparos).add(marcado);
        vectorDisparos.setEntry(transicion, 0);

        for (double v : ecuacion.toArray()) 
        {
            if (v < 0) return false;
        }
        System.out.printf("T%d disparada(Thread: %s)\n", transicion, Thread.currentThread().getName());
        
        sensibilizadas  = getSensibilizadas();
        marcado         = ecuacion.copy();
        nuevasSen       = getSensibilizadas();
        iniciarTiempo(sensibilizadas, nuevasSen);
        setNumeroInvariantes(transicion);
        logger.log("T" + transicion);
        return true;
    }

    private void iniciarTiempo(RealVector anterior, RealVector nuevo){
        RealVector delta = nuevo.subtract(anterior);
        for(int i=0; i<delta.getDimension(); i++){
            if (timeAlfa.getEntry(i) > 0.0) 
            {
                if(delta.getEntry(i) > 0)
                {
                    timeStamp.setEntry(i, System.currentTimeMillis());

                    double time = System.currentTimeMillis() - inicioPrograma;
                    System.out.printf("T%d tiempo de inicio %f ms\n", i, time);
                } 
            }
        }
    }

    public int hilosEsperando(int t) {
        if (flagEspera.getEntry(t) == 1.0) {
            // Tiempo espera minimo hasta que se desensibiliza la transición
            return (int) Math.max(0, getSleepTime(t) - 50); // 50 ms de margen
        }
        return 0;
    }

    public boolean testVentanadeTiempo(int t){
        if (timeAlfa.getEntry(t) <= 0.0) {
            return true; // No hay restricción de tiempo para esta transición
        }

        double tiempoTranscurrido = System.currentTimeMillis() - timeStamp.getEntry(t);
        System.out.printf("T%d tiempo transcurrido %f ms (Thread: %s) \n", t, tiempoTranscurrido , Thread.currentThread().getName());
        if(tiempoTranscurrido >= timeAlfa.getEntry(t)){
            flagEspera.setEntry(t, 0.0);
            return true; // La transición puede dispararse
        } else {
            return false; // La transición no puede dispararse
        }
    }

    public boolean estaSensibilizada(int t) {
        return getSensibilizadas().getEntry(t) > 0.0;
    }

    public int getSleepTime(int t) {
        double time = timeStamp.getEntry(t) + timeAlfa.getEntry(t) - System.currentTimeMillis();
        flagEspera.setEntry(t, 1.0);
        return (int) Math.max(0, time);
    }

    public void setNumeroInvariantes(int t) {
        if (t == 11) {
            completados++;
        }
    }

    public boolean getInvariantesCompletados(){
        return completados == 150; // Verifica si se han completado los 15 invariantes
    }

    public boolean verificarInvariantes(RealVector marcado)
    {
        double suma1 = marcado.getEntry(0) + marcado.getEntry(1) + marcado.getEntry(10) + marcado.getEntry(11) + marcado.getEntry(3) + marcado.getEntry(4) +
        marcado.getEntry(5) + marcado.getEntry(7) + marcado.getEntry(8) + marcado.getEntry(9);

        double suma2 = marcado.getEntry(1) + marcado.getEntry(2);

        double suma3 = marcado.getEntry(10) + marcado.getEntry(4) + marcado.getEntry(5) + marcado.getEntry(6) + marcado.getEntry(7) + marcado.getEntry(8) + marcado.getEntry(9);

        if(suma1 == 3.0 && suma2 == 1.0 && suma3 == 1.0)
        {
            return true;
        }

        return false;

    }
}

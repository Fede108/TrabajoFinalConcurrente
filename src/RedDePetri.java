package src;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;




public class RedDePetri {
    private final RealMatrix matrizIncidencia;
    private RealVector marcado;
    private RealVector marcadoInicial;
    private RealVector vectorDisparos;
    
    /*
     * Constructor de la clase
     * 
     * @param matrizIncidencia matriz de incidencia de la RdP
     * @param marcado marcado incial de la RdP
     */



    public RedDePetri(double[][] matrizIncidencia, double[] marcado)
    {
        this.matrizIncidencia = MatrixUtils.createRealMatrix(matrizIncidencia);
        this.marcado = MatrixUtils.createRealVector(marcado);
        this.marcadoInicial = this.marcado.copy();
        this.vectorDisparos = new ArrayRealVector(this.matrizIncidencia.getRowDimension());

    }


    private RealVector getTransition(int transicion)
    {
        double[] vector = new double[matrizIncidencia.getRowDimension()];
        vector[transicion] = 1.0;
        return MatrixUtils.createRealVector(vector);
    }

    public List<Integer> getSensibilizadas()
    {
        List<Integer> sensibilizadas = new ArrayList<>();
        for(int transicion = 0; transicion<matrizIncidencia.getRowDimension(); transicion++)
        {
            RealVector resultado = marcado.add(matrizIncidencia.getRowVector(transicion));
            boolean valido = true;
            for(double valor: resultado.toArray())
            {
                if(valor<0)
                {
                    valido = false;
                    break;
                }
            }
            if(valido)
            {
                sensibilizadas.add(transicion);
            }
        }
        return sensibilizadas;
    }

    public boolean disparar(int transicion)
    {
        List<Integer> sensibilizadas = getSensibilizadas();
        if(!sensibilizadas.contains(transicion))
        {
            return false;
        }

        RealVector fila  = matrizIncidencia.getRowVector(transicion);
        marcado = marcado.add(fila);
        vectorDisparos.addToEntry(transicion,1); //suma 1 
        return true;
    }

    public void imprimirMarcado()
    {
        System.out.print("Marcado actual: ");
        for(double token: marcado.toArray())
        {
            System.out.print((int) token + " ");
        }
        System.out.println();
    }

    public RealVector EcuacionDeEstado()
    {
        RealVector ecuacion = matrizIncidencia.preMultiply(vectorDisparos).add(marcadoInicial);
        return ecuacion;
    }

}

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
        this.vectorDisparos = new ArrayRealVector(this.matrizIncidencia.getColumnDimension());
    }


    private RealVector getTransition(int transicion)
    {
        double[] vector = new double[matrizIncidencia.getRowDimension()];
        vector[transicion] = 1.0;
        return MatrixUtils.createRealVector(vector);
    }

    public RealVector getSensibilizadas()
    {
        int numTrans = matrizIncidencia.getColumnDimension(); // 6
        RealVector sensibilizadas = new ArrayRealVector(numTrans);

        for(int transicion = 0; transicion<numTrans; transicion++)
        {
            RealVector columna   = matrizIncidencia.getColumnVector(transicion);
            RealVector resultado = marcado.add(columna);
            boolean valido = true;
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
        System.out.print("Marcado actual: ");
        for(double token: marcado.toArray())
        {
            System.out.print((int) token + " ");
        }
        System.out.println();
    }

    public boolean EcuacionDeEstado(int transicion)
    {
        vectorDisparos.setEntry(transicion, 1);
        RealVector ecuacion = matrizIncidencia.operate(vectorDisparos).add(marcado);
        vectorDisparos.setEntry(transicion, 0);
        for (double v : ecuacion.toArray()) {
            if (v < 0) return false;
        }
        marcado = ecuacion.copy();
        return true;
    }
}

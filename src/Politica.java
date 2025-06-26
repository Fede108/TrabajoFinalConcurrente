package src;

import java.util.*;

import org.apache.commons.math3.linear.RealVector;

public class Politica {
    enum TipoPolitica {
        ALEATORIA,
        PRIORIZADA
    }

    public enum TipoProceso {
        SIMPLE,
        INTERMEDIO,
        COMPLEJO
    }

    private TipoPolitica tipo;

    private Random random;
    private HashMap<TipoProceso, Integer> mapaClasificaion;

    public Politica(TipoPolitica tipo) {

        this.tipo = tipo; // iniciliza la politica con alguno de los 2 tipos
        this.random = new Random();
        this.mapaClasificaion = new HashMap<>();

        mapaClasificaion.put(TipoProceso.SIMPLE, 5);
        mapaClasificaion.put(TipoProceso.INTERMEDIO, 2);
        mapaClasificaion.put(TipoProceso.COMPLEJO, 7);

    }

    public TipoProceso seleccionarModo(RealVector resultado) {
        if (tipo == TipoPolitica.ALEATORIA) {
            return seleccionarAleatorio(resultado);
        } else if (tipo == TipoPolitica.PRIORIZADA) {
            return seleccionarPriorizado(resultado);
        } else {
            throw new IllegalArgumentException("Política desconocida");
        }
    }

    private TipoProceso seleccionarAleatorio(RealVector resultado) {
        /*
         * Recibe como parametro el Vector resultado,contiene las transiciones
         * sensibilizadas
         * y que tienen hilos esperando
         */
        List<TipoProceso> disponibles = new ArrayList<>();
        // crea un ArrayList para almacenar los tipos de Procesos que estan habilitados
        // segun el vector
        for (Map.Entry<TipoProceso, Integer> entry : mapaClasificaion.entrySet()) {
            if (resultado.getEntry(entry.getValue()) == 1.0) {
                disponibles.add(entry.getKey());
                /*
                 * verifica si el valor de la posicion del vector == 1 y dependiendo cuales
                 * tenga en 1, va a agregar el tipo de proceso a la nuevo ArrayList
                 * 
                 */

            }
        }

        if (disponibles.isEmpty())
            return null;

        return disponibles.get(random.nextInt(disponibles.size()));
        // Selecciona un proceso al azar usando un Random

    }

    private TipoProceso seleccionarPriorizado(RealVector resultado) {
        if (resultado.getEntry(mapaClasificaion.get(TipoProceso.SIMPLE)) == 1.0) {
            return TipoProceso.SIMPLE;
        } else if (resultado.getEntry(mapaClasificaion.get(TipoProceso.INTERMEDIO)) == 1.0) {
            return TipoProceso.INTERMEDIO;

        } else if (resultado.getEntry(mapaClasificaion.get(TipoProceso.COMPLEJO)) == 1.0) {
            return TipoProceso.COMPLEJO;
        }
        return null;
    }

    public int getTransicion(TipoProceso tipo) {
        return mapaClasificaion.get(tipo);
    }
}

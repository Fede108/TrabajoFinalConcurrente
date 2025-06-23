package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    public Politica(TipoPolitica tipo) {

        this.tipo = tipo; // iniciliza la politica con alguno de los 2 tipos
        this.random = new Random();
    }

    public TipoProceso seleccionarModo(boolean simpleHabilitado, boolean intermedioHabilitado,
            boolean complejoHabilitado) {
        if (tipo == TipoPolitica.ALEATORIA) {
            return seleccionarAleatorio(simpleHabilitado, intermedioHabilitado, complejoHabilitado);
        } else if (tipo == TipoPolitica.PRIORIZADA) {
            return seleccionarPriorizado(simpleHabilitado, intermedioHabilitado, complejoHabilitado);
        } else {
            throw new IllegalArgumentException("Política desconocida");
        }
    }

    private TipoProceso seleccionarAleatorio(boolean simple, boolean intermedio, boolean complejo) {
        List<TipoProceso> disponibles = new ArrayList<>();
        if (simple)
            disponibles.add(TipoProceso.SIMPLE);
        if (intermedio)
            disponibles.add(TipoProceso.INTERMEDIO);
        if (complejo)
            disponibles.add(TipoProceso.COMPLEJO);

        if (disponibles.isEmpty())
            return null;

        return disponibles.get(random.nextInt(disponibles.size()));
    }

    private TipoProceso seleccionarPriorizado(boolean simple, boolean intermedio, boolean complejo) {
        if (simple)
            return TipoProceso.SIMPLE;
        if (intermedio)
            return TipoProceso.INTERMEDIO;
        if (complejo)
            return TipoProceso.COMPLEJO;
        return null;
    }
}

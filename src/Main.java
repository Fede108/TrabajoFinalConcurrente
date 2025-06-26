package src;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealVector;


public class Main {
     public static void main(String[] args) {

            // T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11
        double[][] matrizIncidencia = {
            {-1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1 },  // P0
            { 1, -1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0 },  // P1
            {-1,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0 },  // P2
            { 0,  1, -1,  0,  0, -1,  0, -1,  0,  0,  0,  0 },  // P3
            { 0,  0,  1, -1,  0,  0,  0,  0,  0,  0,  0,  0 },  // P4
            { 0,  0,  0,  1, -1,  0,  0,  0,  0,  0,  0,  0 },  // P5
            { 0,  0, -1,  0,  1, -1,  1, -1,  0,  0,  1,  0 },  // P6
            { 0,  0,  0,  0,  0,  1, -1,  0,  0,  0,  0,  0 },  // P7
            { 0,  0,  0,  0,  0,  0,  0,  1, -1,  0,  0,  0 },  // P8
            { 0,  0,  0,  0,  0,  0,  0,  0,  1, -1,  0,  0 },  // P9
            { 0,  0,  0,  0,  0,  0,  0,  0,  0,  1, -1,  0 },  // P10
            { 0,  0,  0,  0,  1,  0,  1,  0,  0,  0,  1, -1 },  // P11
        };
        // Marcado inicial: tokens en P0, P2, ..., P6.
        double[] marcadoInicial = new double[] {
            3,  // P0
            0,  // P1
            1,  // P2
            0,  // P3
            0,  // P4
            0,  // P5
            1,  // P6
            0,  // P7
            0,  // P8
            0,  // P9
            0,  // P10
            0   // P11
        };

        // Crear RedDePetri y monitor de concurrencia
        RedDePetri red  = new RedDePetri(matrizIncidencia, marcadoInicial);
        Monitor monitor = new Monitor(red);

        List<List<Integer>> transicionesPorSegmento = List.of(
            List.of(0),            // segmento 1
            List.of(1),            // segmento 2
            List.of(2, 3, 4),      // segmento 3
            List.of(5, 6),         // segmento 4
            List.of(7, 8, 9, 10),  // segmento 5
            List.of(11)            // segmento 6
        );

        int[] hilosPorSegmento = {
            1,  // 3 hilos para T0
            2,  // 1 hilo  para T1
            1,  // 1 hilos para T2,T3,T4
            1,  // 1 hilo  para T5,T6
            1,  // 1 hilos para T7,T8,T9,T10
            2   // 2 hilos para T11
        };

        // Crear y arrancar los hilos
        List<Thread> hilos = new ArrayList<>();
        
        for (int i = 0; i < transicionesPorSegmento.size(); i++) {
            List<Integer> lista = transicionesPorSegmento.get(i);

            for (int nroHilo = 0; nroHilo < hilosPorSegmento[i]; nroHilo++) {
                
                String nombre = String.format("Segmento %d-Hilo %d", i + 1, nroHilo);

                Thread hilo = new Thread( new Task(monitor, lista, i+1==transicionesPorSegmento.size()),nombre);
                hilos.add(hilo);
            }
        }

        hilos.forEach(Thread::start);
        
        for (Thread hilo : hilos) {
        try {
            hilo.join();
            } catch (InterruptedException e) {
                 e.printStackTrace();
            }
        }


    System.out.println("\tPRUEBA POLÍTICA");
    Politica politica = new Politica(Politica.TipoPolitica.ALEATORIA);
    RealVector sensibilizadas = red.getSensibilizadas();


// Seleccionar una clase de proceso habilitada
    Politica.TipoProceso modo = politica.seleccionarModo(sensibilizadas);

    if (modo != null) {
        int transicion = politica.getTransicion(modo);
        boolean ok = red.EcuacionDeEstado(transicion);
        System.out.println("T" + transicion + (ok ? " disparada." : " no disparada."));
        red.imprimirMarcado();
    }
    else {
        System.out.println("Ningún proceso seleccionable por la política.");
        System.out.print("Transiciones sensibilizadas: ");
        for (int i = 0; i < sensibilizadas.getDimension(); i++) {
            if (sensibilizadas.getEntry(i) == 1.0) {
                System.out.print("T" + i + " ");
            }
        }
        System.out.println(); // Salto de línea final
        }
    }

}


        

    


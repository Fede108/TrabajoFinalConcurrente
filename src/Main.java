package src;

import java.util.ArrayList;
import java.util.List;

public class Main {
     public static void main(String[] args) {
        // Matriz de incidencia (filas: lugares P0..P6, columnas: transiciones T0..T5)
        double[][] matrizIncidencia = {
            // T0, T1,   T2, T3,   T4, T5
            { -1,   0,   0,   0,   1,   0 },  // P0
            {  0,  -1,   0,   0,   0,   1 },  // P1
            {  1,   0,  -1,   0,   0,   0 },  // P2
            {  0,   1,   0,  -1,   0,   0 },  // P3
            {  0,   0,   0,   1,   0,  -1 },  // P4
            {  0,   0,   1,   0,  -1,   0 },  // P5
            {  0,   0,  -1,  -1,   1,   1 }   // P6
        };

        // Marcado inicial: tokens en P0, P1, ..., P6.
        double[] marcadoInicial = new double[] {
            1,  // P0
            1,  // P1
            0,  // P2
            0,  // P3
            0,  // P4
            0,  // P5
            1   // P6
        };


        // Crear RedDePetri y estructuras de concurrencia
        RedDePetri red = new RedDePetri(matrizIncidencia, marcadoInicial);
        int numTransiciones = matrizIncidencia[0].length;
        Queues queues = new Queues(numTransiciones);
        Monitor monitor = new Monitor(red, queues);

        // Definir listas de transiciones para cada hilo:
        // Hilo 1 intentará disparar T0, T2, T4 en ciclo
        List<Integer> listaHilo1 = new ArrayList<>();
        listaHilo1.add(0);
        listaHilo1.add(2);
        listaHilo1.add(4);
        // Hilo 2 intentará disparar T1, T3, T5 en ciclo
        List<Integer> listaHilo2 = new ArrayList<>();
        listaHilo2.add(1);
        listaHilo2.add(3);
        listaHilo2.add(5);

        // Crear y arrancar hilos
        Thread hilo1 = new Thread(new Task(monitor, listaHilo1), "Hilo-1");
        Thread hilo2 = new Thread(new Task(monitor, listaHilo2), "Hilo-2");
        Thread hilo3 = new Thread(new Task(monitor, listaHilo1), "Hilo-3");
        Thread hilo4 = new Thread(new Task(monitor, listaHilo2), "Hilo-4");

        hilo1.start();
        hilo2.start();
       // hilo4.start();
       // hilo3.start();

    }
}

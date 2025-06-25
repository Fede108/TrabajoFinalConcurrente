package src;

import java.util.ArrayList;
import java.util.List;

public class Main {
     public static void main(String[] args) {

//Orden de transiciones: T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11
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
        // Marcado inicial: tokens en P0, P1, ..., P6.
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

        // Crear RedDePetri y estructuras de concurrencia
        RedDePetri red = new RedDePetri(matrizIncidencia, marcadoInicial);
        int numTransiciones = matrizIncidencia[0].length;
        Queues queues = new Queues(numTransiciones);
        Monitor monitor = new Monitor(red, queues);

        // Definir listas de transiciones para cada hilo:
        // Hilo 1 intentará disparar T0 en ciclo
        List<Integer> listaHilo1 = new ArrayList<>();
        listaHilo1.add(0);
        
        // Hilo 2 intentará disparar T1 en ciclo
        List<Integer> listaHilo2 = new ArrayList<>();
        listaHilo2.add(1);
        
        // Hilo 3 intentará disparar T2,T3,T4 en ciclo
        List<Integer> listaHilo3 = new ArrayList<>();
        listaHilo3.add(2);
        listaHilo3.add(3);
        listaHilo3.add(4);

        // Hilo 4 intentará disparar T5,T6 en ciclo
        List<Integer> listaHilo4 = new ArrayList<>();
        listaHilo4.add(5);
        listaHilo4.add(6);

        // Hilo 5 intentará disparar T7,T8,T9,T10 en ciclo
        List<Integer> listaHilo5 = new ArrayList<>();
        listaHilo5.add(7);
        listaHilo5.add(8);
        listaHilo5.add(9);
        listaHilo5.add(10);

        // Hilo 6 intentará disparar T11 en ciclo
        List<Integer> listaHilo6 = new ArrayList<>();
        listaHilo6.add(11);

        // Crear y arrancar hilos
        Thread hilo1 = new Thread(new Task(monitor, listaHilo1, false), "Hilo-1");
        Thread hilo2 = new Thread(new Task(monitor, listaHilo2, false), "Hilo-2");
        Thread hilo3 = new Thread(new Task(monitor, listaHilo3, false), "Hilo-3");
        Thread hilo4 = new Thread(new Task(monitor, listaHilo4, false), "Hilo-4");
        Thread hilo5 = new Thread(new Task(monitor, listaHilo5, false), "Hilo-5");
        Thread hilo6 = new Thread(new Task(monitor, listaHilo6, true), "Hilo-6");

        hilo1.start();
        hilo2.start();
        hilo4.start();
        hilo3.start();
        hilo6.start();
        hilo5.start();

    }
}

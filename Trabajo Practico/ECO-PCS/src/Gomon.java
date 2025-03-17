import java.util.Random;

public class Gomon implements Runnable {
    private Carrera carrera;
    private int id;
    private boolean tipo;
    private int capacidad;
    public static final String ROJO = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    public Gomon(Carrera carrera, int id, boolean tipo) {
        this.carrera = carrera;
        this.id = id;
        this.tipo = tipo;
        capacidad = 1;
        if (tipo) {
            capacidad++;
        }
    }


    public void run() {
        Random generador = new Random();
        while (true) {
            try {
                carrera.gomonListo(tipo, id); //El gomon esta listo para que alguien lo tome
                if(tipo){
                    System.out.println("Gomon Doble " + id + " listo para correr");
                }else{
                    System.out.println("Gomon Individual " + id + " listo para correr");
                }


                carrera.gomonEsperaCarrera(tipo,  id); // El gomon se lleno

                int tiempoCarrera = generador.nextInt(1000) + 700;


                Thread.sleep(tiempoCarrera); //Corre la carrera

                System.out.println(ROJO + "Gomon "+ id +" corrio la carrera en: " + tiempoCarrera + "." + RESET);

                carrera.gomonFinalizado(tipo, id); //El gomon termino de correr, se reinicia

            } catch (InterruptedException e) {
            }

        }
    }


}

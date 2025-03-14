import java.util.Random;

public class Gomon implements Runnable {
    private Carrera carrera;
    private int id;
    private boolean tipo;
    private int capacidad;

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
                carrera.gomonLleno(tipo, id); // El gomon se lleno

                int tiempoCarrera = generador.nextInt(1000) + 700;


                Thread.sleep(tiempoCarrera); //Corre la carrera

                System.out.println("Gomon corrio la carrera en: " + tiempoCarrera + "");

                carrera.gomonFinalizado(tipo, id); //El gomon termino de correr, se reinicia

            } catch (InterruptedException e) {
            }

        }
    }


}

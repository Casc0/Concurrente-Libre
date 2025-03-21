import java.util.Random;

public class generadorPersonas implements Runnable {
    private String[] nombres = new String[200];
    private Colectivo[] colectivos;
    private Entrada entrada;
    private Shop tienda;
    private Reloj reloj;
    private FaroMirador faro;
    private lagunaSnorkel snorkel;
    private Restaurante[] restaurantes;
    private standPertenencias standDePertenencias;
    private TrenInterno trenInterno;
    private Carrera carreraGomones;
    private standBicis standDeBicis;
    private Piletas piletas;

    public generadorPersonas(String[] nombres, Colectivo[] colectivos, Entrada entrada, Shop tienda, Reloj reloj, FaroMirador faro, lagunaSnorkel snorkel, Restaurante[] restaurantes, standPertenencias standP, TrenInterno tren, Carrera carrera, standBicis standB, Piletas piletas) {
        this.nombres = nombres;
        this.entrada = entrada;
        this.reloj = reloj;
        this.colectivos = colectivos;
        this.tienda = tienda;
        this.restaurantes = restaurantes;
        this.faro = faro;
        this.snorkel = snorkel;
        this.standDePertenencias = standP;
        this.standDeBicis = standB;
        this.trenInterno = tren;
        this.carreraGomones = carrera;
        this.piletas = piletas;

    }

    @Override
    public void run() {
        Random rand = new Random();
        while (true) {
            reloj.esperarQuinceMinutos();
            Thread persona = new Thread(new Persona(nombres[rand.nextInt(200)], entrada, reloj, colectivos, tienda, restaurantes, faro, snorkel, standDePertenencias, trenInterno, carreraGomones, standDeBicis, piletas));
            persona.start();
            reloj.esperar45Minutos();

        }

    }

}




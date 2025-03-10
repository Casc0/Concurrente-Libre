import java.util.Random;

public class generadorPersonas implements Runnable{
    private String[] nombres = new String[200];
    private Colectivo[] colectivos;
    private Entrada entrada;
    private Shop tienda;
    private Reloj reloj;
    private FaroMirador faro;
    private lagunaSnorkel snorkel;
    private Restaurante[] restaurantes;

    public generadorPersonas(String[] nombres, Colectivo[] colectivos, Entrada entrada, Shop tienda, Reloj reloj, FaroMirador faro, lagunaSnorkel snorkel, Restaurante[] restaurantes) {
        this.nombres = nombres;
        this.entrada = entrada;
        this.reloj = reloj;
        this.colectivos = colectivos;
        this.tienda = tienda;
        this.restaurantes = restaurantes;
        this.faro = faro;
        this.snorkel = snorkel;
    }

    @Override
    public void run() {
        Random rand = new Random();
        while (true) {
            if(!reloj.dentroHorario()) {
                try {
                    Thread.sleep(rand.nextInt(10000, 20000));
                } catch (InterruptedException e) {
                    System.out.println("Error en generadorPersonas");
                }
            }else{
                try {

                    Thread persona = new Thread( new Persona(nombres[rand.nextInt(200)], entrada, reloj, colectivos, tienda, restaurantes, faro, snorkel));
                    persona.start();
                    Thread.sleep(rand.nextInt(2000, 4000));
                } catch (InterruptedException e) {
                    System.out.println("Error en generadorPersonas");
                }
            }

        }

    }


}

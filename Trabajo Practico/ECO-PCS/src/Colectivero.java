public class Colectivero implements Runnable {
    private Colectivo colectivo;
    private String nombre;
    private Reloj reloj;
    public static final String CIAN = "\u001B[36m";
    public static final String RESET = "\u001B[0m";


    public Colectivero(Colectivo cole, Reloj rel) {
        colectivo = cole;
        nombre = "Colectivero N°" + +colectivo.getID();
        reloj = rel;
    }

    @Override
    public void run() {
        //Sale cada 30 minutos y si no esta abierto el parque espera.
        while (true) {
            reloj.esperarQuinceMinutos(); //espera quince minutos si esta abierto el parque sino espera a que abra.

            colectivo.salir();
            System.out.println(CIAN + nombre + " salio" + RESET);
            reloj.viajarQuinceMinutos(); //espera 15 minutos para que llegue al parque
            System.out.println(CIAN + nombre + " llego al parque" + RESET);
            colectivo.llegar();
            System.out.println(CIAN + nombre + " esta volviendo" + RESET);


        }


    }
}

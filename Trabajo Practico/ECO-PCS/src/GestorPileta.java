public class GestorPileta implements Runnable {
    private Reloj reloj;
    private Piletas piletas;
    private static final String RESET = "\u001B[0m";
    private static final String AZUL = "\u001B[34m";

    public GestorPileta(Reloj reloj, Piletas piletas) {
        this.reloj = reloj;
        this.piletas = piletas;
    }

    public void run() {
        piletas.esperarMinimo();
        reloj.esperarQuinceMinutos();
        piletas.empezarActividad();
        System.out.println(AZUL +"--------------- Empieza la actividad de delfines ---------------" + RESET);
        reloj.esperar45Minutos();
        System.out.println(AZUL +"--------------- Termino la actividad de delfines ---------------"+ RESET);
        piletas.terminarActividad();

    }

}

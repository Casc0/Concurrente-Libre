public class Gestor implements Runnable {
    private Salon salon;
    private String YELLOW = "\u001B[33m";
    private String RESET = "\u001B[0m";
    private int cantTurnos;

    public Gestor(Salon salon, int unNum) {
        this.salon = salon;
        cantTurnos = unNum;
    }

    public void run() {
        int i = 0;
        while (i < cantTurnos) {
            try {
                System.out.println(YELLOW + "Empieza el Turno N°" + (i+1) + RESET);
                Thread.sleep(15000);//Tiempo para que lleguen las personas

                if (!salon.empezarTurno()) {

                    System.out.println("No hay suficientes personas para empezar la actividad, se cancela el turno N°" + (i + 1));
                    Thread.sleep(2000);
                    salon.reiniciarEstado();
                } else {

                    System.out.println(YELLOW + "Esperando la actividad N°1" + RESET);

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        System.out.println("Error de interrupcion");
                    }

                    salon.finalizarPrimeraActividad();

                    System.out.println(YELLOW + "Rotan de actividad" + RESET);

                    System.out.println(YELLOW + "Esperando la actividad N°2" + RESET);

                    salon.empezarSegundaActividad();

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        System.out.println("Error de interrupcion");
                    }

                    salon.finalizarSegundaActividad();
                    System.out.println(YELLOW + "Termino el Turno N°" + (i + 1) + RESET);
                }
                i++;
            } catch (InterruptedException e) {
                System.out.println("Error de interrupcion");
            }

        }

        salon.avisarFin();
        System.out.println(YELLOW + "Se terminaron los turnos" + RESET);
    }
}

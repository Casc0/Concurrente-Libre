public class Reloj {
    private int hora, minuto;
    private boolean parqueAbierto;
    public static final String AMARILLO = "\u001B[33m";
    public static final String RESET = "\u001B[0m";
    public int horaApertura = 9;
    public int horaCierre = 17;

    public Reloj(int horaInicio, int apertura, int cierre) {
        hora = horaInicio;
        minuto = 0;
        horaApertura = apertura;
        horaCierre = cierre;
        parqueAbierto = (hora >= horaApertura && hora < horaCierre);
    }

    public synchronized void incrementarHora() {
        // Metodo que incrementa en una hora el reloj
        hora = (hora + 1) % 24; // incrementa la hora
        System.out.println(AMARILLO + "Hora: " + hora + ":" + minuto + RESET);
        parqueAbierto = (hora >= horaApertura && hora < horaCierre); // verifica si el parque debe cerrar
        if (!parqueAbierto) {
            System.out.println(AMARILLO + "El parque esta cerrado" + RESET);
        }
        notifyAll(); // avisa a los hilos que esten esperando
    }

    public synchronized void incrementar15Minutos() {
        // Metodo que incrementa en 15 minutos el reloj
        minuto = (minuto + 15) % 60; // incrementa los minutos
        if (minuto == 0) { // si llega a 60 minutos incrementa la hora
            incrementarHora();
        } else{ //sino imprime la hora actual y avisa que pasaron quince minutos
            System.out.println(AMARILLO + "Hora: " + hora + ":" + minuto + RESET);
            notifyAll();
        }
    }

    public synchronized boolean dentroHorario() {
        return parqueAbierto;
    }

    public synchronized void esperarQuinceMinutos() {
        // espera a que pasen 15 minutos, si el parque esta cerrado espera a que abra

        while (!parqueAbierto) {
            try {
                wait();
            } catch (InterruptedException ie) {
                System.out.println("Problema en el Reloj");
            }
        }

        try {
            wait();
        } catch (InterruptedException ie) {
            System.out.println("Problema en el Reloj");
        }

    }

    public synchronized void viajarQuinceMinutos() {
        try {
            wait();
        } catch (InterruptedException ie) {
            System.out.println("Problema en el Reloj");
        }
    }
}
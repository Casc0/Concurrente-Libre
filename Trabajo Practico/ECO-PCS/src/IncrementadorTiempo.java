public class IncrementadorTiempo implements Runnable {
    private Reloj reloj;
    private int tiempo15minutos;

    public IncrementadorTiempo(Reloj reloj, int tiempo) {
        this.reloj = reloj;
        this.tiempo15minutos = tiempo;
    }

    public void run(){
        while (true) {
            try {
                Thread.sleep(tiempo15minutos);
            } catch (InterruptedException e) {
                System.out.println("Reloj Interrumpido");
            }
            reloj.incrementar15Minutos();
        }
    }
}

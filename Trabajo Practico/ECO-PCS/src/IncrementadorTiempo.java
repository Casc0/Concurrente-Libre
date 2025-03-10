public class IncrementadorTiempo implements Runnable {
    private Reloj reloj;

    public IncrementadorTiempo(Reloj reloj) {
        this.reloj = reloj;
    }

    public void run(){
        while (true) {
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                System.out.println("Reloj Interrumpido");
            }
            reloj.incrementar15Minutos();
        }
    }
}

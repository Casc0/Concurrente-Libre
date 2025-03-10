import java.util.concurrent.locks.ReentrantLock;

public class Pertenencias {
    int bolsosDisponibles, bolsosTotales;
    ReentrantLock lockBolsos = new ReentrantLock();

    public Pertenencias(int bolsosTotales){
        this.bolsosTotales = bolsosTotales;
        bolsosDisponibles = bolsosTotales;
    }

    public void dejarPertenencias(){
        lockBolsos.lock();
        while(bolsosDisponibles > 0){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Error al dejar pertenencias");
            }
        }
        bolsosDisponibles--;

    }

    public void recogerPertenencias(){

    }
}

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Carrera {
    private int cantGomones, gomonesIndividuales, gomonesDupla, gomonesEnCarrera, minimo;
    private Semaphore gomones;
    private CyclicBarrier arranque;
    private Semaphore mutex = new Semaphore(1);

    public Carrera(int cantGomDe1, int cantGomDe2, int min) {
        this.gomonesIndividuales = cantGomDe1;
        this.gomonesDupla = cantGomDe2;
        this.cantGomones = cantGomDe1 + cantGomDe2;
        this.minimo = min;
        this.gomones = new Semaphore(minimo);


        arranque = new CyclicBarrier(minimo, new Runnable() {
            @Override
            public void run() {
                System.out.println("Empieza la carrera");
            }
        });
    }

    public void esperarIndividual() {
            try {
                gomones.acquire();
                arranque.await();
                gomonesEnCarrera++;
            } catch (InterruptedException e) {
                System.out.println("Error en la espera de la carrera");
            } catch (BrokenBarrierException e) {
            }
    }

    public void esperarDupla() {
        try {
            gomones.acquire(2);
            arranque.await();
            gomonesEnCarrera++;
        } catch (InterruptedException e) {
            System.out.println("Error en la espera de la carrera");
        } catch (BrokenBarrierException e) {
        }
    }

    public void terminarCarrera() {
        try{
            mutex.acquire();

            gomonesEnCarrera--;
            if(gomonesEnCarrera == 0) {
                arranque.reset();
                gomones.release(cantGomones);
            }

        } catch (InterruptedException e) {
            System.out.println("Error en la salida de la carrera");
        }


    }

}

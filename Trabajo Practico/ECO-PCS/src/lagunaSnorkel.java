import java.util.concurrent.Semaphore;

public class lagunaSnorkel {
    int cantSnorkel, cantSalvavidas, cantPatasRana;
    Semaphore atencionAsistente, snorkels, salvavidas, patasRanas;


    public lagunaSnorkel(int snorkel, int salvavida, int patasRana, int asistentes) {
        this.cantSnorkel = snorkel;
        this.cantSalvavidas = salvavida;
        this.cantPatasRana = patasRana;
        atencionAsistente = new Semaphore(asistentes);
        snorkels = new Semaphore(cantSnorkel);
        salvavidas = new Semaphore(cantSalvavidas);
        patasRanas = new Semaphore(cantPatasRana);

    }

    public void conseguirEquipo() {
        try {
            atencionAsistente.acquire();
            // Intenta conseguir un snorkel
            snorkels.acquire();
            // Intenta conseguir un salvavidas
            salvavidas.acquire();
            // Intenta conseguir patas de rana
            patasRanas.acquire();
            atencionAsistente.release();

        } catch (InterruptedException e) {
            System.out.println("Error en la espera de conseguir equipo de snorkel");
        }
    }

    public void devolverEquipo() {
        snorkels.release();
        salvavidas.release();
        patasRanas.release();
    }


}

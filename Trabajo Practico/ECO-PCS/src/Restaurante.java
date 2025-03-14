import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurante {
    private Queue<Integer> colaEntrada;
    private ReentrantLock lockEntrada = new ReentrantLock(true);
    private Condition fila = lockEntrada.newCondition();
    private int turnoActual, mesasLibre;

    public Restaurante(int mesas) {
        turnoActual = 0;
        mesasLibre = mesas;
        colaEntrada = new LinkedList<Integer>();
    }

    public void entrar() {
        try {
            lockEntrada.lock();

            int turno = turnoActual;
            turnoActual++;
            colaEntrada.add(turno);

            while (mesasLibre <= 0 && colaEntrada.peek() != turno) { //Si no hay mesas libres y no es su turno espera
                fila.await();
            }

            colaEntrada.poll();

            mesasLibre--;

        } catch (InterruptedException e) {
            System.out.println("Error en la espera de la fila del restaurante");
        } finally {
            lockEntrada.unlock();
        }
    }


    public void irse() {
        lockEntrada.lock();
        mesasLibre++;
        fila.signalAll();
        lockEntrada.unlock();
    }

}

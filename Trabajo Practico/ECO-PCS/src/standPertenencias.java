import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class standPertenencias {
    //Estructuras para el manejo de los bolsos
    private BlockingQueue<Integer> colaBolsosDisponibles, colaBolsosParaCamioneta;
    private ConcurrentHashMap<Integer, Integer> pertenenciasParaRetirar;

    //Variables para la sincronizacion de la espera del primer bolso
    private ReentrantLock lock = new ReentrantLock(true);
    private Condition espera = lock.newCondition();

    public standPertenencias(int bolsos) {
        colaBolsosDisponibles = new LinkedBlockingQueue<>(bolsos);
        for (int i = 1; i < bolsos; i++) {
            colaBolsosDisponibles.add(i);
        }
        colaBolsosParaCamioneta = new LinkedBlockingQueue<>();
        pertenenciasParaRetirar = new ConcurrentHashMap<>();
    }

    public int dejarSusPertenencias() {
        int llave = 0;
        try {


            llave = colaBolsosDisponibles.take(); //Si hay un bolso disponible, toma la llave

            /*
            if(esperando) { //Si la camioneta estaba esperando, le avisa que llego un bolso
                lock.lock();
                espera.signal();
                esperando = false;
                lock.unlock();
            }

             */

            //mutex?
            colaBolsosParaCamioneta.add(llave); //deja el bolso para que lo lleve la camioneta

        } catch (InterruptedException e) {
            System.out.println("Error al tomar la llave del bolso");
        }

        return llave;
    }

    public void recogerSusPertenencias(int llave) {
        lock.lock();

        while(pertenenciasParaRetirar.get(llave) == null){ //mientras no este su bolso, espera
            try {
                espera.await();
            } catch (InterruptedException e) {
                System.out.println("Error al esperar la pertenencia");
            }
        }

        lock.unlock();
    }

    public void cargarCamioneta(int[] caja) {
        try { /*
            NECESARIO???

            lock.lock();
            if(colaBolsosParaCamioneta.isEmpty()) { //Si no hay bolsos para cargar, espera a que llegue alguien
                esperando = true;
                espera.await();
            }
            lock.unlock();
            */

            int i = 0;
            Integer bolsoActual = colaBolsosParaCamioneta.take(); //Espera a que haya un bolso para cargar

            while(bolsoActual != null && (i < caja.length)){ //Mientras haya un bolso para tomar y quede lugar en la caja
                caja[i] = bolsoActual; //carga el bolso
                i++;
                bolsoActual = colaBolsosParaCamioneta.poll(); //toma el siguiente bolso, si da null corta la repetitiva
            }

        } catch (InterruptedException e) {
            System.out.println("Error al cargar las pertenencias");
        }

    }

    public void vaciarCamioneta(int[] caja) {
        lock.lock();
        for (int i = 0; i < caja.length; i++) {
            pertenenciasParaRetirar.put(caja[i], caja[i]);
        }
        espera.signalAll();
        lock.unlock();
    }
}

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class standPertenencias {
    //Estructuras para el manejo de los bolsos
    private BlockingQueue<Object[]> colaBolsosDisponibles, colaBolsosParaCamioneta;
    private ConcurrentHashMap<Integer, String> bolsosParaRetirar;

    //Variables para la sincronizacion de la espera del primer bolso
    private ReentrantLock lock = new ReentrantLock(true);
    private Condition espera = lock.newCondition();

    public standPertenencias(int bolsos) {
        colaBolsosDisponibles = new LinkedBlockingQueue<>(bolsos);
        for (int i = 1; i < bolsos; i++) {
            Object[] bolso = {i, null};
            colaBolsosDisponibles.add(bolso);
        }
        colaBolsosParaCamioneta = new LinkedBlockingQueue<>();
        bolsosParaRetirar = new ConcurrentHashMap<>();
    }

    public int dejarSusPertenencias(String pertenencias) {
        int llave = 0;
        try {


            Object[] bolso = colaBolsosDisponibles.take(); //Si hay un bolso disponible, toma la llave
            llave = (int) bolso[0];
            bolso[1] = pertenencias; //Guarda las pertenencias en el bolso
            /*
            if(esperando) { //Si la camioneta estaba esperando, le avisa que llego un bolso
                lock.lock();
                espera.signal();
                esperando = false;
                lock.unlock();
            }

             */

            //mutex?
            colaBolsosParaCamioneta.add(bolso); //deja el bolso para que lo lleve la camioneta

        } catch (InterruptedException e) {
            System.out.println("Error al tomar la llave del bolso");
        }

        return llave;
    }

    public String recogerSusPertenencias(int llave) {
        lock.lock();

        String pertenencias = bolsosParaRetirar.remove(llave); //busca el bolso correspondiente a su llave
        while(pertenencias == null){ //mientras no este su bolso, espera
            try {
                espera.await();
            } catch (InterruptedException e) {
                System.out.println("Error al esperar la pertenencia");
            }
            pertenencias = bolsosParaRetirar.get(llave);
        }

        lock.unlock();

        Object[] bolso= {llave,  null};  //Devuelve el bolso con la llave que se libero
        colaBolsosDisponibles.add(bolso);

        return pertenencias;
    }

    public void cargarCamioneta(Object[] caja) {
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
            Object[] bolsoActual = colaBolsosParaCamioneta.take(); //Espera a que haya un bolso para cargar

            while(bolsoActual != null && (i < caja.length)){ //Mientras haya un bolso para tomar y quede lugar en la caja
                caja[i] = bolsoActual; //carga el bolso
                i++;
                bolsoActual = colaBolsosParaCamioneta.poll(500, TimeUnit.MILLISECONDS); //toma el siguiente bolso, si da null corta la repetitiva
            }

        } catch (InterruptedException e) {
            System.out.println("Error al cargar las pertenencias");
        }

    }

    public void vaciarCamioneta(Object[] caja) {
        lock.lock();
        int i = 0;
        while(i <= caja.length && caja[i] != null) { //Mientras haya un bolso en la caja y no se haya llegado al final
            Object[] bolsoActual = (Object[]) caja[i]; //Toma el bolso de la caja.
            bolsosParaRetirar.put((int) bolsoActual[0], (String) bolsoActual[1]); //Guarda el bolso en la lista de bolsos para retirar. En la posicion 0 la llave, posicion 1 el dueño de las pertenencias
            i++;
        }

        espera.signalAll();
        lock.unlock();
    }
}

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Piletas {

    private Reloj reloj;
    private CyclicBarrier barrera;
    private boolean abierto = false, inicio = false, termino = false;

    private ReentrantLock lock = new ReentrantLock();
    private Condition esperandoEntrar = lock.newCondition();
    private Condition esperandoInicio = lock.newCondition();
    private Condition esperandoFin = lock.newCondition();
    private Condition esperandoVaciarse = lock.newCondition();

    private static final String RESET = "\u001B[0m";
    private static final String AZUL = "\u001B[34m";

    private int cantidadTotal, cantidadActual = 0;

    public Piletas(int capacidadPileta) {
        this.cantidadTotal = capacidadPileta*4;
        this.barrera = new CyclicBarrier(capacidadPileta * 3, new Runnable() {
            @Override
            public void run() {
                System.out.println(AZUL + "--------------- Llegaron "+ capacidadPileta*3 +"  personas, se puede empezar la actividad de delfines ---------------" + RESET);
            }
        }); //minimo se llenan 3 piletas


    }

    public void esperarMinimo(){
        // Metodo de GESTOR PILETA - espera a que llegue el minimo de personas
        lock.lock();

        abierto = true;
        esperandoEntrar.signalAll(); //avisa que se abrio la pileta
        lock.unlock();

        try {
            barrera.await(); //espera a que se llegue el minimo a la pileta
        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("Error en el esperarMinimo de Piletas");
        }

    }

    public void empezarActividad(){
        // Metodo de GESTOR PILETA - avisa que se puede empezar la actividad
        lock.lock();
        abierto = false;

        inicio = true;
        esperandoInicio.signalAll(); //avisa que se ininio la actividad
        lock.unlock();
    }

    public void terminarActividad(){
        // Metodo de GESTOR PILETA - avisa que termino la actividad y espera que se vacie la pileta
        lock.lock();
        termino = true; //avisa que termino la actividad
        esperandoFin.signalAll();

        while(cantidadActual > 0){ //espera a que se vacie la pileta
            try {
                esperandoVaciarse.await();
            } catch (InterruptedException e) {
                System.out.println("Error en el terminarActividad de Piletas");
            }
        }

        //reiniciar variables
        inicio = false;
        termino = false;
        barrera.reset();

        lock.unlock();
    }

    public void entrarActividad(){
        // Metodo de PERSONA - entra a la actividad
        lock.lock();
        try {
            while (!abierto && cantidadActual >= cantidadTotal) { //espera a que se abra la pileta y haya lugar
                esperandoEntrar.await();
            }
            cantidadActual++;

            if (barrera.isBroken()) { //Si la barrera esta rota es porque ya llego el minimo, entonces pasa de largo

            } else {
                lock.unlock();
                barrera.await(); //espera a que llegue el minimo
                lock.lock();
            }

            while (!inicio) { //espera a que empiece la actividad
                esperandoInicio.await();
            }
        }catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("Error en el entrarActividad de Piletas");
        }

        lock.unlock();
    }

    public void salirActividad(){
        // Metodo de PERSONA - sale de la actividad
        lock.lock();
        while(!termino){
            try {
                esperandoFin.await();
            } catch (InterruptedException e) {
                System.out.println("Error en el salirActividad de Piletas");
            }
        }
        cantidadActual--;
        if(cantidadActual <= 0){
            esperandoVaciarse.signal();
        }
        lock.unlock();
    }



}

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TrenInterno {
    private ReentrantLock lockTren = new ReentrantLock();
    private Condition esperandoTren = lockTren.newCondition();
    private Condition esperandoGente = lockTren.newCondition();
    private Condition esperandoLlegada = lockTren.newCondition();

    private int capacidadActual, capacidadMaxima;
    private boolean llego = false, bajaronTodos = false, salio = false;

    private Reloj reloj;

    public TrenInterno(int capacidadMaxima, Reloj reloj){
        this.capacidadMaxima = capacidadMaxima;
        this.capacidadActual = 0;
        this.reloj = reloj;
    }

    public void salir(){
        // Metodo de TREN para salir
        lockTren.lock();
        salio = true; //Sale del origen
        lockTren.unlock();
    }

    public void llegar(){
        // Metodo de TREN para llegar a destino
        lockTren.lock();

        llego = true; //Llego a destino
        esperandoLlegada.signalAll(); //Avisa a todos que llego

        while(!bajaronTodos){ //Espera a que bajen todos
            try {
                esperandoGente.await();
            } catch (InterruptedException e) {
                System.out.println("Error en la espera de que bajen todos del tren");
            }
        }

        bajaronTodos = false; //Resetea la variable
        llego = false; //Resetea la variable
        salio = false;

        lockTren.unlock();
    }

    public boolean subirse(){
        // Metodo de PERSONA para subirse al tren
        boolean entro = false;
        lockTren.lock();
        while(reloj.dentroHorario() && !salio &&capacidadActual >= capacidadMaxima){ //Espera a que haya lugar
            try {
                esperandoTren.await();
            } catch (InterruptedException e) {
                System.out.println("Error en la espera de subirse al tren");
            }
        }


        if(reloj.dentroHorario()){
            entro = true;
            capacidadActual++; //Se sube al tren
            esperandoGente.signal(); //Avisa al tren

        }

        lockTren.unlock();
        return entro;
    }

    public void bajarse(){
        // Metodo de PERSONA para bajarse del tren
        lockTren.lock();

        while(!llego){ //Espera a que llegue a destino
            try {
                esperandoLlegada.await();
            } catch (InterruptedException e) {
                System.out.println("Error en la espera de bajarse del tren");
            }
        }


        capacidadActual--; //Se baja del tren

        if(capacidadActual == 0){
            bajaronTodos = true; //Avisa que bajaron todos
            esperandoGente.signalAll(); //Avisa al tren
        }
        lockTren.unlock();

    }
}

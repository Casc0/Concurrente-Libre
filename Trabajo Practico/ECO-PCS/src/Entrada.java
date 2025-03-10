import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Entrada {
    private int molinetesDisponibles;

    public Entrada(int molinetes){
        molinetesDisponibles = molinetes;
    }

    public synchronized void hacerFila(){
        // metodo para hacer fila en la entrada

        while(molinetesDisponibles <= 0){ // mientras no haya molinetes disponibles
            try{
                wait();
            }catch(InterruptedException ie){
                System.out.println("Problema en la Entrada");
            }
        }
        molinetesDisponibles--; // ocupa un molinete
    }

    public synchronized void recibirPulsera(){
        // cuando se pasa el molinete, se recibe la pulsera

        molinetesDisponibles++; // libera el molinete y avisa
        notify();

    }
}

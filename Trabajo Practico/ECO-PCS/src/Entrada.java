import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Entrada {
    private int molinetesDisponibles;
    private Reloj reloj;

    public Entrada(int molinetes, Reloj reloj){
        molinetesDisponibles = molinetes;
        this.reloj = reloj;
    }

    public synchronized boolean hacerFila(){
        // metodo para hacer fila en la entrada
        boolean entro = false;

        while(reloj.dentroHorario() && molinetesDisponibles <= 0 ){ // mientras no haya molinetes disponibles
            try{
                wait();
            }catch(InterruptedException ie){
                System.out.println("Problema en la Entrada");
            }
        }
        if(reloj.dentroHorario()){ // si no esta dentro del horario, no puede entrar
            entro = true;
            molinetesDisponibles--; // ocupa un molinete
        }

        return entro;
    }

    public synchronized void recibirPulsera(){
        // cuando se pasa el molinete, se recibe la pulsera

        molinetesDisponibles++; // libera el molinete y avisa
        notifyAll();

    }
}

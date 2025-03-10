import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Colectivo {
    private int capacidadDisponible;
    private int capacidadMaxima;
    private boolean seFue, llego, bajaronTodos, esperandoAlguien;
    private int id;
    private Lock lockColectivo = new ReentrantLock(true);
    private Condition esperando = lockColectivo.newCondition();
    private Condition viajando = lockColectivo.newCondition();
    private Condition condicionColectivero = lockColectivo.newCondition();


    //Los colectivos salen aproximadamente cada media hora, con la cantidad de gente que se haya llegado a subir, siempre menor a o igual a la capacidad.

    public Colectivo(int capacidad, int id) {
        this.capacidadMaxima = capacidad;
        this.capacidadDisponible = capacidadMaxima;
        this.id = id;
        seFue = false;
        llego = false;
        bajaronTodos = false;
        esperandoAlguien = false;
    }

    public int getID(){
        return id;
    }

    public void salir() {
        lockColectivo.lock();
        // Metodo del Colectivero - Cuando sale, avisa que no se pueden subir más personas y se va. Tiene que haber por lo menos una persona para que salga.
        while(capacidadDisponible >= capacidadMaxima){
            try{
                esperandoAlguien = true;
                condicionColectivero.await();
            }catch(InterruptedException ie){
                System.out.println("Problema en el Colectivo");
            }
        }
        esperandoAlguien = false;

        seFue = true; //Avisa que no puede entrar nadie más
        lockColectivo.unlock();

    }

    public void llegar() {
        // Metodo del Colectivero - Cuando llega al parque, avisa a las personas que se bajen. Luego espera a que se bajen todos y reinicia las variables.

        lockColectivo.lock();
        System.out.println("LLEGOOOOOOOOOOOO" + id);

        llego = true;   //Avisa a las personas que llego asi se empiezan a bajar
        viajando.signalAll();

        while(!bajaronTodos){ //Espera a que se bajen todos
            try{
                condicionColectivero.await();
            }catch(InterruptedException ie){
                System.out.println("Problema en el Colectivo");
            }
        }
        System.out.println("REINICIOOOOOOOOOOOOOOOOOOOO");
        //Reinicia las variables


        bajaronTodos = false;
        llego = false;
        seFue = false;
        esperando.signalAll(); //Avisa a las personas que se pueden subir
        lockColectivo.unlock();
    }


    public synchronized void subirse() {
        //Metodo de Persona - Mientras haya espacio en el colectivo, se sube
        lockColectivo.lock();

        while (capacidadDisponible <= 0 || seFue) { // espera mientras haya no espacio o haya salido
            try {
                esperando.await();
            } catch (InterruptedException ie) {
                System.out.println("Problema en el Colectivo");
            }
        }

        if(esperandoAlguien){
            capacidadDisponible--;
            condicionColectivero.signal();
        }else{
            capacidadDisponible--;
        }

        lockColectivo.unlock();
    }

    public synchronized void bajarse() {
        //Metodo de Persona - Mientras no haya llegado, espera. Cuando llega, se baja. Si es el ultimo avisa que se vacio.
        lockColectivo.lock();

        while(!llego){ //Mientras no haya llegado espera
            try{
                viajando.await();
            }catch(InterruptedException ie){
                System.out.println("Problema en el Colectivo");
            }
        }

        System.out.println("Se bajo una persona del colectivo " + id);
        capacidadDisponible++;

        if (capacidadDisponible >= capacidadMaxima) { //Si es el ultimo avisa que se vacio
            bajaronTodos = true;
            condicionColectivero.signal();
        }
        lockColectivo.unlock();


    }



}

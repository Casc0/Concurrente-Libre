import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class FaroMirador {
    private int capacidadEscalera, personasSubiendo;

    private ReentrantLock lockEscalera = new ReentrantLock(true);
    private Condition esperandoLaEscalera = lockEscalera.newCondition();

    private ReentrantLock lockTobogan = new ReentrantLock(true);
    private Condition esperandoSubir = lockTobogan.newCondition();
    private Condition administrador = lockTobogan.newCondition();

    private int turnoEscalera;
    private Queue<Integer> colaAfuera, colaEscalera;
    private boolean tobogan1Disponible, tobogan2Disponible, alguienEsperando;

    private Exchanger<Integer> eleccionTobogan;

    private static final String RESET = "\u001B[0m";
    public static final String ROJO = "\u001B[31m";

    private Reloj reloj;

    public FaroMirador(int cantToboganes, int capacidadEscalera, Reloj reloj) {
        this.capacidadEscalera = capacidadEscalera;
        this.personasSubiendo = 0;
        turnoEscalera = 0;
        colaAfuera = new LinkedList<>();
        colaEscalera = new LinkedList<>();
        tobogan1Disponible = true;
        tobogan2Disponible = true;
        alguienEsperando = false;
        eleccionTobogan = new Exchanger<Integer>();
        this.reloj = reloj;
    }

    public int subir() {
        //Metodo de Persona - Se encarga de subir a la escalera

        lockEscalera.lock();

        int turno = turnoEscalera; //se asigna el turno a la persona
        turnoEscalera++; //se actualiza el turno
        colaAfuera.add(turno); //se agrega a la cola

        while (reloj.dentroHorario() && personasSubiendo >= capacidadEscalera && colaAfuera.peek() != turno ) {
            //mientas la escalera este llena y no sea su turno, solo avanza cuando hay espacio y es el siguiente
            try {
                esperandoLaEscalera.await();
            } catch (InterruptedException e) {
                System.out.println("Error en subir la escalera");
            }
        }
        if(!reloj.dentroHorario()){ //si no esta dentro del horario, nadie más puede subir
            turno = -1;
            colaAfuera.clear(); //se vacia la cola para que nadie mas pueda subir
            esperandoLaEscalera.signal(); //se avisa a todos los que estan esperando que no pueden subir
        }else{
            colaAfuera.poll(); //se saca de la cola
            colaEscalera.add(turno); //Empieza a hacer fila en la escalera
            personasSubiendo++; //se suma una persona a las que estan subiendo

        }
        lockEscalera.unlock();

        return turno;
    }

    public void llegarCima() {
        //Metodo de Persona - Se encarga de liberar su lugar en la escalera

        //ACA WHILE

        lockEscalera.lock();
        personasSubiendo--;
        esperandoLaEscalera.signalAll();
        lockEscalera.unlock();
    }

    public int esperarTobogan(int suTurno) {
        //Metodo de Persona - Se encarga de hacer la cola para tirarse en el tobogan

        lockTobogan.lock();

        while (alguienEsperando &&!(tobogan1Disponible || tobogan2Disponible) && colaEscalera.peek() != suTurno) {
            // ESPERA mientras no haya nadie esperando asignacion del tobogan, no haya toboganes disponibles y no sea el siguiente en la escalera
            try {
                esperandoSubir.await();
            } catch (InterruptedException e) {
                System.out.println("Error en tirarse tobogan");
            }
        }

        lockEscalera.lock(); //se toma el lock de la escalera para sacar a la persona de la cola, proteger las variables y poder avisar al conjunto de espera

        colaEscalera.poll(); //saca de la cola de la escalera y se prepara para tirarse en tobogan
        personasSubiendo--;
        esperandoLaEscalera.signalAll();

        lockEscalera.unlock();

        //Separado para facilitar la lectura
        return conseguirTobogan(); //devuelve el tobogan asignado
    }

    private int conseguirTobogan(){
        //Continuacion de esperarTobogan - Separado para facilitar lectura
        int suTobogan = 0;

        alguienEsperando = true; //se marca que alguien esta esperando
        lockTobogan.unlock(); //se libera el lock ya que si entra en exchange no lo liberaria

        try {
            //sout.println(ROJO + Thread.currentThread().getName() +" Hace EXCHANGE" + RESET);

            suTobogan = eleccionTobogan.exchange(0); //el administrador le asigna un tobogan

            //System.out.println(ROJO + Thread.currentThread().getName() + "consiguio el tobogan nro " + suTobogan + RESET);

            lockTobogan.lock(); //se vuelve a tomar el lock para hacer signal
            alguienEsperando = false;
            esperandoSubir.signalAll();

        } catch (InterruptedException e) {
            System.out.println("Error en tirarse tobogan");
        }

        lockTobogan.unlock();

        return suTobogan; //devuelve el tobogan asignado
    }

    public void bajandoEnTobogan(int suTobogan) {
        //Metodo de Persona - Se encarga de liberar el tobogan

        lockTobogan.lock();

        if(suTobogan == 1){  //caso para ambos toboganes
            tobogan1Disponible = true; //se libera el tobogan

        }else{
            tobogan2Disponible = true; //se libera el tobogan
        }

        //System.out.println(ROJO + Thread.currentThread().getName() + " Libera tobogan " + suTobogan + RESET);

        administrador.signal(); //se avisa al administrador que hay un tobogan disponible
        esperandoSubir.signalAll(); //se avisa a las personas que estan esperando

        lockTobogan.unlock();
    }

    public void administrar() {
        //Metodo del Administrador - Se encarga de asignar los toboganes
        lockTobogan.lock();


        while(!tobogan1Disponible && !tobogan2Disponible){ //mientras no haya toboganes disponibles espera
            try {
                administrador.await();
            } catch (InterruptedException e) {
                System.out.println("Error en administrar tobogan");
            }
        }
        lockTobogan.unlock();
        if(tobogan1Disponible) { //si el tobogan 1 esta disponible, asigna ese

            //System.out.println(ROJO + "Administrador: Asigna tobogan 1" + RESET);

            try {
                eleccionTobogan.exchange(1);
            } catch (InterruptedException e) {
                System.out.println("Error en administrar tobogan" );
            }
            tobogan1Disponible = false;

        }else if(tobogan2Disponible){ //si no, asigna el tobogan 2

            //System.out.println(ROJO + "Administrador: Asigna tobogan 2" + RESET);


            try {
                eleccionTobogan.exchange(2);
            } catch (InterruptedException e) {
                System.out.println("Error en administrar tobogan");
            }
            tobogan2Disponible = false;
        }
    }
}

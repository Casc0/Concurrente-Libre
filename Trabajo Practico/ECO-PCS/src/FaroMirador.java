import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class FaroMirador {
    private int capacidadEscalera, personasSubiendo;

    private ReentrantLock lockEscalera = new ReentrantLock(true);
    private Condition escalera = lockEscalera.newCondition();

    private ReentrantLock lockTobogan = new ReentrantLock(true);
    private Condition tobogan = lockTobogan.newCondition();
    private Condition administrador = lockTobogan.newCondition();
    private Condition esperandoEleccion = lockTobogan.newCondition();

    private int turnoEscalera, turnoTobogan;
    private Queue<Integer> colaAfuera, colaEscalera, colaTobogan;
    private boolean tobogan1Disponible, tobogan2Disponible, alguienEsperando;

    private Exchanger<Integer> eleccionTobogan;

    private static final String RESET = "\u001B[0m";
    public static final String ROJO = "\u001B[31m";

    private Reloj reloj;

    public FaroMirador(int cantToboganes, int capacidadEscalera, Reloj reloj) {
        this.capacidadEscalera = capacidadEscalera;
        this.personasSubiendo = 0;
        turnoEscalera = 0;
        turnoTobogan = 0;
        colaAfuera = new LinkedList<>();
        colaTobogan = new LinkedList<>();
        tobogan1Disponible = true;
        tobogan2Disponible = true;
        alguienEsperando = false;
        eleccionTobogan = new Exchanger<Integer>();
        this.reloj = reloj;
    }

    public boolean subir() {
        //Metodo de Persona - Se encarga de subir a la escalera
        boolean subio;

        lockEscalera.lock();

        int turno = turnoEscalera; //se asigna el turno a la persona
        turnoEscalera++; //se actualiza el turno
        colaAfuera.add(turno); //se agrega a la cola

        while (reloj.dentroHorario() && personasSubiendo >= capacidadEscalera && colaAfuera.peek() != turno ) {
            //mientas la escalera este llena y no sea su turno, solo avanza cuando hay espacio y es el siguiente
            try {
                escalera.await();
            } catch (InterruptedException e) {
                System.out.println("Error en subir la escalera");
            }
        }
        if(!reloj.dentroHorario()){ //si no esta dentro del horario, nadie más puede subir
            subio = false;
            colaAfuera.clear(); //se vacia la cola para que nadie mas pueda subir
            escalera.signal(); //se avisa a todos los que estan esperando que no pueden subir
        }else{
            subio = true;
            colaAfuera.poll(); //se saca de la cola
            colaEscalera.add(turno);
            personasSubiendo++; //se suma una persona a las que estan subiendo

        }
        lockEscalera.unlock();

        return subio;
    }

    public void llegarCima() {
        //Metodo de Persona - Se encarga de liberar su lugar en la escalera

        //ACA WHILE

        lockEscalera.lock();
        personasSubiendo--;
        escalera.signalAll();
        lockEscalera.unlock();
    }

    public int esperarTobogan() {
        //Metodo de Persona - Se encarga de hacer la cola para tirarse en el tobogan

        lockTobogan.lock();

        while (!(tobogan1Disponible || tobogan2Disponible)) { //mientras no sea su turno y no haya toboganes disponibles
            try {
                tobogan.await();
            } catch (InterruptedException e) {
                System.out.println("Error en tirarse tobogan");
            }
        }

        colaEscalera.poll();

        //Separado para facilitar la lectura
        return conseguirTobogan(); //devuelve el tobogan asignado
    }

    private int conseguirTobogan(){
        //Continuacion de esperarTobogan - Separado para facilitar lectura
        int suTobogan = 0;



        while(alguienEsperando){ //Para evitar que intercambien entre si dos personas que estan esperando
            try {
                esperandoEleccion.await();
            } catch (InterruptedException e) {
                System.out.println("Error en tirarse tobogan");
            }
        }

        alguienEsperando = true; //se marca que alguien esta esperando
        lockTobogan.unlock(); //se libera el lock ya que si entra en exchange no lo liberaria

        try {
            //.out.println(ROJO + Thread.currentThread().getName() +" Hace EXCHANGE" + RESET);

            suTobogan = eleccionTobogan.exchange(0); //el administrador le asigna un tobogan

            //System.out.println(ROJO + Thread.currentThread().getName() + "consiguio el tobogan nro " + suTobogan + RESET);

            lockTobogan.lock(); //se vuelve a tomar el lock para hacer signal
            alguienEsperando = false;
            esperandoEleccion.signal();

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
        tobogan.signalAll(); //se avisa a las personas que estan esperando

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

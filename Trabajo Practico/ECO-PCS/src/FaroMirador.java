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
    private Queue<Integer> colaEscalera, colaTobogan;
    private boolean tobogan1Disponible, tobogan2Disponible, alguienEsperando;

    private Exchanger<Integer> eleccionTobogan;

    public FaroMirador(int cantToboganes, int capacidadEscalera) {
        this.capacidadEscalera = capacidadEscalera;
        this.personasSubiendo = 0;
        turnoEscalera = 0;
        turnoTobogan = 0;
        colaEscalera = new LinkedList<>();
        colaTobogan = new LinkedList<>();
        tobogan1Disponible = true;
        tobogan2Disponible = true;
        alguienEsperando = false;
    }

    public void subir() {
        //Metodo de Persona - Se encarga de subir a la escalera

        lockEscalera.lock();
        int turno = turnoEscalera; //se asigna el turno a la persona
        turnoEscalera++; //se actualiza el turno
        colaEscalera.add(turno); //se agrega a la cola

        while (personasSubiendo >= capacidadEscalera && colaEscalera.peek() != turno) {
            //mientas la escalera este llena y no sea su turno, solo avanza cuando hay espacio y es el siguiente
            try {
                escalera.await();
            } catch (InterruptedException e) {
                System.out.println("Error en subir la escalera");
            }
        }
        colaEscalera.poll(); //se saca de la cola
        personasSubiendo++; //se suma una persona a las que estan subiendo
        lockEscalera.unlock();
    }

    public void llegarCima() {
        //Metodo de Persona - Se encarga de liberar su lugar en la escalera

        lockEscalera.lock();
        personasSubiendo--;
        escalera.signalAll();
        lockEscalera.unlock();
    }

    public int esperarTobogan() {
        //Metodo de Persona - Se encarga de hacer la cola para tirarse en el tobogan

        lockTobogan.lock();

        int turno = turnoTobogan; //se asigna el turno a la persona
        turnoTobogan++; //se actualiza el turno
        colaTobogan.add(turno); //se agrega a la cola


        while (!(tobogan1Disponible || tobogan2Disponible) && (colaTobogan.peek() != turno)) { //mientras no sea su turno y no haya toboganes disponibles
            try {
                tobogan.await();
            } catch (InterruptedException e) {
                System.out.println("Error en tirarse tobogan");
            }
        }
        colaTobogan.poll(); //se saca de la cola

        //Separado para facilitar la lectura
        return esperarTobogan(); //devuelve el tobogan asignado
    }

    private int tirarseTobogan(){
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
            suTobogan = eleccionTobogan.exchange(0); //el administrador le asigna un tobogan

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

        if(tobogan1Disponible) { //si el tobogan 1 esta disponible, asigna ese
            try {
                eleccionTobogan.exchange(1);
                tobogan1Disponible = false;
            } catch (InterruptedException e) {
                System.out.println("Error en administrar tobogan");
            }
        }else { //si no, asigna el tobogan 2
            try {
                eleccionTobogan.exchange(2);
            } catch (InterruptedException e) {
                System.out.println("Error en administrar tobogan");
            }
        }






        lockTobogan.unlock();
    }
}

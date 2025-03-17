import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Carrera {

    private CyclicBarrier largada; //barrera ciclica que marca la largada

    //Semaforos:
    private Semaphore lugarEnDoble = new Semaphore(0); //Semaforo que indica si hay lugar en algun gomon doble
    private Semaphore lugarEnIndividual = new Semaphore(0);
    private Semaphore termino = new Semaphore(0);
    private Semaphore mutex = new Semaphore(1); //Semaforo para la seccion critica
    private Semaphore esperandoCarrera;

    //Arreglos de semaforos para que cada gomon libere y adquiera permisos de su posicion
    private Semaphore[] puedeSubir;
    private Semaphore[] esperandoPersona;
    private Semaphore[] esperandoBajarse;
    private int cantGomonesNecesarios, inicioDobles;

    //private boolean largo = false;

    public static final String ROJO = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    public Carrera(int min, int cantGomonesIndividuales, int cantGomonesDobles) {
        cantGomonesNecesarios = min;
        inicioDobles = cantGomonesIndividuales; //marca donde empiezan a ser gomones dobles en el arreglo
        int gomonesTotales = cantGomonesIndividuales + cantGomonesDobles;

        puedeSubir = new Semaphore[gomonesTotales];
        esperandoPersona = new Semaphore[gomonesTotales];
        esperandoBajarse = new Semaphore[gomonesTotales];


        for (int i = 0; i < gomonesTotales; i++) {
            puedeSubir[i] = new Semaphore(0);
            esperandoPersona[i] = new Semaphore(0);
            esperandoBajarse[i] = new Semaphore(0);
        }

        esperandoCarrera = new Semaphore(0);

        largada = new CyclicBarrier(cantGomonesNecesarios+1, new Runnable() {
            @Override
            public void run() {
                System.out.println(ROJO + "---------------------------------------------------------------------------------" + RESET);
                System.out.println(ROJO + "EMPIEZA LA CARRERA" + RESET);
                System.out.println(ROJO + "---------------------------------------------------------------------------------" + RESET);
                /*

                if(esperandoCarrera.availablePermits() > 0){
                    int i = esperandoCarrera.drainPermits();
                    termino.release(i);
                }

                largo = true;
                */





            }
        });
    }

    public int subirseGomon(boolean tipo) {
        //Metodo de PERSONA - se sube a un gomon disponible del tipo que quiera, sino espera a que se libere alguno
        int i = -1;
        int fin;
        try {

            if (tipo) { // Caso DOBLE
                lugarEnDoble.acquire(); //Espera a ver si se libera un gomon
                i = inicioDobles;
                fin = puedeSubir.length;

            } else { //Caso INDIVIDUAL
                lugarEnIndividual.acquire(); //Espera a ver si se libera un gomo
                i = 0;
                fin = inicioDobles;
            }
            mutex.acquire();
            boolean encontro = false;

            while (i < fin && !encontro) { //Recorre buscando el gomon con lugar
                if (puedeSubir[i].tryAcquire()) { //Pregunta si es el gomon con espacio. Si es, lo ocupa
                    encontro = true;
                } else {
                    i++;
                }
            }
            mutex.release();


        } catch (InterruptedException e) {
            System.out.println("Error al subirse a un Gomon");
        }
        return i; //retorna el gomon que uso;
    }

    public void correrCarrera(int idGomon) {
        //Metodo de PERSONA - se baja del gomon
        try {
            esperandoPersona[idGomon].release(); //Avisa que se subio una persona

            esperandoBajarse[idGomon].acquire(); //Se baja de su gomon
        } catch (InterruptedException e) {
            System.out.println("Error al correr la carrera");
        }
    }

    public void gomonListo(boolean tipoGomon, int idGomon) {
        //Metodo de GOMON - avisa que el gomon esta disponible y espera a que se suban n personas.
        try {

            int permisos = 1;
            if (tipoGomon) {
                permisos++;
            }

            puedeSubir[idGomon].release(permisos); //permite que se suban dos personas

            if (tipoGomon) { //Caso DOBLE
                lugarEnDoble.release(2); //avisa que hay lugar en un gomon doble
            }else{
                lugarEnIndividual.release(); //avisa que hay lugar en un gomon individual
            }
            esperandoPersona[idGomon].acquire(permisos); //espera a que se suban dos personas

        } catch (InterruptedException e) {
            System.out.println("Error en el esperandoPersona de Gomon");
        }
    }

    public void gomonEsperaCarrera(boolean tipoGomon, int idGomon) {
        //Metodo de GOMON - Espera a que espere la carrera


        try {
            esperandoCarrera.acquire(); //espera a que se llene la carrera o termine la anterior

            largada.await();

            /*
            if(!largo){ //Si no se largo la carrera, se baja del gomon
                cancelado(tipoGomon, idGomon);
            }
            */

        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("Error en el gomonEsperaCarrera de Gomon");
        }

        //return largo;
    }



    public void gomonFinalizado(boolean tipoGomon, int idGomon) {
        //Metodo de GOMON - Finalizo la carrera y debe reiniciarse


        if (tipoGomon) { //Caso DOBLE
            esperandoBajarse[idGomon].release(2); //Le avisa a la persona que se puede bajar
        } else { //Caso INDIVIDUAL
            esperandoBajarse[idGomon].release(); //Le avisa a la persona que se puede bajar
        }

        termino.release();


        mutex.release();

    }

    public void empezarCarrera(){
        //Metodo de GESTOR GOMON - avisa que se puede empezar la carrera y espera a los gomones

        try{
            esperandoCarrera.release(cantGomonesNecesarios); //avisa que se pueden preparar los gomones

            largada.await(); //espera a que se preparen todos los gomones


        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("Error en el empezarCarrera de GestorGomon");
        }
    }

    public void terminarCarrera(){
        //Metodo de GESTOR GOMON - espera que termine la carrera y reinicia la barrera
        try {
            termino.acquire(cantGomonesNecesarios);
        } catch (InterruptedException e) {
            System.out.println("Error en el terminarCarrera de GestorGomon");
        }

        //largo = false;

        largada.reset();
    }


    /*
    private void cancelado(boolean tipoGomon, int idGomon){
        //Paso un tiempo y no empezo la carrera, se bajan del gomon y se reinicia
        mutex.acquire();
        gomonesListos--;
        esperandoCarrera.release();
        if (tipoGomon) { //Caso DOBLE
            esperandoBajarse[idGomon].release(2); //Le avisa a la persona que se puede bajar
        } else { //Caso INDIVIDUAL
            esperandoBajarse[idGomon].release(); //Le avisa a la persona que se puede bajar
        }
        mutex.release();
    }
    */
}

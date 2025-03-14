import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Carrera {

    private CyclicBarrier largada; //barrera ciclica que marca la largada

    //Semaforos:
    private Semaphore lugarEnDoble = new Semaphore(0); //Semaforo que indica si hay lugar en algun gomon doble
    private Semaphore lugarEnIndividual = new Semaphore(0);

    //Arreglos de semaforos para que cada gomon libere y adquiera permisos de su posicion
    private Semaphore[] puedeSubir;
    private Semaphore[] esperandoPersona;
    private Semaphore[] esperandoCarrera;
    private int cantGomonesNecesarios, inicioDobles;

    public Carrera(int min, int cantGomonesIndividuales, int cantGomonesDobles) {
        cantGomonesNecesarios = min;
        inicioDobles = cantGomonesIndividuales + 1; //marca donde empiezan a ser gomones dobles en el arreglo
        puedeSubir = new Semaphore[cantGomonesIndividuales + cantGomonesDobles];
        esperandoPersona = new Semaphore[cantGomonesDobles + cantGomonesIndividuales];

        for (int i = 0; i < puedeSubir.length; i++) {
            puedeSubir[i] = new Semaphore(0);
            esperandoPersona[i] = new Semaphore(0);
        }


        largada = new CyclicBarrier(cantGomonesNecesarios, new Runnable() {


            @Override
            public void run() {
                System.out.println("Empieza la carrera");
            }
        });
    }

    public int subirseGomon(boolean tipo) {
        //Metodo de PERSONA - se sube a un gomon disponible del tipo que quiera, sino espera a que se libere alguno

        try {
            int i;
            if (tipo) { // Caso DOBLE
                lugarEnDoble.acquire(); //Espera a ver si se libera un gomon
                i = inicioDobles;
                while (i < puedeSubir.length) { //Recorre buscando el gomon con lugar
                    if (puedeSubir[i].tryAcquire()) { //Pregunta si es el gomon con espacio. Si es, ocupa uno
                        esperandoPersona[i].release(); //Avisa que se subio una persona
                    } else {
                        i++;
                    }
                }

            } else { //Caso INDIVIDUAL
                lugarEnIndividual.acquire(); //Espera a ver si se libera un gomo
                i = 0;
                while (i < inicioDobles) { //Recorre buscando el gomon con lugar
                    if (puedeSubir[i].tryAcquire()) { //Pregunta si es el gomon con espacio. Si es, lo ocupa
                        esperandoPersona[i].release(); //Avisa que se subio una persona
                    } else {
                        i++;
                    }
                }

                return i; //retorna el gomon que uso;
            }
        } catch (InterruptedException e) {
            System.out.println("Error al subirse a un Gomon");
        }

    }

    public void gomonListo(boolean tipoGomon, int idGomon) {
        //Metodo de GOMON - avisa que el gomon esta disponible y espera a que se suban n personas.
        try {

            int permisos = 1;

            if (tipoGomon) { //Caso DOBLE
                permisos++;
            }

            puedeSubir[idGomon].release(permisos); //permite que se suban dos personas
            lugarEnDoble.release(2); //avisa que hay lugar en un gomon doble

            esperandoPersona[idGomon].acquire(permisos); //espera a que se suban dos personas

        } catch (InterruptedException e) {
            System.out.println("Error en el esperandoPersona de Gomon");
        }
    }

    public void gomonLleno(boolean tipoGomon, int idGomon) {
        //Metodo de GOMON - Espera a que espere la carrera

    }

    public void gomonFinalizado(boolean tipoGomon, int idGomon) {
        //Metodo de GOMON - Finalizo la carrera y debe reiniciarse

    }


}

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Buffer {

    /* Pautas del buffer Oscilante
    1. Supongamos las dos colas inicialmente vacías, una de ellas etiquetada para insertar y
        otra para extraer.

    2. Si un proceso quiere extraer datos no puede hacerlo porque no hay ningún dato

    3. Si un proceso quiere insertar datos tendría que hacerlo en la cola etiquetada para ello.

    4. Supongamos la inserción, cuando ambas estan vacias, de un dato d1 en la cola etiquetada además el proceso debería “oscilar” las colas.

    5. Supongamos que dos procesos quieren insertar datos, cuando no estan vacias, tendrán que insertar en la cola etiquetada
        y lo tendrán que hacer en exclusión mutua

    6. Si un proceso quiere insertar y otro extraer podran realizar la operación de forma simultánea.

    7. Hay que asegurar la politica de First In First Out (FIFO) en la extracción de datos

    */
    private int esperando = 0;

    //Semaforos de exclusion mutua
    private Semaphore mutexInsercion = new Semaphore(1);
    private Semaphore mutexExtraccion = new Semaphore(1);

    //Semaforo para la espera de insercion
    private Semaphore esperandoInsercion = new Semaphore(0);

    //Colas de insercion y extraccion
    private Queue<Object> colaInsercion = new LinkedList<Object>();
    private Queue<Object> colaExtraccion = new LinkedList<Object>();

    //Variable de control
    private boolean vacio = true;

    //Colores para la salida
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    private static final String YELLOW = "\u001B[33m";
    public Buffer() {
    }

    public void insertar(Object elem) {
        //metodo del hilo insertor
        try {
            String nombre = Thread.currentThread().getName();


            mutexInsercion.acquire(); //Exlusión mutua para asegurar la pauta 5

            colaInsercion.add(elem);

            System.out.println(GREEN + nombre + " ha insertado el elemento " + elem + RESET);


            if (vacio) { //si ambos colas estan vacias entonces debe oscilar, por pauta 4

                vacio = false; // deja de estar vacio

                System.out.println("La inserción oscila");
                oscilar();
                if(esperando > 0){
                    esperandoInsercion.release(esperando);
                }

                //desbloquea a la extraccion

            }

            mutexInsercion.release();
            //System.out.println("Cola despues de insertar: "+ colaInsercion.toString());

        } catch (InterruptedException e) {
            System.out.println("Error de Interrupción");
        }
    }

    public void extraer() {
        //metodo del hilo extractor
        try {
            String nombre = Thread.currentThread().getName();
            Object elemExtraido;


            mutexExtraccion.acquire();

            //Se oscila cuando la cola de extraccion esta vacia
            if (colaExtraccion.isEmpty()) {

                mutexInsercion.acquire(); //evita que se inserte mientras oscila


                if (colaInsercion.isEmpty()) {  //verifica si la cola de insercion esta vacia, en cuyo caso espera a que se inserte un elemento
                    vacio = true;

                    System.out.println("Extraccion espera inserción");

                    esperando++;
                    mutexInsercion.release();
                    esperandoInsercion.acquire(); // espera a que se inserte un elemento
                    esperando--;

                } else {//oscila porque la cola de insercion tiene elementos, para asi asegurar la pauta 7

                    System.out.println("La extraccion oscila");
                    oscilar();
                    mutexInsercion.release();
                }
            }
            //extrae el elemento
            elemExtraido = colaExtraccion.remove();


            System.out.println(RED + nombre + " ha extraido el elemento: " + elemExtraido + RESET);
            //System.out.println("Cola despues de extraer: "+ colaExtraccion.toString());

            mutexExtraccion.release();
        } catch (InterruptedException e) {
            System.out.println("Error de Interrupción");
        }
    }


    private void oscilar() {
        estadoBuffer("antes de oscilar"); //Imprime el estado de las colas antes de oscilar

        //Intercambia las colas
        Queue auxiliar = colaInsercion;
        colaInsercion = colaExtraccion;
        colaExtraccion = auxiliar;

        estadoBuffer("despues de oscilar"); //Imprime el estado de las colas despues de oscilar
    }

    //Imprime el estado de las colas
    private void estadoBuffer(String mensaje) {
        System.out.println(YELLOW + "Colas " + mensaje + ": ");
        System.out.println("\t Cola de insercion: " + colaInsercion.toString());
        System.out.println("\t Cola de extraccion: " + colaExtraccion.toString() + RESET);
    }
}

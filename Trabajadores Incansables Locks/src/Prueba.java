import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class Prueba {
    private ReentrantLock lock = new ReentrantLock();

    private Semaphore napolitanasPorHacer = new Semaphore(0);
    private Semaphore veganasPorHacer = new Semaphore(0);

    private Semaphore mostradorVeganas = new Semaphore(0);
    private Semaphore mostradorNapolitanas = new Semaphore(0);

    private Queue<Pedido> colaPedidos = new LinkedList<Pedido>();

    private Semaphore semPedidos = new Semaphore(0);

    private Semaphore mutexCola = new Semaphore(1);

    private int MAX_PEDIDOS;

    public Prueba(int max) {
        MAX_PEDIDOS = max;
    }

    public void iniciarPedido(Pedido pedido){
        try {
            mutexCola.acquire(); //para evitar modificacion de la cola mientras se introduce un elemento

            colaPedidos.add(pedido);
            semPedidos.release(); //avisa que hay un pedido


            mutexCola.release();

            if(pedido.getTipoPizza()){ //true es vegana, false es napolitana
                veganasPorHacer.release(2);
            }else{
                napolitanasPorHacer.release(1);
            }

        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");
        }


    }

    public void hacerPizzaNapolitana() {
        try {
            napolitanasPorHacer.acquire();
        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");
        }


    }
    public void terminarPizzaNapolitana() {
        mostradorNapolitanas.release();

    }

    public void hacerPizzaVegana() {
        try {
            veganasPorHacer.acquire();
        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");
        }

    }
    public void terminarPizzaVegana()  {
        mostradorVeganas.release();
    }

    public Pedido recogerPedido() throws InterruptedException {
        semPedidos.acquire(); //espera a que haya pedidos

        mutexCola.acquire(); //para evitar modificacion de la cola mientras se saca un elemento

        Pedido pedido = colaPedidos.remove();
        mutexCola.release();

        return pedido;
    }

    public void entregarNapolitana() {
        try {
            mostradorNapolitanas.acquire();
        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");
        }
    }

    public void entregarVegana() {
        try {
            mostradorVeganas.acquire(2);
        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");
        }

    }



    public void descansar(boolean pizza)  {
        try {
            //agarra una pizza y avisa que hay que hacer otra para el pedido
            if (pizza){
                napolitanasPorHacer.release();
                mostradorNapolitanas.acquire();
            }else{
                veganasPorHacer.release();
                mostradorVeganas.acquire();
            }
        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");
        }

    }
}

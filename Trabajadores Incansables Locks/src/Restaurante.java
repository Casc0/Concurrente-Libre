import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurante {

    private Semaphore mutexVeganas = new Semaphore(1);
    private Semaphore mutexNapolitanas = new Semaphore(1);
    private Semaphore mutexMostradorNapo = new Semaphore(1);
    private Semaphore mutexMostradorVeg = new Semaphore(1);

    private int MAX_PEDIDOS;

    private ReentrantLock lockPedidos = new ReentrantLock();
    private Condition pedidosEnEspera = lockPedidos.newCondition();
    private Condition repartidoresPendientes = lockPedidos.newCondition();
    private Queue<Pedido> colaPedidos = new LinkedList<>();

    private ReentrantLock lockNapolitanas = new ReentrantLock();
    private Condition pizzerosNapos = lockNapolitanas.newCondition();
    private Condition deliveryNapos = lockNapolitanas.newCondition();


    private ReentrantLock lockVeganas = new ReentrantLock();
    private Condition pizzerosVeg = lockVeganas.newCondition();
    private Condition deliveryVeg = lockVeganas.newCondition();


    private int naposPorHacer, vegPorHacer, mostradorNapos, mostradorVeg, cantPedidos;


    public Restaurante(int max) {
        MAX_PEDIDOS = max;
        naposPorHacer = 0;
        vegPorHacer = 0;
        mostradorNapos = 0;
        mostradorVeg = 0;
        cantPedidos = 0;
    }

    public void hacerPizzaNapolitana() {
        try {
            lockNapolitanas.lock();
            while (naposPorHacer <= 0) { //espera a que haya pedidos
                pizzerosNapos.await();
            }

            mutexNapolitanas.acquire(); //mutex para proteger la variable
            naposPorHacer--;
            mutexNapolitanas.release();

        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");
        } finally {
            lockNapolitanas.unlock();
        }


    }

    public void terminarPizzaNapolitana() {
        try {
            lockNapolitanas.lock();

            mutexMostradorNapo.acquire(); //mutex para proteger la variable
            mostradorNapos++;
            deliveryNapos.signal(); //avisa que hay una pizza lista
            mutexMostradorNapo.release();

        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");
        }finally{
            lockNapolitanas.unlock();
        }

    }

    public void entregarNapolitana() {
        try {
            lockNapolitanas.lock();
            while (mostradorNapos < 1) { //Espera a que haya una pizza napolitana
                deliveryNapos.await();
            }

            mutexMostradorNapo.acquire(); //mutex para proteger la variable
            mostradorNapos--;
            mutexMostradorNapo.release();

        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");
        } finally {
            lockNapolitanas.unlock();
        }
        lockPedidos.lock(); //lock para proteger la variable y poder avisar al conjunto de pedidos

        cantPedidos--; //considero que cuando se va a entregar el pedido ya se libera un lugar para otro
        pedidosEnEspera.signal(); //Avisa que hay lugar para un nuevo pedido

        lockPedidos.unlock();
    }

    public void hacerPizzaVegana() {
        try {
            lockVeganas.lock();
            while (vegPorHacer <= 0) { //espera a que haya pedidos
                pizzerosVeg.await();
            }

            mutexVeganas.acquire(); //mutex para proteger la variable
            vegPorHacer--;
            mutexVeganas.release();

        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");
        } finally {
            lockVeganas.unlock();
        }

    }

    public void terminarPizzaVegana() {
        try {
            lockVeganas.lock();

            mutexMostradorVeg.acquire(); //mutex para proteger la variable
            mostradorVeg++;
            deliveryVeg.signal(); //avisa que hay una pizza lista

            mutexMostradorVeg.release();

        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");
        }finally{
            lockVeganas.unlock();
        }
    }

    public void entregarVegana() {
        try {
            lockVeganas.lock();
            while (mostradorVeg < 2) { //Espera a que haya dos pizzas veganas
                deliveryVeg.await();
            }

            mutexMostradorVeg.acquire(); //mutex para proteger la variable
            mostradorVeg -= 2;
            mutexMostradorVeg.release();

        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");
        } finally {
            lockVeganas.unlock();
        }

        lockPedidos.lock(); //lock para proteger la variable y poder avisar

        cantPedidos--;  //considero que cuando se va a entregar el pedido ya se libera un lugar para otro
        pedidosEnEspera.signal(); //Avisa que hay lugar para un nuevo pedido

        lockPedidos.unlock();

    }

    //Metodo del pedido de un cliente
    public void iniciarPedido(Pedido pedido) {
        try {
            lockPedidos.lock();

            while (cantPedidos >= MAX_PEDIDOS) { //Espera a que haya lugar para el pedido
                System.out.println("El pedido de " + pedido.getNombreCliente() + " tiene que esperar");
                pedidosEnEspera.await();
            }

            cantPedidos++;
            colaPedidos.add(pedido); //Se añade a la cola de pedidos pendientes
            repartidoresPendientes.signal(); //avisa que hay un nuevo pedido


            if (pedido.getTipoPizza()) { //true es vegana, false es napo

                mutexVeganas.acquire(); //para proteger la variable
                vegPorHacer = vegPorHacer + 2;
                mutexVeganas.release();

                lockVeganas.lock(); //Para poder avisar a los pizzeros
                pizzerosVeg.signalAll();
                lockVeganas.unlock();



            } else {

                mutexNapolitanas.acquire(); //para proteger la variable
                naposPorHacer++;
                mutexNapolitanas.release();

                lockNapolitanas.lock(); //Para poder avisar a los pizzeros
                pizzerosNapos.signal();
                lockNapolitanas.unlock();


            }

        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");
        } finally {
            lockPedidos.unlock();
        }


    }

    //Metodo de seleccion de un pedido de un repartidor
    public Pedido recogerPedido() {
        try {
            lockPedidos.lock();
            while (colaPedidos.isEmpty()) { //Espera a que haya un pedido para agarrar
                repartidoresPendientes.await();
            }
        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");
        }finally{

            Pedido pedido = colaPedidos.remove(); //Lo agarra
            lockPedidos.unlock();

            return pedido;
        }
    }



    //metodo del descanso de un repartidor
    public void descansar(boolean pizza) {
        try {
            //agarra una pizza y avisa que hay que hacer otra para el pedido
            if (!pizza) {
                lockNapolitanas.lock();

                mutexNapolitanas.acquire();
                naposPorHacer++;
                pizzerosNapos.signal(); //avisa que tienen que hacer una nueva
                mutexNapolitanas.release();

                while (mostradorNapos <= 0) { //espera a que haya alguna hecha
                    deliveryNapos.await();
                }

                mutexMostradorNapo.acquire(); //se come una pizza
                mostradorNapos--;
                mutexMostradorNapo.release();

                lockNapolitanas.unlock();
            } else {
                lockVeganas.lock();

                mutexVeganas.acquire();
                vegPorHacer++;
                pizzerosVeg.signal(); //avisa que tienen que hacer una nueva
                mutexVeganas.release();

                while (mostradorVeg <= 0) { //espera a que haya alguna hecha
                    deliveryVeg.await();
                }

                mutexMostradorVeg.acquire(); //se come una pizza
                mostradorVeg--;
                mutexMostradorVeg.release();

                lockVeganas.unlock();
            }
        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");
        }

    }
}

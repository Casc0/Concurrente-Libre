import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurante {

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

            naposPorHacer--;

        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");
        } finally {
            lockNapolitanas.unlock();
        }


    }

    public void terminarPizzaNapolitana() {

            lockNapolitanas.lock();

            mostradorNapos++;
            deliveryNapos.signal(); //avisa que hay una pizza lista


            lockNapolitanas.unlock();

    }

    public void entregarNapolitana() {
        try {
            lockNapolitanas.lock();
            while (mostradorNapos < 1) { //Espera a que haya una pizza napolitana
                deliveryNapos.await();
            }

            mostradorNapos--;

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

            vegPorHacer--;

        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");
        } finally {
            lockVeganas.unlock();
        }

    }

    public void terminarPizzaVegana() {

            lockVeganas.lock();

            mostradorVeg++;
            deliveryVeg.signal(); //avisa que hay una pizza lista



            lockVeganas.unlock();

    }

    public void entregarVegana() {
        try {
            lockVeganas.lock();
            while (mostradorVeg < 2) { //Espera a que haya dos pizzas veganas
                deliveryVeg.await();
            }

            mostradorVeg -= 2;

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


                lockVeganas.lock(); //Para poder avisar a los pizzeros
                vegPorHacer = vegPorHacer + 2;
                pizzerosVeg.signalAll();
                lockVeganas.unlock();



            } else {

                lockNapolitanas.lock(); //Para poder avisar a los pizzeros
                naposPorHacer++;
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


                naposPorHacer++;
                pizzerosNapos.signal(); //avisa que tienen que hacer una nueva


                mostradorNapos--;
                while (mostradorNapos <= 0) { //espera a que haya alguna hecha
                    deliveryNapos.await();
                }



                lockNapolitanas.unlock();
            } else {
                lockVeganas.lock();


                vegPorHacer++;
                pizzerosVeg.signal(); //avisa que tienen que hacer una nueva

                while (mostradorVeg <= 0) { //espera a que haya alguna hecha
                    deliveryVeg.await();
                }


                mostradorVeg--;
                lockVeganas.unlock();
            }
        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");
        }

    }
}

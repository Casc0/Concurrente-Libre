import java.util.Random;

public class Repartidor implements Runnable {
    private String nombre;
    private Restaurante restaurante;
    private int viajes, cicloDescanso;

    private static final String BLUE = "\u001B[34m";
    private static final String RESET = "\u001B[0m";
    private static final String YELLOW = "\u001B[33m";

    public Repartidor(String nombre, Restaurante restaurante, int ciclo) {
        this.nombre = nombre;
        this.restaurante = restaurante;
        viajes = 0;
        cicloDescanso = ciclo;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Pedido pedido = restaurante.recogerPedido(); //Elije el primer pedido de la cola y espera a que este listo

                if (pedido.getTipoPizza()) { // espera las pizzas
                    System.out.println(BLUE + nombre + " está esperando las pizzas veganas de " + pedido.getNombreCliente() + RESET);
                    restaurante.entregarVegana();
                } else {
                    System.out.println(BLUE + nombre + " está esperando la pizza napolitana de " + pedido.getNombreCliente() + RESET);
                    restaurante.entregarNapolitana();
                }

                System.out.println(BLUE + nombre + " está entregando el pedido de " + pedido.getNombreCliente() + RESET);
                Thread.sleep(6000); // Simula el tiempo de entrega

                System.out.println(BLUE + nombre + " ha entregado el pedido de " + pedido.getNombreCliente() + RESET);
                viajes++;

                if (viajes >= cicloDescanso) { //cuando descansa come una pizza del mismo tipo de las que entrego
                    restaurante.descansar(pedido.getTipoPizza());
                    System.out.println(YELLOW + nombre + " está descansando y comiendo una pizza" + RESET);
                    Thread.sleep(6000); // Simula el tiempo de descanso
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

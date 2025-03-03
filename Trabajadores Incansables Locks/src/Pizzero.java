public class Pizzero implements Runnable {
    private String nombre;
    private Restaurante restaurante;
    private int tipoPizza;

    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    public Pizzero(String nombre, Restaurante restaurante, int tipo) {
        this.nombre = nombre;
        this.restaurante = restaurante;
        tipoPizza = tipo;
    }

    @Override
    public void run() {

            //Pizzero de veganas
            if ((tipoPizza % 2) == 0) {
                while(true){
                    restaurante.hacerPizzaVegana();
                    System.out.println(GREEN + nombre + " esta haciendo una pizza Vegana" + RESET);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        System.out.println("Error de interrupcion");
                    }
                    restaurante.terminarPizzaVegana();
                    System.out.println(GREEN + nombre + " ha terminado la pizza Vegana" + RESET);
                }

            //Pizzero de napolitanas
            } else {
                while(true){
                    restaurante.hacerPizzaNapolitana();
                    System.out.println(RED + nombre + " esta haciendo una pizza Napolitana" + RESET);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        System.out.println("Error de interrupcion");
                    }
                    restaurante.terminarPizzaNapolitana();
                    System.out.println(RED + nombre + " ha terminado la pizza Napolitana" + RESET);
                }
            }
        }
    }


public class Main {
    public static void main(String[] args) {
        int MAX_PEDIDOS = 20, cicloDescansoRepartidor = 10;
        int cantPizzeros = 12, cantRepartidores = 8, cantGeneradores = 3;
        Restaurante restaurante = new Restaurante(MAX_PEDIDOS);

        Thread[] pizzeros = new Thread[cantPizzeros];
        Thread[] repartidores = new Thread[cantRepartidores];
        Thread[] generadores = new Thread[cantGeneradores];

        crearHilos(restaurante, pizzeros, repartidores, generadores, cicloDescansoRepartidor);
        iniciarHilos(pizzeros, repartidores, generadores);
    }

    public static void crearHilos(Restaurante restaurante, Thread[] pizzeros, Thread[] repartidores, Thread[] generadores, int ciclo) {
        for(int i = 0; i < pizzeros.length; i++) {
            pizzeros[i] = new Thread(new Pizzero("Pizzero " + i, restaurante, i));
        }

        for(int j = 0; j < repartidores.length; j++) {
            repartidores[j] = new Thread(new Repartidor("Repartidor " + j, restaurante, ciclo));
        }

        for(int k = 0; k < generadores.length; k++) {
            generadores[k] = new Thread(new GeneradorPedidos(restaurante));
        }
    }

    public static void iniciarHilos(Thread[] pizzeros, Thread[] repartidores, Thread[] generadores) {
        for(int i = 0; i < pizzeros.length; i++) {
            pizzeros[i].start();
        }

        for(int j = 0; j < repartidores.length; j++) {
            repartidores[j].start();
        }
        for(int k = 0; k < generadores.length; k++) {
            generadores[k].start();
        }
    }
}
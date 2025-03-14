public class Camioneta implements Runnable {
    private Object[] caja;
    private standPertenencias stand;


    public Camioneta(int cap, standPertenencias stand) {
        caja = new Object[cap];
        this.stand = stand;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Error al dormir la camioneta");
            }

            stand.cargarCamioneta(caja);
            System.out.println("Camioneta cargada. Yendo hasta el final del recorrido");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Error al dormir la camioneta");
            }

            stand.vaciarCamioneta(caja);
            System.out.println("Camioneta vaciada. Volviendo al stand");


        }

    }
}

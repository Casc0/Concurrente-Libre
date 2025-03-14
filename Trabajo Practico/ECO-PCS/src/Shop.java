public class Shop {
    private int cantCajas, cantCajasOcupadas;

    public Shop(int cajas){
        cantCajas = cajas;
        cantCajasOcupadas = 0;
    }

    public synchronized void empezarCompra(){
        // La persona llega, elige los souvenires y se pone en la fila

        while(cantCajasOcupadas >= cantCajas){ // Si no hay cajas disponibles, la persona espera
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("La persona se fue de la tienda porque fue interrumpida.");
            }
        }
        cantCajasOcupadas++;

    }

    public synchronized void terminarCompra(){
        // La persona termina de comprar y se va, liberando la caja

        cantCajasOcupadas--;
        notifyAll();
    }
}

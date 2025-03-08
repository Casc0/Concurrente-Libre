import java.util.Random;

public class Insertor implements Runnable {
    private Buffer buffer;

    public Insertor(Buffer oscilante) {
        buffer = oscilante;
    }

    @Override
    public void run() {
        Random rand = new Random();
        while(true){
            int elem = rand.nextInt(100);
            buffer.insertar(elem);
            try{
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Error de Interrupción");
            }
        }
    }
}

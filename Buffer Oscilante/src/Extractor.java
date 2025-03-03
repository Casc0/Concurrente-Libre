public class Extractor implements Runnable{
    Buffer buffer;

    public Extractor(Buffer oscilante) {
        buffer = oscilante;
    }

    @Override
    public void run() {
        while(true){
            buffer.extraer();
            try{
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Error de Interrupción");
            }
        }
    }
}

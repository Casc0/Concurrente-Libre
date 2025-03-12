public class Main {
    public static void main(String[] args) {
    Buffer b = new Buffer();
    int cantInsertores = 5, cantExtractores = 16;
    Thread[] insertores = new Thread[cantInsertores];
    Thread[] extractores = new Thread[cantExtractores];
    crearHilos(b, insertores, extractores);
    iniciarHilos(insertores, extractores);
    }

    public static void crearHilos(Buffer b, Thread[] insertores, Thread[] extractores){
        for (int i = 0; i < insertores.length; i++) {
            insertores[i] = new Thread(new Insertor(b));
        }
        for (int i = 0; i < extractores.length; i++) {
            extractores[i] = new Thread(new Extractor(b));
        }
    }

    public static void iniciarHilos(Thread[] insertores, Thread[] extractores){
        for (int i = 0; i < insertores.length; i++) {
            insertores[i].start();
        }
        for (int i = 0; i < extractores.length; i++) {
            extractores[i].start();
        }
    }


}

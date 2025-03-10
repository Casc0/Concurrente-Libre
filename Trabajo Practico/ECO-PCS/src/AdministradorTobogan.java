public class AdministradorTobogan implements Runnable {
    FaroMirador faro;

    public AdministradorTobogan(FaroMirador faro) {
        this.faro = faro;
    }

    @Override
    public void run() {
        while (true) {
            try {
                faro.administrar();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Error en el administrador del tobogan");
            }
        }

    }
}

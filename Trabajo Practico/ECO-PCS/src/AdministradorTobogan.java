public class AdministradorTobogan implements Runnable {
    private FaroMirador faro;

    public AdministradorTobogan(FaroMirador faro) {
        this.faro = faro;
    }

    @Override
    public void run() {
        while (true) {
            faro.administrar();
        }

    }
}

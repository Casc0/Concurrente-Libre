public class GestorGomon implements Runnable {
    private Carrera carrera;

    public GestorGomon(Carrera carrera) {
        this.carrera = carrera;
    }

    @Override
    public void run() {
        while (true) {
            carrera.empezarCarrera();
            carrera.terminarCarrera();
            System.out.println("---------------------------------------------------------------------------------");
            System.out.println("TERMINO LA CARRERA");
            System.out.println("---------------------------------------------------------------------------------");
        }
    }
}

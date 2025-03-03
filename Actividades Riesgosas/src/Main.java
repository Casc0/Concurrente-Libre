public class Main{

    public static void main(String[] args) {
        int cantPersonas = 142, turnos = 10;
        Salon salon = new Salon(4);
        Thread[] personas = new Thread[cantPersonas];
        crearHilos(personas, salon, turnos);

        Thread gestor = new Thread(new Gestor(salon, turnos));
        iniciarHilos(personas, gestor);
    }

    public static void crearHilos(Thread[] personas, Salon salon, int turnos){
        for (int i = 0; i < personas.length; i++) {
            personas[i] = new Thread(new Persona(salon, "Persona " + i, turnos));
        }
    }



    public static void iniciarHilos(Thread[] personas, Thread gestor){
        for (int i = 0; i < personas.length; i++) {
            personas[i].start();
        }
        gestor.start();
    }

}

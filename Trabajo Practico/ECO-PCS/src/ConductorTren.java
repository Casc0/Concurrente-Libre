public class ConductorTren implements Runnable {
    private TrenInterno suTren;
    private Reloj reloj;


    public ConductorTren(TrenInterno tren, Reloj rel) {
        suTren = tren;
        reloj = rel;
    }

    @Override
    public void run() {
        //Sale cada media hora, y si no esta abierto el parque espera.
        while (true) {
            reloj.esperarQuinceMinutos(); //espera quince minutos para que entre gente, si no esta abierto el parque espera a que abra.

            suTren.salir();
            System.out.println("El tren salio");
            reloj.viajarQuinceMinutos(); //espera 15 minutos para que llegue a destino
            System.out.println("El tren llego");
            suTren.llegar();
            System.out.println("El tren esta volviendo");
            reloj.viajarQuinceMinutos(); //espera 15 minutos para que vuevla al origen
            System.out.println("El tren volvio");




        }


    }
}

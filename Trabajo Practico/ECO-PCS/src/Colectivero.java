public class Colectivero implements Runnable {
    private Colectivo colectivo;
    private String nombre;
    private Reloj reloj;
    public static final String RED = "\u001B[31m";
    public static final String RESET = "\u001B[0m";


    public Colectivero(Colectivo cole, Reloj rel) {
        colectivo = cole;
        nombre = "Colectivero N°" + + colectivo.getID();
        reloj = rel;
    }

    @Override
    public void run() {
        //Sale cada media hora, y si no esta abierto el parque espera.
        while (true) {
            if(!reloj.esperarMediaHora()){ //espera media hora si esta abierto el parque sino espera a que abra.
                try{
                    Thread.sleep(5000);
                }catch(InterruptedException e){
                    System.out.println(nombre + " interrumpido");
                }
            }else{ //si esta abierto el parque, sale
                colectivo.salir();
                System.out.println(RED + nombre + " salio" + RESET);
                try{
                    Thread.sleep(500); //viajando
                }catch(InterruptedException e){
                    System.out.println(nombre + " interrumpido");
                }
                System.out.println(RED + nombre + " llego al parque" + RESET);
                colectivo.llegar();
            }

        }


    }
}

public class Reloj {
    private int hora, minuto;
    private boolean parqueAbierto;
    public static final String AMARILLO = "\u001B[33m";
    public static final String RESET = "\u001B[0m";

    public Reloj(int horaInicio) {
        hora = horaInicio;
        minuto = 0;
        parqueAbierto = (hora >= 9 && hora < 17);
    }

    public synchronized void incrementarHora(){
        hora = (hora+1)%24;
        System.out.println(AMARILLO + "Hora: " + hora + ":" + minuto + RESET);
        parqueAbierto = (hora >= 9 && hora < 17);
        if(!parqueAbierto){
            System.out.println(AMARILLO + "El parque ha cerrado" + RESET);
            notifyAll();
        }
    }

    public synchronized void incrementar15Minutos(){
        minuto = (minuto+15)%60;
        if(minuto == 0){
            incrementarHora();
            notifyAll();
        }else if(minuto == 30){
            System.out.println(AMARILLO + "Hora: " + hora + ":" + minuto + RESET);
            notifyAll();
        }
    }

    public synchronized boolean dentroHorario(){
        return parqueAbierto;
    }

    public synchronized boolean esperarMediaHora(){
        // espera a que pasen 30 minutos
        if(parqueAbierto){
            try{
                wait();
            }catch(InterruptedException ie){
                System.out.println("Problema en el Reloj");
            }
        }
        return parqueAbierto;
    }
}
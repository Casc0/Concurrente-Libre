public class standBicis {
    int cantBicis;
    Reloj reloj;

    public standBicis(int cantBicis, Reloj reloj){
        this.cantBicis = cantBicis;
        this.reloj = reloj;
    }

    public synchronized boolean tomarBici(){
        boolean subio = false;
        while(reloj.dentroHorario() && cantBicis == 0){
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Error en la espera de bicis");
            }
        }
        if(reloj.dentroHorario()){
            subio = true;
            cantBicis--;
        }
        return subio;
    }

    public synchronized void devolverBici(){
        cantBicis++;
        notify();
    }
}

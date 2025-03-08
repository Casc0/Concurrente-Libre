public class Salon {

    //control de cantidad de personas en cada actividad
    private boolean telasLleno = false;
    private boolean lyraLleno = false;
    private boolean yogaLleno = false;

    //evita que se salga de la actividad antes de tiempo
    private boolean terminoActividad = false;

    private boolean terminaronTurnos;

    //marca si se puede entrar a las actividades, pero cuando ya estan dentro del salon
    private boolean puedenEntrar;

    //marca si pueden volver a entrar a las actividades
    private boolean salieronTodos = false;
    private boolean cancelado = false;
    private boolean habilitado = true;


    private int capActualTelas, capActualLyra, capActualYoga, cupoMaxActividad, cupoMaxSalon, cupoActualSalon;


    public Salon(int cupoAct) {
        cupoMaxActividad = cupoAct;
        capActualTelas = cupoMaxActividad;
        capActualLyra = cupoMaxActividad;
        capActualYoga = cupoMaxActividad;

        cupoMaxSalon = cupoMaxActividad *3;
        puedenEntrar = false;
        cupoActualSalon = 0;
        terminaronTurnos = false;
    }

    //Metodo para ver si la persona llega a entrar en el cupo, si no llega, o si no hay mas turnos.
    public synchronized int llegarASalon() {
        int entroEnTurno = 0;

        if(terminaronTurnos){
            entroEnTurno = -1;
        }else if(cupoActualSalon < cupoMaxSalon && habilitado){ //mientras haya lugar y este habilitado para entrar
            cupoActualSalon++;
            entroEnTurno = 1;
        }

        return entroEnTurno;
    }

    //Metodo para esperar a que se llene el cupo del salon, caso contrario devuelve que se cancelo el turno.
    public synchronized boolean esperarTurno(){
        boolean retorna = true;
        while(cupoActualSalon != cupoMaxSalon && !cancelado){ //mientras no este lleno el cupo y no se haya cancelado el turno
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Error de interrupcion");
            }
        }
        if (cancelado){
            retorna = false;
        }
        return retorna;
    }

    //Metodo para que la persona pueda entrar a la actividad de telas
    public synchronized boolean entrarTelas() {
        while(!puedenEntrar){ //Espera a que se de inicio al turno
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Error de interrupcion");
            }
        }
        boolean entro = false;
        if (!telasLleno) { //Puede entrar mientras haya lugar en la actividad
            entro = true;
            capActualTelas--;
            if (capActualTelas == 0) { //si es el ultimo en entrar, se llena la actividad
                telasLleno = true;
                notifyAll();
            }
        }
        return entro;
    }

    //Metodo para que la persona pueda entrar a la actividad de lyra
    public synchronized boolean entrarLyra() {
        while(!puedenEntrar){ //Espera a que se de inicio al turno
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Error de interrupcion");
            }
        }
        boolean entro = false;
        if (!lyraLleno) { //Puede entrar mientras haya lugar en la actividad
            entro = true;
            capActualLyra--;
            if (capActualLyra == 0) { //si es el ultimo en entrar, se llena la actividad
                lyraLleno = true;
                notifyAll();
            }
        }
        return entro;
    }

    //Metodo para que la persona pueda entrar a la actividad de yoga
    public synchronized boolean entrarYoga() {
        while(!puedenEntrar){ //Espera a que se de inicio al turno
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Error de interrupcion");
            }
        }
        boolean entro = false;
        if (!yogaLleno) { //Puede entrar mientras haya lugar en la actividad
            entro = true;
            capActualYoga--;
            if (capActualYoga == 0) { //si es el ultimo en entrar, se llena la actividad
                yogaLleno = true;
                notifyAll();
            }
        }
        return entro;
    }

    //Metodo para salir de la actividad de telas
    public synchronized void salirTelas() {
        while(!terminoActividad){ //Espera a que termine la actividad
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Error de interrupcion");
            }
        }

        capActualTelas++; //Sale de la actividad, liberando un espacio
        if (capActualTelas == cupoMaxActividad) { //Si se vacia la actividad, se marca como no llena
            telasLleno = false;
            if(!lyraLleno && !yogaLleno){ //Si no hay nadie en las otras actividades, se marca que salieron todos, para asi avanzar
                salieronTodos = true;
                notifyAll();
            }
        }
    }

    //Metodo para salir de la actividad de lyra
    public synchronized void salirLyra() {
        while(!terminoActividad){ //Espera a que termine la actividad
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Error de interrupcion");
            }
        }

        capActualLyra++; //Sale de la actividad, liberando un espacio
        if (capActualLyra == cupoMaxActividad) { //Si se vacia la actividad, se marca como no llena
            lyraLleno = false;
            if(!telasLleno && !yogaLleno){ //Si no hay nadie en las otras actividades, se marca que salieron todos, para asi avanzar
                salieronTodos = true;
                notifyAll();
            }
        }
    }

    //Metodo para salir de la actividad de yoga
    public synchronized void salirYoga() {
        while(!terminoActividad){ //Espera a que termine la actividad
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Error de interrupcion");
            }
        }

        capActualYoga++; //Sale de la actividad, liberando un espacio
        if (capActualYoga == cupoMaxActividad) { //Si se vacia la actividad, se marca como no llena
            yogaLleno = false;
            if(!lyraLleno && !telasLleno){ //Si no hay nadie en las otras actividades, se marca que salieron todos, para asi avanzar
                salieronTodos = true;
                notifyAll();
            }
        }
    }

    //Metodo para salir del salon, cuando terminan ambas actividades, en un solo metodo para que la variable quede protegida
    public synchronized void salir() {
        cupoActualSalon--;
        if(cupoActualSalon <= 0 && salieronTodos) { //si se vacio el salon y salieron todos de las actividades, se reinician el estado para el proximo turno
            notifyAll();
        }
    }

    public synchronized boolean empezarTurno() {
        habilitado = false; //No deja que entren mas personas
        boolean empezo = false; //Variable de retorno, marca si se llevo a cabo la actividad o si se cancelo

        if(cupoActualSalon >= cupoMaxSalon){ //Si cuando empieza el turno no hay suficientes personas, se cancela el turno

            terminoActividad = false; //variable para que las personas esperen a que termine la actividad
            puedenEntrar = true; //variable para permitir que las personas elijan sus actividades
            notifyAll();
            empezo = true;

            //Hasta que no se llene cada actividad, no se puede empezar.
            //Como ya se aseguro que hay cupo cupoMaxSalon de personas, si o si se van a llegar a la condición.
            while (!telasLleno || !lyraLleno || !yogaLleno) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.out.println("Error de interrupcion");
                }
            }
            puedenEntrar = false; //Para evitar que cuando terminen su actividad, entren a otra antes de tiempo
        }else{ //avisa que se cancelo el turno
            cancelado = true;
            notifyAll();
        }
        return empezo;
    }

    public synchronized void finalizarPrimeraActividad() {
        terminoActividad = true;
        notifyAll();

        while(!salieronTodos){
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Error de interrupcion");
            }
        }
        salieronTodos = false;
    }

    //Metodo para que las personas puedan entrar a la segunda actividad
    public synchronized void empezarSegundaActividad() {
        terminoActividad = false; //variable para que las personas esperen a que termine la actividad
        puedenEntrar = true; //variable para permitir que las personas elijan sus actividades
        notifyAll();
        while (!telasLleno || !lyraLleno || !yogaLleno) { //Hasta que no se llene cada actividad, no se puede empezar.
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Error de interrupcion");
            }
        }
        puedenEntrar = false; //Para evitar que cuando terminen su actividad, entren a otra antes de tiempo
    }

    //Metodo para que las personas salgan de la segunda actividad y se termine el turno
    public synchronized void finalizarSegundaActividad() {
        terminoActividad = true; //variable para que las personas salgan de su actividad
        notifyAll();

        while(!salieronTodos && cupoActualSalon!=0){ //Si no salieron todos y no se vacio el salon, se espera
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Error de interrupcion");
            }
        }
        //Se reinician la variable para el proximo turno:
        salieronTodos = false;
        cancelado = false;
        habilitado = true;
    }

    //Si se cancela el turno se debe reiniciar las variables
    public synchronized void reiniciarEstado(){
        cancelado = false;
        cupoActualSalon = 0;
        habilitado = true;
    }

    //Metodo para sacar a cualquier persona que haya ingresado al salon despues de que no hayan mas turnos
    public synchronized void avisarFin(){
        terminaronTurnos = true;
        cancelado = true;
        habilitado = false;
        notifyAll();
    }

}

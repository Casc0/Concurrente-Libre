import java.util.Random;

public class Persona implements Runnable {
    private String nombre;
    private Reloj reloj;
    private Colectivo[] colectivos;
    private Entrada entrada;
    private Shop tienda;
    private Restaurante[] restaurantes;
    private FaroMirador faro;
    private lagunaSnorkel snorkel;
    private static final String RESET = "\u001B[0m";
    private boolean yaCompro, yaMerendo, yaAlmorzo, yaSubioFaro;

    public Persona(String nombre, Entrada entr, Reloj rel, Colectivo[] coles, Shop shop, Restaurante[] restos, FaroMirador faro, lagunaSnorkel snorkel) {
        this.nombre = nombre;
        this.reloj = rel;
        this.colectivos = coles;
        this.entrada = entr;
        this.tienda = shop;
        this.restaurantes = restos;
        this.faro = faro;
        this.snorkel = snorkel;
        //variable de control de que actividad ya hizo, para evitar repetir
        yaCompro = false;
        yaMerendo = false;
        yaAlmorzo = false;
        yaSubioFaro = false;
    }

    @Override
    public void run() {
        Random rand = new Random();
        if (!reloj.dentroHorario()) { //Revisa que se este entre las 9 hs y las 17 hs
            System.out.println(nombre + " no puede entrar al Parque porque está cerrado.");
        } else {
            /*
            if (rand.nextInt() % 3 == 0) { // 1/3 de probabilidad de que la persona llegue en colectivo
                colectivo(colectivos[rand.nextInt(colectivos.length)]); // elige un colectivo al azar

            }
            */
            colectivo(colectivos[rand.nextInt(colectivos.length)]);


            entrar();
            boolean seguir = true;
            while(reloj.dentroHorario() && seguir) {
                boolean valido = true;
                while(valido){
                    switch (rand.nextInt(3)) {
                        case 0:

                            break;
                        case 1:
                            if(!yaAlmorzo && !yaMerendo) { //Si no almorzó ni merendó elije uno
                                if(rand.nextBoolean()){
                                    almorzar();
                                }else{
                                    merendar();
                                }
                            }else if(yaAlmorzo && !yaMerendo) { //Si ya almorzó merienda
                                merendar();
                            }else if(!yaAlmorzo && yaMerendo){ //Si ya merendó almuerza
                                almorzar();
                            }
                            break;
                        case 2:
                            if(!yaCompro){
                                shop();
                            }
                            break;
                        default:
                            valido = false;
                            break;
                    }


                }

                shop();

                seguir = !(rand.nextInt(4) == 0); // 1/4 de probabilidad de que la persona se vaya
            }
        }

    }

    public void colectivo(Colectivo colectivo) {
        String COLOR = "\u001B[" + (34+ colectivo.getID()) + "m";


        colectivo.subirse();
        System.out.println(COLOR + nombre + " se subió al colectivo " + colectivo.getID() + RESET);
        colectivo.bajarse();
        System.out.println(COLOR + nombre + " llegó en colectivo " + colectivo.getID() + RESET);


    }

    public void entrar() {
        try {
            entrada.hacerFila();
            Thread.sleep(2000);
            entrada.recibirPulsera();
            System.out.println(nombre + " entró al Parque y recibio su pulsera.");
        } catch (InterruptedException e) {
            System.out.println(nombre + " no pudo entrar al parque porque fue interrumpido.");
        }
    }

    public void shop() {
        try {
            tienda.empezarCompra();
            Thread.sleep(2000);
            tienda.terminarCompra();
            System.out.println(nombre + " compró en la tienda.");
            yaCompro = true;
        } catch (InterruptedException ie) {
            System.out.println(nombre + " no pudo comprar en la tienda porque fue interrumpido.");
        }
    }

    public void almorzar(){
        Random rand = new Random();
        Restaurante restaurante = restaurantes[rand.nextInt(restaurantes.length)];
        restaurante.entrar();
        System.out.println(nombre + " esta almorzando en el restaurante.");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println("Error en la espera del almuerzo");
        }
        restaurante.irse();
        System.out.println(nombre + " se fue del restaurante.");
        yaAlmorzo = true;
    }

    public void merendar(){
        Random rand = new Random();
        Restaurante restaurante = restaurantes[rand.nextInt(restaurantes.length)];
        restaurante.entrar();
        System.out.println(nombre + " esta merendando en el restaurante.");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println("Error en la espera de la merienda");
        }
        restaurante.irse();
        System.out.println(nombre + " se fue del restaurante.");
        yaMerendo = true;
    }

    public void irAlFaro(){
        faro.subir();
        System.out.println(nombre + " esta subiendo al faro.");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println("Error en la espera de la subida al faro");
        }
        faro.llegarCima();
        System.out.println(nombre + " llego al mirador.");
        System.out.println(nombre + " hace fila para los toboganes.");
        int tobogan = faro.esperarTobogan();
        System.out.println(nombre + " esta bajando en el tobogan n°" + tobogan + ".");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Error en la espera de la bajada del tobogan");
        }
        faro.bajandoEnTobogan(tobogan);
        yaSubioFaro = true;
    }
}

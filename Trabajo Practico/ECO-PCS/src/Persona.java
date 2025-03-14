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
    private standPertenencias standDePertenencias;
    private TrenInterno trenInterno;
    private Carrera carreraGomones;
    private standBicis standDeBicis;
    private boolean yaCompro, yaMerendo, yaAlmorzo, yaSubioFaro, yaHizoCarrera, yaHizoSnorkel, yaNadoDelfines;

    Random rand = new Random();
    private static final String RESET = "\u001B[0m";
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m";
    public static final String MORADO = "\u001B[35m";
    public static final String CIAN = "\u001B[36m";
    public static final String BLANCO = "\u001B[37m";


    public Persona(String nombre, Entrada entr, Reloj rel, Colectivo[] coles, Shop shop, Restaurante[] restos, FaroMirador faro, lagunaSnorkel snorkel, standPertenencias standP, TrenInterno tren, Carrera carrera, standBicis standB) {
        this.nombre = nombre;
        this.reloj = rel;
        this.colectivos = coles;
        this.entrada = entr;
        this.tienda = shop;
        this.restaurantes = restos;
        this.faro = faro;
        this.snorkel = snorkel;
        this.standDePertenencias = standP;
        this.standDeBicis = standB;
        this.trenInterno = tren;
        this.carreraGomones = carrera;

        //variable de control de que actividad ya hizo, para evitar repetir
        yaCompro = false;
        yaMerendo = false;
        yaAlmorzo = false;
        yaSubioFaro = false;
        yaHizoCarrera = false;
        yaHizoSnorkel = false;
        yaNadoDelfines = false;
    }

    @Override
    public void run() {


        if (rand.nextInt() % 3 == 0) { // 1/3 de probabilidad de que la persona llegue en colectivo
            colectivo(colectivos[rand.nextInt(colectivos.length)]); // elige un colectivo al azar

        }


        if (!reloj.dentroHorario()) { //Revisa que se este entre las 9 hs y las 17 hs
            System.out.println(nombre + " no puede entrar al Parque porque está cerrado.");

        } else {

            boolean seguir = entrar(); //La persona entra al parque pasando por los molinetes y recibiendo su pulsera





            while (reloj.dentroHorario() && seguir) { //Mientras este dentro del horario y quiera seguir en el parque


                elegirActividad(); //Elige una actividad al azar

                if(yaCompro && yaAlmorzo && yaMerendo && yaSubioFaro && yaHizoSnorkel && yaNadoDelfines && yaHizoCarrera){ //Si ya hizo todas las actividades se va
                    seguir = false;
                }else{
                    seguir = !(rand.nextInt(4) == 0); // 1/4 de probabilidad de que la persona se vaya
                }

            }

        }

    }

    private void elegirActividad(){
        switch (rand.nextInt(6)) { //Elige una actividad al azar, nunca repite la misma actividad excepto restaurante

            //CARRERA

                //CARRERA
            case 0:
                if (!yaHizoCarrera) {
                    irCarrera();
                }
                break;

            //RESTAURANTE
            case 1:
                if (!yaAlmorzo && !yaMerendo) { //Si no almorzó ni merendó elije uno
                    if (rand.nextBoolean()) {
                        almorzar();
                    } else {
                        merendar();
                    }
                } else if (yaAlmorzo && !yaMerendo) { //Si ya almorzó merienda
                    merendar();
                } else if (!yaAlmorzo && yaMerendo) { //Si ya merendó almuerza
                    almorzar();
                }
                break;

            //SHOP
            case 2:
                if (!yaCompro) {
                    shop();
                }
                break;

            //FARO
            case 3:
                if (!yaSubioFaro) {
                    irAlFaro();
                }
                break;

            //DELFINES
            case 4:
                if (!yaNadoDelfines) {
                    nadarDelfines();
                }
                break;

            //SNORKEL
            case 5:
                if(!yaHizoSnorkel){
                    hacerSnorkel();
                }
                break;
            default:
                System.out.println("Error en la elección de actividad");
                break;


        }
    }

    private void colectivo(Colectivo colectivo) {
        //La persona llega en colectivo
        //String COLOR = "\u001B[" + (34 + colectivo.getID()) + "m";


        colectivo.subirse();
        //System.out.println(COLOR + nombre + " se subió al colectivo " + colectivo.getID() + RESET);
        colectivo.bajarse();
        //System.out.println(COLOR + nombre + " llegó en colectivo " + colectivo.getID() + RESET);
        System.out.println(CIAN + nombre + " llegó en colectivo " + colectivo.getID() + RESET);


    }

    private boolean entrar() {
        //La persona entra al parque pasando por los molinetes y recibiendo su pulsera
        boolean entro = false;
        try {
            entro = entrada.hacerFila();
            if (entro) {
                Thread.sleep(500);
                entrada.recibirPulsera();
                System.out.println(VERDE + nombre + " entró al Parque y recibio su pulsera." + RESET);
            }
        } catch (InterruptedException e) {
            System.out.println(nombre + " no pudo entrar al parque porque fue interrumpido.");
        }
        return entro;


    }

    private void shop() {
        //La persona compra en la tienda
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

    private void almorzar() {
        //La persona almuerza en el restaurante
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

    private void merendar() {
        //La persona merienda en el restaurante
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

    private void irAlFaro() {
        //La persona sube al faro y baja por los toboganes

        boolean subio = faro.subir(); //devuelve si pudo subir al faro
        if (subio) { //Si pudo subir al faro sigue
            System.out.println(nombre + " esta subiendo al faro.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Error en la espera de la subida al faro");
            }
            faro.llegarCima();
            System.out.println(nombre + " llego al mirador.");
            System.out.println(nombre + " hace fila para los toboganes.");
            int tobogan = faro.esperarTobogan();
            System.out.println(nombre + " esta bajando en el tobogan n°" + tobogan + ".");
            try {
                Thread.sleep(rand.nextInt(500, 1000));
            } catch (InterruptedException e) {
                System.out.println("Error en la espera de la bajada del tobogan");
            }
            faro.bajandoEnTobogan(tobogan);
            yaSubioFaro = true;
        }


    }

    private void hacerSnorkel() {
        //La persona hace snorkel en la laguna
        snorkel.conseguirEquipo();
        System.out.println(nombre + " esta haciendo snorkel en la laguna.");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.out.println("Error en la espera del snorkel");
        }
        snorkel.devolverEquipo();
        System.out.println(nombre + " devolvio el equipo de snorkel.");
        yaHizoSnorkel = true;
    }

    private void irCarrera() {
        boolean subio;
        //La persona participa en la carrera de gomones
        boolean comoVa = rand.nextBoolean();
        if (comoVa) { //TRUE va en tren, FALSE va en bici
            subio = irTren();
        } else {
            subio = irBici();
        }

        if (subio) {
            String pertenencias = "Cosas de " + nombre;
            int llave = standDePertenencias.dejarSusPertenencias(pertenencias);
            System.out.println(nombre + " dejo sus cosas y tomo la llave n°" + llave + ".");


            String recuperado = standDePertenencias.recogerSusPertenencias(llave);
            if(pertenencias.equals(recuperado)) {
                System.out.println(nombre + " recogio sus pertenencias: " + pertenencias);
            }else{
                System.out.println("Hubo un error y "+ nombre + " no pudo recuperar sus pertenencias.");
            }
        }

    }

    private boolean irTren(){
        boolean subio = trenInterno.subirse();
        if(subio) {
            trenInterno.bajarse();
        }
        return subio;

    }

    private boolean irBici(){
        boolean subio = standDeBicis.tomarBici();
        if(subio) {
            System.out.println(nombre + " esta yendo a la carrera en bici.");
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                System.out.println("Error en la subida a bici");
            }
            standDeBicis.devolverBici();
            System.out.println(nombre + " llego y devolvio la bici.");
        }
        return subio;

    }

    private void nadarDelfines() {


        yaNadoDelfines = true;
    }
}

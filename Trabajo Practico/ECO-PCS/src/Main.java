import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Main {

    //LLEGADA AL PARQUE
    private static Colectivo[] colectivos;
    private static Entrada entrada;

    //ACTIVIDADES
    private static Shop tienda;
    private static Reloj reloj;
    private static FaroMirador faro;
    private static lagunaSnorkel snorkel;
    private static Restaurante[] restaurantes;
    private static Carrera carrera;
    private static standBicis bicis;
    private static standPertenencias standP;
    private static TrenInterno tren;

    //RUNNABLES
    private static Persona[] personas;
    private static IncrementadorTiempo incTiempo;
    private static Colectivero[] colectiveros;
    private static AdministradorTobogan administrador;
    private static generadorPersonas[] generadores;
    private static ConductorTren conductorDelTren;
    private static Camioneta camioneta;
    private static Gomon[] gomones;
    private static GestorGomon gestorGomon;

    //HILOS
    private static Thread[] hilosPersonas;
    private static Thread[] hilosColectiveros;
    private static Thread[] hilosGeneradores;
    private static Thread[] hilosGomones;
    private static Thread hiloTiempo;
    private static Thread hiloAdmin;
    private static Thread hiloConductor;
    private static Thread hiloCamioneta;
    private static Thread hiloGestorGomon;

    //MISC
    private static String[] nombres = new String[200];
    private static String archivoEntrada = "src/Nombres";
    private static Random rand = new Random();

    public static void main(String[] args) {

        generarNombres();

        reloj();

        shop();

        entrada();

        faro();

        snorkel();

        restaurante();

        carrera();

        colectivo();

        personas();

        //CREACION DE HILOS
        crearHilos();

        //INICIO DE HILOS
        iniciarHilos();
    }

    private static void reloj() {
        // RELOJ
        int horaInicio = 9, tiempo15Minutos = 1000, apertura = 9, cierre = 17;
        reloj = new Reloj(horaInicio, apertura, cierre);
        incTiempo = new IncrementadorTiempo(reloj, tiempo15Minutos);
    }

    private static void entrada() {
        // ENTRADA
        int cantMolinetes = 3;
        entrada = new Entrada(cantMolinetes, reloj);
    }

    private static void shop() {
        // SHOP
        int cantCajas = 2;
        tienda = new Shop(cantCajas);
    }

    private static void faro() {
        // FARO
        int cantToboganes = 2, capacidadEscalera = 6;
        faro = new FaroMirador(cantToboganes, capacidadEscalera, reloj);
        administrador = new AdministradorTobogan(faro);
    }

    private static void snorkel() {
        // SNORKEL
        int cantSnorkel = 10, cantSalvavidas = 8, cantPatasRana = 12, cantAsistentes = 2;
        snorkel = new lagunaSnorkel(cantSnorkel, cantSalvavidas, cantPatasRana, cantAsistentes);
    }

    private static void restaurante() {
        // RESTAURANTES
        int cantRestaurantes = 3;
        restaurantes = new Restaurante[cantRestaurantes];
        for (int i = 0; i < cantRestaurantes; i++) {
            restaurantes[i] = new Restaurante(rand.nextInt(5, 10));
        }
    }

    private static void carrera() {
        //GOMONES

        int cantGomonesIndividuales = 10, cantGomonesDupla = 5, minimo = 12;
        carrera = new Carrera(4, cantGomonesIndividuales, cantGomonesDupla);

        gomones = new Gomon[cantGomonesIndividuales + cantGomonesDupla];

        for (int i = 0; i < cantGomonesIndividuales; i++) {
            gomones[i] = new Gomon(carrera, i, false);
        }

        for (int i = cantGomonesIndividuales; i < gomones.length; i++) {
            gomones[i] = new Gomon(carrera, i, true);
        }



        gestorGomon = new GestorGomon(carrera);

        int lugarTren = minimo + 10;
        tren = new TrenInterno(lugarTren, reloj);

        conductorDelTren = new ConductorTren(tren, reloj);

        int cantBicis = minimo + 2;
        bicis = new standBicis(cantBicis, reloj);

        int cantBolsos = minimo + 5, capacidadCamio = minimo + 2;
        standP = new standPertenencias(20);

        camioneta = new Camioneta(capacidadCamio, standP);
    }

    private static void colectivo() {
        // COLECTIVOS
        int cantColectivos = 3;
        int capacidadColectivo = 25;
        colectivos = new Colectivo[cantColectivos];
        colectiveros = new Colectivero[cantColectivos];
        for (int i = 0; i < cantColectivos; i++) {
            colectivos[i] = new Colectivo(capacidadColectivo, i + 1);
            colectiveros[i] = new Colectivero(colectivos[i], reloj);
        }
    }

    private static void personas() {
        // PERSONAS
        int cantPersonasIniciales = 15;
        personas = new Persona[cantPersonasIniciales];
        for (int i = 0; i < cantPersonasIniciales; i++) {
            personas[i] = new Persona(nombres[rand.nextInt(200)], entrada, reloj, colectivos, tienda, restaurantes, faro, snorkel, standP, tren, carrera, bicis);
        }

        // GENERADORES DE PERSONAS
        int cantGeneradores = 1;
        generadores = new generadorPersonas[cantGeneradores];
        for (int i = 0; i < cantGeneradores; i++) {
            generadores[i] = new generadorPersonas(nombres, colectivos, entrada, tienda, reloj, faro, snorkel, restaurantes, standP, tren, carrera, bicis);
        }
    }


    private static void crearHilos() {

        //HILOS
        hilosPersonas = new Thread[personas.length];
        hilosColectiveros = new Thread[colectiveros.length];
        hilosGeneradores = new Thread[generadores.length];
        hiloTiempo = new Thread(incTiempo);
        hiloAdmin = new Thread(administrador);
        hiloConductor = new Thread(conductorDelTren);
        hiloCamioneta = new Thread(camioneta);
        hiloGestorGomon = new Thread(gestorGomon);
        hilosGomones = new Thread[gomones.length];

        for (int i = 0; i < personas.length; i++) {
            hilosPersonas[i] = new Thread(personas[i]);
        }
        for (int i = 0; i < colectiveros.length; i++) {
            hilosColectiveros[i] = new Thread(colectiveros[i]);
        }

        for (int i = 0; i < hilosGeneradores.length; i++) {
            hilosGeneradores[i] = new Thread(generadores[i]);
        }

        for(int i = 0; i < hilosGomones.length; i++){
            hilosGomones[i] = new Thread(gomones[i]);
        }

    }

    private static void iniciarHilos() {

        hiloAdmin.start();
        hiloConductor.start();
        hiloCamioneta.start();
        hiloGestorGomon.start();
        hiloTiempo.start();

        for (int i = 0; i < hilosPersonas.length; i++) {
            hilosPersonas[i].start();
        }
        for (int i = 0; i < hilosColectiveros.length; i++) {
            hilosColectiveros[i].start();
        }


        for (int i = 0; i < hilosGeneradores.length; i++) {
            hilosGeneradores[i].start();
        }

        for (int i = 0; i < hilosGomones.length; i++) {
            hilosGomones[i].start();
        }


    }

    private static void generarNombres() {
        try {
            String linea;
            int i = 0;
            FileReader lector = new FileReader(archivoEntrada);
            BufferedReader bufferLector = new BufferedReader(lector);

            while ((linea = bufferLector.readLine()) != null && i < 200) {
                nombres[i] = linea;
                i++;
            }

            bufferLector.close();

        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage() + "\nEl archivo que queres leer no existe.");
        } catch (IOException ex) {
            System.err.println("Error leyendo o escribiendo en algun archivo.");

        }

    }
}

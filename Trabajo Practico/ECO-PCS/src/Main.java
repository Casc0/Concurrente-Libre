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

    //HILOS
    private static Persona[] personas;
    private static IncrementadorTiempo incTiempo;
    private static Colectivero[] colectiveros;
    private static AdministradorTobogan administrador;
    private static generadorPersonas[] generadores;

    //MISC
    private static String[] nombres = new String[200];
    private static String archivoEntrada = "src/Nombres";

    public static void main(String[] args) {
        Random rand = new Random();
        generarNombres();

        // ENTRADA
        int cantMolinetes = 3;
        entrada = new Entrada(cantMolinetes);

        // SHOP
        int cantCajas = 2;
        tienda = new Shop(cantCajas);

        // RELOJ
        int horaInicio = 9;
        reloj = new Reloj(horaInicio);
        incTiempo = new IncrementadorTiempo(reloj);

        // FARO
        int cantToboganes = 2, capacidadEscalera = 6;
        faro = new FaroMirador(cantToboganes, capacidadEscalera);
        administrador = new AdministradorTobogan(faro);

        // SNORKEL
        int cantSnorkel = 10, cantSalvavidas = 8, cantPatasRana = 12, cantAsistentes = 2;
        snorkel = new lagunaSnorkel(cantSnorkel, cantSalvavidas, cantPatasRana, cantAsistentes);


        // RESTAURANTES
        int cantRestaurantes = 3;
        restaurantes = new Restaurante[cantRestaurantes];
        for (int i = 0; i < cantCajas; i++) {
            restaurantes[i] = new Restaurante(rand.nextInt(5,10));
        }

        // COLECTIVOS
        int cantColectivos = 3;
        int capacidadColectivo = 25;
        colectivos = new Colectivo[cantColectivos];
        colectiveros = new Colectivero[cantColectivos];
        for (int i = 0; i < cantColectivos; i++) {
            colectivos[i] = new Colectivo(capacidadColectivo, i+1);
            colectiveros[i] = new Colectivero(colectivos[i], reloj);
        }

        // PERSONAS
        int cantPersonasIniciales = 100;
        personas = new Persona[cantPersonasIniciales];
        for (int i = 0; i < cantPersonasIniciales; i++) {
            personas[i] = new Persona(nombres[rand.nextInt(200)], entrada, reloj, colectivos, tienda, restaurantes, faro, snorkel);
        }

        // GENERADORES DE PERSONAS
        int cantGeneradores = 15;
        generadores = new generadorPersonas[cantGeneradores];
        for (int i = 0; i < cantGeneradores; i++) {
            generadores[i] = new generadorPersonas(nombres, colectivos, entrada, tienda, reloj, faro, snorkel, restaurantes);
        }

        //HILOS
        Thread[] hilosPersonas = new Thread[personas.length];
        Thread[] hilosColectiveros = new Thread[colectiveros.length];
        Thread[] hilosGeneradores = new Thread[generadores.length];
        Thread hiloTiempo = new Thread(incTiempo);

        //CREACION DE HILOS
        crearHilos(hilosPersonas, hilosColectiveros, hilosGeneradores);

        //INICIO DE HILOS
        iniciarHilos(hilosPersonas, hilosColectiveros, hiloTiempo, hilosGeneradores);
    }



    public static void crearHilos(Thread[] hilosPersonas, Thread[] hilosColectiveros, Thread[] hilosGeneradores) {
        for (int i = 0; i < personas.length; i++) {
            hilosPersonas[i] = new Thread(personas[i]);
        }
        for (int i = 0; i < colectiveros.length; i++) {
            hilosColectiveros[i] = new Thread(colectiveros[i]);
        }

        for (int i = 0; i < hilosGeneradores.length; i++) {
            hilosGeneradores[i] = new Thread(generadores[i]);
        }

    }

    public static void iniciarHilos(Thread[] hilosPersonas, Thread[] hilosColectiveros, Thread hiloTiempo, Thread[] hilosGeneradores) {

            for (int i = 0; i < hilosPersonas.length; i++) {
                hilosPersonas[i].start();
            }
            for (int i = 0; i < hilosColectiveros.length; i++) {
                hilosColectiveros[i].start();
            }
            hiloTiempo.start();

            for (int i = 0; i < hilosGeneradores.length; i++) {
                hilosGeneradores[i].start();
            }




    }

    public static void generarNombres() {
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

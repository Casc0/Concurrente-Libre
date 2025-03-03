import java.util.Random;

public class Persona implements Runnable {
    private String nombre;
    private Salon salon;
    private int cantTurnos;
    private String RESET = "\u001B[0m";
    private String PURPLE = "\u001B[35m";
    private String BLUE = "\u001B[34m";
    private String GREEN = "\u001B[32m";
    private String RED = "\u001B[31m";
    private String DARK_PURPLE = "\u001B[38;5;55m";
    private String DARK_BLUE = "\u001B[38;5;19m";
    private String DARK_GREEN = "\u001B[38;5;22m";

    Persona(Salon sal, String nom, int turnos) {
        salon = sal;
        nombre = nom;
        cantTurnos = turnos;
    }

    public void run() {
        try {
            Random rand = new Random();
            int i = 0;
            boolean noSeCancelo = false;
            do { //Si no logra entrar en el turno, va a intentar en el siguiente.

                int entroAlSalon = salon.llegarASalon(); // 1 si entro, 0 si no entro, -1 si no hay mas turnos

                if (entroAlSalon == 1) {
                    System.out.println(nombre + " llego al salon");

                    if (salon.esperarTurno()) { //Espera a que se llene el cupo, sino se cancela el turno
                        noSeCancelo = true;
                        int act1, act2;
                        act1 = elegirPrimeraActividad();
                        boolean eligio = false;
                        //ACTIVIDAD 1
                        do {
                            switch (act1) { //0 es Telas, 1 es Lyra, 2 es Yoga
                                //TELAS
                                case 0:
                                    if (salon.entrarTelas()) {
                                        eligio = true;
                                        System.out.println(PURPLE + nombre + " entro a Telas" + RESET);
                                        Thread.sleep(3000);
                                        salon.salirTelas();
                                    } else {
                                        System.out.println(DARK_PURPLE + nombre + " no pudo entrar a Telas" + RESET);
                                        act1++; //como no pudo entrar a la actividad, se le suma 1 para que pruebe en la siguiente
                                    }
                                    break;

                                //LYRA
                                case 1:
                                    if (salon.entrarLyra()) {
                                        eligio = true;
                                        System.out.println(GREEN + nombre + " entro a Lyra" + RESET);
                                        Thread.sleep(3000);
                                        salon.salirLyra();
                                    } else {
                                        System.out.println(DARK_GREEN + nombre + " no pudo entrar a Lyra" + RESET);
                                        act1++; //como no pudo entrar a la actividad, se le suma 1 para que pruebe en la siguiente
                                    }
                                    break;

                                //YOGA
                                case 2:
                                    if (salon.entrarYoga()) {
                                        eligio = true;
                                        System.out.println(BLUE + nombre + " entro a Yoga" + RESET);
                                        Thread.sleep(3000);
                                        salon.salirYoga();
                                    } else {
                                        System.out.println(DARK_BLUE + nombre + " no pudo entrar a Yoga" + RESET);
                                        act1 = 0; //como no pudo entrar a la actividad, prueba con la siguiente, que seria Acro
                                    }
                                    break;
                                default:
                                    System.out.println("Error en la eleccion de la actividad");
                                    break;
                            }
                        } while (!eligio);

                        //ACTIVIDAD 2 - Prueba con las dos actividades restantes y sino vuelve a la primera
                        //Ojala no sea dificil de entender la formula de los casos alternativos, pero
                        // asegura que se prueben las dos actividades restantes antes de repetir.

                        eligio = false;
                        act2 = elegirSegundaActividad(act1);
                        do {
                            switch (act2) { //0 es Telas, 1 es Lyra, 2 es Yoga
                                //TELAS
                                case 0:
                                    if (salon.entrarTelas()) {
                                        eligio = true;
                                        System.out.println(PURPLE + nombre + " entro a Telas" + RESET);
                                        Thread.sleep(3000);
                                        salon.salirTelas();
                                    } else {

                                        System.out.println(DARK_PURPLE + nombre + " no pudo entrar a Telas" + RESET);
                                        act2++; //como no pudo entrar a la actividad, se le suma 1 para que pruebe en la siguiente

                                        if (act2 == act1) {
                                            act1 = 0;
                                            act2++;
                                        }
                                    }
                                    break;

                                //LYRA
                                case 1:
                                    if (salon.entrarLyra()) {
                                        eligio = true;
                                        System.out.println(GREEN + nombre + " entro a Lyra" + RESET);
                                        Thread.sleep(3000);
                                        salon.salirLyra();
                                    } else {
                                        System.out.println(DARK_GREEN + nombre + " no pudo entrar a Lyra" + RESET);
                                        act2++; //como no pudo entrar a la actividad, se le suma 1 para que pruebe en la siguiente

                                        if (act2 == act1) {
                                            act1 = 1;
                                            act2 = 0;
                                        }
                                    }
                                    break;
                                //YOGA
                                case 2:
                                    if (salon.entrarYoga()) {
                                        eligio = true;
                                        System.out.println(BLUE + nombre + " entro a Yoga" + RESET);
                                        Thread.sleep(3000);
                                        salon.salirYoga();
                                    } else {
                                        System.out.println(DARK_BLUE + nombre + " no pudo entrar a Yoga" + RESET);
                                        act2 = 0; //como no pudo entrar a la actividad, se le suma 1 para que pruebe en la siguiente

                                        if (act2 == act1) {
                                            act1 = 2;
                                            act2++;
                                        }
                                    }
                                    break;
                                default:
                                    System.out.println("Error en la eleccion de la actividad");
                                    break;
                            }
                        } while (!eligio);

                        salon.salir();
                    } else {
                        System.out.println(RED + nombre + " tuvo su turno cancelado en su intento N°" + (i + 1) + RESET);
                        i++;
                        Thread.sleep(23000 + rand.nextInt(10000));
                    }
                }else if(entroAlSalon == 0) {
                    System.out.println(RED + nombre + " no pudo entrar al salon en su intento N°" + (i + 1) + RESET);
                    i++;
                    Thread.sleep(23000 + rand.nextInt(10000));
                } else { //Resguardo en caso de que alguien entre cuando no hay más turnos.
                    System.out.println(RED + nombre + " no pudo entrar a ningun turno" + RESET);
                    i = cantTurnos;
                }
            } while (!noSeCancelo && i < cantTurnos);

            System.out.println(nombre + " se retira del salon");

        } catch (InterruptedException e) {
            System.out.println("Error de interrupcion");

        }
    }

    public int elegirPrimeraActividad() {
        Random rand = new Random();
        return rand.nextInt(0, 2);
    }

    public int elegirSegundaActividad(int x) {
        Random rand = new Random();
        //modulo 3 para mantenerse dentro del rango 0-3
        return (x + rand.nextInt(1, 2)) % 3;
    }
}

import java.io.*;
import java.util.Random;

public class GeneradorPedidos implements Runnable{
    private Restaurante restaurante;
    private String[] listaNombres = new String[200];
    private static final String archivoEntrada = "src/Nombres";

    public GeneradorPedidos( Restaurante restaurante){
        this.restaurante = restaurante;
        generarNombres(listaNombres);
    }

    @Override
    public void run() {
            Random rand = new Random();
            while(true) {
                //crea el pedido
                Pedido pedido = new Pedido(listaNombres[rand.nextInt(0,199)], rand.nextInt());

                //manda a iniciarlo
                restaurante.iniciarPedido(pedido);
                System.out.println("Llego un pedido de " + pedido.getNombreCliente());

                /* Eliminar comentario para ver el proceso de generación de pedidos con más pausa
                //un sleep para evitar acumulación de mucho texto y facilitar la lectura de la ejecución
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    System.out.println("Error de interrupcion");
                }
                */
            }

    }



    public static void generarNombres(String[] nombres) {
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

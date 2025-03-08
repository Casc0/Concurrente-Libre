import java.util.Random;

public class Pedido{
    private boolean tipoPizza; //true es vegana, false es napolitana
    private String nombreCliente;
    private int ID;

    public Pedido(String nombre, int tipo) {
        Random rand = new Random();
        if((tipo%2) == 0){
            tipoPizza = true;
        }else{
            tipoPizza = false;
        }

        this.nombreCliente = nombre;

        ID = rand.nextInt();
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public boolean getTipoPizza() {
        return tipoPizza;
    }

    public boolean equals(Pedido pedido) {
        return this.ID == pedido.ID && this.nombreCliente.equals(pedido.nombreCliente);
    }

}

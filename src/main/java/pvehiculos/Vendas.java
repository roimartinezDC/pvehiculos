package pvehiculos;

public class Vendas {

    private int id;
    private String dni;
    private String codveh;

    public Vendas(int id, String dni, String codveh) {
        this.id = id;
        this.dni = dni;
        this.codveh = codveh;
    }

    public int getId() {
        return id;
    }

    public String getDni() {
        return dni;
    }

    public String getCodveh() {
        return codveh;
    }

    @Override
    public String toString() {
        return "Vendas{" +
                "id=" + id +
                ", dni='" + dni + '\'' +
                ", codvhe='" + codveh + '\'' +
                '}';
    }
}

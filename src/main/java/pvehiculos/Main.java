package pvehiculos;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {
    static MongoClient mongo = new MongoClient("localhost", 27017);
    static MongoDatabase test = mongo.getDatabase("test");
    static MongoCollection<Document> vendasMongo = test.getCollection("vendas");
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("./db/vehicli.odb");
    static EntityManager emOdb = emf.createEntityManager();
    static Connection conPsql;
    static {
        try {
            conPsql = conexionPostgres();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) throws SQLException {
        ArrayList<Vendas> vendas = obtenerVendas();
        for (Vendas venda : vendas) {
            PreparedStatement st = conPsql.prepareStatement("INSERT INTO finalveh (id, dni, nomec, vehf) VALUES ((?), (?), (?), ((?), (?)))");
            st.setInt(1, venda.getId());
            st.setString(2, venda.getDni());
            st.setString(3, getNomCli(venda));
            st.setString(4, getNomVhe(venda));
            st.setInt(5, calcPrezoFinal(venda));
            st.execute();
            System.out.println("INSERT");
        }
        conPsql.close();
        emOdb.close();
        mongo.close();
    }
    public static String getNomVhe(@NotNull Vendas venda) {
        Query query = emOdb.createQuery("SELECT v.nomveh FROM Vehiculos v WHERE v.codveh=:codveh", String.class);
        query.setParameter("codveh", venda.getCodveh());
        return (String) query.getSingleResult();
    }
    public static String getNomCli(@NotNull Vendas venda) {
        Query query = emOdb.createQuery("SELECT c.nomec FROM Clientes c WHERE c.dni=:dni", String.class);
        query.setParameter("dni", venda.getDni());
        return (String) query.getSingleResult();
    }
    public static int calcPrezoFinal(@NotNull Vendas venda) {
        int desconto;
        if (getNCompras(venda) > 0) {
            desconto = 500;
        } else {
            desconto = 0;
        }
        return (getPrezOrixe(venda)-((2019-getAnoMatr(venda))*500)-desconto);
    }
    public static int getAnoMatr(@NotNull Vendas venda) {
        Query query = emOdb.createQuery("SELECT v.anomatricula FROM Vehiculos v WHERE v.codveh=:codveh");
        query.setParameter("codveh", venda.getCodveh());
        return (int) query.getSingleResult();
    }
    public static int getPrezOrixe(@NotNull Vendas venda) {
        Query query = emOdb.createQuery("SELECT v.prezoorixe FROM Vehiculos v WHERE v.codveh=:codveh");
        query.setParameter("codveh", venda.getCodveh());
        return (int) query.getSingleResult();
    }
    public static int getNCompras(@NotNull Vendas venda) {
        Query query = emOdb.createQuery("SELECT c.ncompras FROM Clientes c WHERE c.dni=:dni");
        query.setParameter("dni", venda.getDni());
        return (int) query.getSingleResult();
    }
    public static ArrayList<Vendas> obtenerVendas() {
        ArrayList<Vendas> vendas = new ArrayList<>();
        for (Document fila : vendasMongo.find()) {
            vendas.add(new Vendas(fila.getInteger("_id"), fila.getString("dni"), fila.getString("codveh")));
        }
        return vendas;
    }
    public static Connection conexionPostgres() throws SQLException {
        String driver = "jdbc:postgresql:";
        String host = "//localhost:";
        String porto = "5432";
        String sid = "postgres";
        String usuario = "dam2a";
        String password = "castelao";
        String url = driver + host+ porto + "/" + sid;
        return DriverManager.getConnection(url,usuario,password);
    }
}
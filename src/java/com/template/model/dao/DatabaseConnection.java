package com.template.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.template.util.ThemeContext;

public class DatabaseConnection {

    static String url = "jdbc:postgresql://localhost:5432/postgres";
    static String user = "postgres";
    static String password = "postgres";

    static {
        initTables();
    }

    public static void initTables() {
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            if (ThemeContext.isBobEsponja()) {
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS spongebob_items (" +
                    "id SERIAL PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "category VARCHAR(100) DEFAULT 'Siri Cascudo'," +
                    "description TEXT," +
                    "rarity VARCHAR(50) DEFAULT 'Comum'," +
                    "price VARCHAR(50) NOT NULL" +
                    ")"
                );

                stmt.execute("ALTER TABLE spongebob_items ADD COLUMN IF NOT EXISTS category VARCHAR(100) DEFAULT 'Siri Cascudo';");
                stmt.execute("ALTER TABLE spongebob_items ADD COLUMN IF NOT EXISTS rarity VARCHAR(50) DEFAULT 'Comum';");

                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM spongebob_items")) {
                    if (rs.next() && rs.getInt(1) < 5) {
                        stmt.execute("DELETE FROM spongebob_items;");
                        stmt.execute(
                            "INSERT INTO spongebob_items (name, category, description, rarity, price) VALUES " +
                            "('Hambúrguer de Siri', 'Siri Cascudo 🍔', 'O hambúrguer mais delicioso e viciante dos sete mares, com a fórmula secreta do Seu Siriguejo.', 'Lendário ⭐', '1.25')," +
                            "('Hambúrguer de Siri Duplo Monstruoso', 'Siri Cascudo 🍔', 'Duas carnes suculentas com queijo marinho derretido, alface e picles crocantes.', 'Raro 💎', '2.50')," +
                            "('Fórmula Secreta do Siri Cascudo', 'Siri Cascudo 🍔', 'Guardada no cofre forte a sete chaves! O maior alvo do vilão Plankton.', 'Ultra Secreto 🔒', '999999.99')," +
                            "('Hambúrguer com Geléia de Água-Viva', 'Siri Cascudo 🍔', 'Combinação exótica inventada pelo Bob Esponja que virou febre gastronômica.', 'Raro 💎', '3.00')," +
                            "('Espátula Hidrodinâmica Turbo', 'Casa do Bob Esponja 🍍', 'Espátula com batedores turbo de ovos e motor de popa para fritar centenas de hambúrgueres.', 'Lendário ⭐', '149.90')," +
                            "('Rede de Caçar Água-Viva Deluxe', 'Campo das Águas-Vivas 🪼', 'Modelo Olly Orelha Furada com malha reforçada e cabo de bambu polido.', 'Raro 💎', '39.90')," +
                            "('Geléia de Água-Viva Azul Real', 'Campo das Águas-Vivas 🪼', 'Extraída da lendária água-viva azul nobre. Doce, efervescente e brilhante.', 'Lendário ⭐', '45.00')," +
                            "('Clarinet do Lula Molusco', 'Casa do Lula Molusco 🗿', 'Instrumento afinado com maestria para afastar qualquer cliente faminto em segundos.', 'Comum 📦', '89.90')," +
                            "('Auto-Retrato \"Negrito e Brilhante\"', 'Casa do Lula Molusco 🗿', 'Pintura incompreendida e esnobe criada pelo virtuoso artista Lula Molusco.', 'Comum 📦', '15.00')," +
                            "('Pedra de Estimação Rocky', 'Lagoa Goo 🏖️', 'A pedra de corrida mais rápida e destemida de toda a história da Fenda do Biquíni.', 'Lendário ⭐', '0.50')," +
                            "('Comida Enlatada Snail-Po', 'Casa do Bob Esponja 🍍', 'A ração favorita do caracol Gary feita com algas frescas e mexilhões marinhos.', 'Comum 📦', '5.50')," +
                            "('Balde de Isca Podre', 'Balde de Lixo 🪣', 'Gororoba suspeita criada por Plankton para tentar competir com o Siri Cascudo.', 'Proibido ☠️', '0.10')," +
                            "('Capacete de Controle Mental', 'Balde de Lixo 🪣', 'Invenção maligna do Plankton para tentar dominar os cidadãos da Fenda.', 'Ultra Secreto 🔒', '499.90')," +
                            "('Traje de Mergulho da Sandy', 'Domo da Árvore 🐿️', 'Escafandro hermético espacial resistente à alta pressão oceânica com florzinha do Texas.', 'Raro 💎', '850.00')," +
                            "('Frasco de Bolhas Mágicas', 'Lagoa Goo 🏖️', 'Frasco de sabão com técnica secreta: pé direito no chão, dobrar o joelho e soprar!', 'Raro 💎', '2.00')"
                        );
                    }
                }
            } else {
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS shopItems (" +
                    "id SERIAL PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "description TEXT," +
                    "price VARCHAR(50) NOT NULL" +
                    ")"
                );
            }
        } catch (SQLException e) {
            System.out.println("Erro ao inicializar tabela: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException("Falha na conexao: " + e.getMessage(), e);
        }
    }
}

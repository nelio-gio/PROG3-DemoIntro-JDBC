package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Properties props = new Properties();


        try (InputStream in = Main.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                System.err.println("Fichier db.properties introuvable dans resources !");
                return;
            }
            props.load(in);
        } catch (IOException e) {
            System.err.println("Erreur lecture db.properties : " + e.getMessage());
            return;
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");


        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connexion réussie !");

            String selectSql = "SELECT id, name, age FROM person";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSql)) {

                System.out.println("Contenu actuel de la table person :");
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int age = rs.getInt("age");
                    System.out.printf("id=%d, name=%s, age=%d%n", id, name, age);
                }
            }


            String insertSql = "INSERT INTO person (name, age) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, "Charlie");
                ps.setInt(2, 28);
                int affected = ps.executeUpdate();
                System.out.println("Lignes insérées : " + affected);
            }


            try (Statement stmt2 = conn.createStatement();
                 ResultSet rs2 = stmt2.executeQuery(selectSql)) {
                System.out.println("Table person après insertion :");
                while (rs2.next()) {
                    System.out.printf("id=%d, name=%s, age=%d%n",
                            rs2.getInt("id"), rs2.getString("name"), rs2.getInt("age"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package me.tooster;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.ibatis.jdbc.ScriptRunner;

public class ShopApp extends Application {

    static Stage primaryStage;
    Controller controller;

    public static Stage getPrimaryStage() { return primaryStage; }

    private static Connection connection;

    public static Connection getConnection() { return connection; }

    public static void main(String[] args) { launch(args); }

    void dbPrepare(Connection con) {
        System.err.print("Assuring the schema has correct tables and triggers...");
        ScriptRunner scriptExecutor = new ScriptRunner(con);
        try {
            scriptExecutor.runScript(new FileReader(getClass().getResource("/SQL/DB_INIT.sql").getFile()));
        } catch (FileNotFoundException ignored) {
        }
        System.err.print("\tDONE\n");
    }

    boolean dbConnect() {

        try (InputStream propInput = getClass().getResourceAsStream("/database.properties")) {
            Properties prop = new Properties();
            prop.load(propInput);

            try {
                connection = DriverManager.getConnection(
                        prop.getProperty("db.url"),
                        prop.getProperty("db.user"),
                        prop.getProperty("db.password"));

                System.err.println("Connected to PostgreSQL database on " + prop.getProperty("db.url") + ".");

                ShopApp.connection = connection;
                dbPrepare(connection);
                return true;

            } catch (SQLException e) {
                System.err.println("Database connection failure");
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.err.println("Cannot open database.properties file.");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ShopApp.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/App.fxml"));
        Scene scene = new Scene(loader.load());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Shop with DB");
        primaryStage.show();
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(470);

        if (!dbConnect())
            Platform.exit();

        controller = loader.getController();
        Controller.setInstance(controller);
        controller.fetchAll();
    }
}

package controller;

import ds.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.plaf.nimbus.State;
import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class StartApp extends Application{
    private FinanceManagementIS fmis = new FinanceManagementIS();
    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginPage.fxml"));
        Parent root = loader.load();

        LoginPage loginPage = loader.getController();
        //fmis = DataRW.loadFinanceManagementSystemFromFile();
        fmis = new FinanceManagementIS();
        loginPage.setFmis(fmis);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }



    @Override
    public void stop() throws IOException {
//        System.out.println("Saving the file");
//        DataRW.writeFinanceManagementSystemToFile(fmis);
    }

    public static void main(String[] args) {
//        DatabaseController.getAllCategories(11);
        launch(args);
    }
}

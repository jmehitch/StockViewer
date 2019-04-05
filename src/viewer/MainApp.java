package viewer;

import java.io.IOException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import viewer.model.Stock;
import viewer.view.HomeController;

/**
 * MainApp class that starts and runs JavaFX program.
 * Starts up homeLayout and sets as main stage, connects to HomeController.
 *
 * @author - 1854326
 */
public class MainApp extends Application {

    /* Initialises instance variable - array of stock names and symbols */
    public String[][] stocksArray;

    /* Initialises Observable list of stocks for use in TableView displaying
    all stocks in homeLayout */
    private ObservableList<Stock> stockData = FXCollections.observableArrayList();

    /* MainApp constructor */
    public MainApp() {
        /* Adds all stock names and symbols to array */
        this.stocksArray = new String[][] {
                {"Ashtead Group plc", "AHT.L"},
                {"Antofagasta plc", "ANTO.L"},
                {"BAE Systems plc", "BA.L"},
                {"British American Tobacco plc", "BATS.L"},
                {"Coca-Cola HBC AG", "CCH.L"},
                {"Carnival plc", "CCL.L"},
                {"Centrica plc", "CNA.L"},
                {"Compass Group plc", "CPG.L"},
                {"Experian plc", "EXPN.L"},
                {"EasyJet plc", "EZJ.L"},
                {"GKN plc", "GKN.L"},
                {"Mediclinic International plc", "MDC.L"},
                {"Provident Financial plc", "PFG.L"},
                {"Paddy Power Betfair plc", "PPB.L"},
                {"Prudential plc", "PRU.L"},
                {"Persimmon plc", "PSN.L"},
                {"Reckitt Benckiser Group plc", "RB.L"},
                {"Royal Dutch Shell plc", "RDSA.L"},
                {"Rolls-Royce Holdings plc", "RR.L"},
                {"Schroders plc", "SDR.L"},
                {"Shire plc", "SHP.L"},
                {"Sky plc", "SKY.L"},
                {"SSE plc", "SSE.L"},
                {"St. James's Place plc", "STJ.L"},
                {"Tesco plc", "TSCO.L"},
                {"TUI AG", "TUI.L"},
                {"Vodafone Group plc", "VOD.L"},
                {"Worldpay Group plc", "WPG.L"}
        };
        /* Adds all stock names and stock symbols to ObservableList stockData
        for use in displaying stocks in table */
        for (int i=0; i<stocksArray.length; i++) {
            stockData.add(new Stock(stocksArray[i][0], stocksArray[i][1]));
        }
    }

    /**
     * Main method for JavaFX programme - connects to FXML file containing
     * homeLayout from SceneBuilder and sets this on primaryStage
     *
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        /* Sets title of window as StockViewerApp */
        primaryStage.setTitle("StockViewerApp");
        try {
            /* Connects and loads to FXML file */
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/HomeLayout.fxml"));
            BorderPane homeLayout = loader.load();

            /* Connects to HomeController class */
            HomeController controller = loader.getController();
            controller.setMainApp(this);

            /* Displays and sets homeLayout as scene on primary stage */
            Scene scene = new Scene(homeLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Public method to access and return stockData */
    public ObservableList<Stock> getStockData() {
        return stockData;
    }

    /**
     * Main method - ignored in JavaFX applications.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
package viewer.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import viewer.MainApp;
import viewer.model.Stock;

import java.awt.*;
import java.io.*;
import java.util.List;

/**
 * Controller for the homeLayout.
 * Displays all stocks and data generates in Stock model class and reacts to
 * events of buttons clicked on homeLayout.
 *
 * @author - 1854326
 */
public class HomeController {

    /* Creates reference to MainApp for later use */
    private MainApp mainApp;

    /* Connects to and declares Table variables from FXML file */
    @FXML
    private TableView<Stock> stockTable;
    @FXML
    private TableColumn<Stock, String> companyNameColumn;
    @FXML
    private TableColumn<Stock, String> stockSymbolColumn;
    @FXML
    private TableColumn<Stock, String> latestPriceColumn;

    /* Connects to and initialises image and stock information from FXML */
    @FXML
    private ImageView stockLogo;
    @FXML
    private Label companyNameLabel;
    @FXML
    private Label latestPriceLabel;
    @FXML
    private Label priceChangeLabel;

    /* Connects to and initialises bottom section of homeLayout for display */
    @FXML
    private AnchorPane initialBottomPane;
    @FXML
    private AnchorPane bottomPane;
    @FXML
    private TabPane tabPane;

    /**
     * Default controller constructor
     */
    public HomeController() {}

    /**
     * Initialise method for HomeController - runs automatically once all FXML
     * fields/items have been declared and the default constructor runs
     */
    @FXML
    private void initialize() {
        /* Sets the the value of properties CellValueFactories to Stock
        properties */
        companyNameColumn.setCellValueFactory(cellData ->
                cellData.getValue().companyNameProperty());
        stockSymbolColumn.setCellValueFactory(cellData ->
                cellData.getValue().stockSymbolProperty());
        latestPriceColumn.setCellValueFactory(cellData ->
                cellData.getValue().latestPriceProperty());

        /* Initially calls displayStockInfo method to clear the bottom
        section */
        displayStockInfo(null);

        /* Listens for currently selected stock to change and displays newly
        selected stock details */
        stockTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> displayStockInfo(newValue));
    }

    /**
     * Method that responds to the generate report button on homeLayout
     * interface (from FXML file)
     */
    @FXML
    private void reactReportButton() {
        /* Obtains an array of the stock names and symbols */
        String[][] stocks = mainApp.stocksArray;

        /* Creates and opens new text file */
        File report = new File("StocksReport.txt");
        Integer count = 0;      // Initialises counter for stock numbering

        /* Obtains stock data using stock model class methods and writes the
        data to the newly created StocksReport.txt file */
        try (Writer writer = new BufferedWriter(new FileWriter(report))) {
            for(int i=0; i<stocks.length; i++) {
                count += 1;
                List<String> data = new Stock().obtainStockReportData(stocks[i][1]);
                String line1 = "Number: " + count.toString();
                String line2 = "\nStock Symbol: " + stocks[i][1];
                String line3 = "\nCompany Name: " + stocks[i][0];
                String line4 = "\nHighest: " + data.get(0) + " (at price: " + data.get(1) + ")";
                String line5 = "\nLowest: " + data.get(2) + " (at price: " + data.get(3) + ")";
                String line6 = "\nAverage close: " + data.get(4);
                String line7 = "\nClose: " + data.get(5);
                String line8 = "\n-------------------------\n";
                String line = line1+line2+line3+line4+line5+line6+line7+line8;
                writer.write(line);
            }

            /* Creates and displays an alert box on screen displaying
            information about newly created stock report text file*/
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Report Created");
            alert.setHeaderText(null);
            alert.setContentText("Stocks_Report.txt generated successfully and saved to StockViewer folder - it will open once confirmed");
            alert.showAndWait();

            /* Opens stock report file in default text editor after alert */
            Desktop.getDesktop().edit(report);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that responds to view detailed stock information button on
     * homeLayout interface (from FXML file) - opens new tab, loads and new
     * displays layout (connects to StockController)
     */
    @FXML
    public void reactStockButton() {
        /* Obtains company name of stock clicked from Label */
        String company = companyNameLabel.getText();

        /* Checks that user has clicked on a company - if not then they are
        prompted to select a company to view */
        if (company.equals("Select a company from the table")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No company selected!");
            alert.setHeaderText(null);
            alert.setContentText("Please select a company from the table to " +
                                 "get a detailed stock view.");
            alert.showAndWait();
        } else {
            /* Opens a new tab in the main window for detailed stock info
            to be displayed in */
            Tab newTab = new Tab(company);
            tabPane.getTabs().add(newTab);
            SingleSelectionModel<Tab> selection = tabPane.getSelectionModel();
            selection.select(newTab);

            /* Initialises main BorderPane in StockView.fxml file */
            BorderPane stockView;
            try {
                /* Loads new StockView FXML file and sets StockView layout as
                the content (displays) in the new tab  */
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(MainApp.class.getResource(
                                          "view/StockView.fxml"));

                /* Sets the controller for the StockView FXML file as
                StockController */
                loader.setController(new StockController(company));
                stockView = loader.load();
                newTab.setContent(stockView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method closes & stops running application when main stock tab is closed
     */
    @FXML
    private void reactCloseMainTab() {
        Platform.exit();
        System.exit(0);
    }

    /**
     * Connects to MainApp using MainApp reference above and fills the main
     * TableView (stockTable) with stock data
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        stockTable.setItems(mainApp.getStockData());
    }

    // Displays stock information in panel below table

    /**
     * Method displays stock information in bottom pane below table on main
     * layout (initially displays welcome message when no stock selected yet)
     * @param stock
     */
    private void displayStockInfo(Stock stock) {
        if (stock != null)  {
            /* Hides initial welcome message */
            bottomPane.setVisible(true);
            initialBottomPane.setVisible(false);

            /* Gets relevant stock symbol to obtain logo */
            String stockSymbol = stock.getStockSymbol();
            String[] parts = stockSymbol.split("[.]");

            /* Obtains logo from folder and displays in the ImageView field */
            String imgFile = parts[0] + ".png";
            String url = "src/StockLogos/"+imgFile;
            Image logo = new Image("file:src/StockLogos/"+imgFile);
            stockLogo.setImage(logo);
            stockLogo.setPreserveRatio(true);
            stockLogo.setSmooth(true);

            /* Obtains stock information and displays using labels in bottom
            pane */
            companyNameLabel.setText(stock.getCompanyName());
            latestPriceLabel.setText(stock.getLatestPrice());
            priceChangeLabel.setText(stock.getTenDayPriceChange().toString() +
                                     "%");
            /* Sets colour of ten day price change information depending on
            value */
            double priceChange = stock.getTenDayPriceChange();
            if (priceChange > 0) {
                priceChangeLabel.setTextFill(Color.GREEN); // Green if positive
            } else if (priceChange < 0) {
                priceChangeLabel.setTextFill(Color.RED); // Red if negative
            } else {
                priceChangeLabel.setTextFill(Color.GREY); // Black if 0.00
            }
        } else {
            /* Initially hides main bottom pane and shows welcome message */
            initialBottomPane.setVisible(true);
            bottomPane.setVisible(false);

        }
    }
}

package viewer.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import viewer.MainApp;
import viewer.model.DayData;
import viewer.model.Stock;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Controller class for detailed stock view tabs
 *
 * @author - 1854326
 */
public class StockController {

    /* Initialises stock instance variables */
    private String stockName;
    private String stockSymbol = "";

    /* Initialises ObservableList for table displaying historical stock data */
    private ObservableList<DayData> dayData = FXCollections.
                                              observableArrayList();

    /* Connects and initialises labels in top bar of stock view tab from
    FXML file */
    @FXML
    private Label stockSymbolLabel;
    @FXML
    private Label stockNameLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label changeLabel;

    /* Connects to and initialises chart pane from FXML file */
    @FXML
    private AnchorPane chartPane;

    /* Connects to and initialises historical data table and all table columns
    from FXML file */
    @FXML
    private TableView<DayData> historicalTable;
    @FXML
    private TableColumn<DayData, String> dateColumn;
    @FXML
    private TableColumn<DayData, String> openColumn;
    @FXML
    private TableColumn<DayData, String> highColumn;
    @FXML
    private TableColumn<DayData, String> lowColumn;
    @FXML
    private TableColumn<DayData, String> closeColumn;
    @FXML
    private TableColumn<DayData, String> volumeColumn;
    @FXML
    private TableColumn<DayData, String> adjCloseColumn;

    /* Connects to and initialises ImageViews, Lables and Hyperlinks from FXML
    file used in News Headlines tab */
    @FXML
    private ImageView newsImage1;
    @FXML
    private ImageView newsImage2;
    @FXML
    private ImageView newsImage3;
    @FXML
    private Label newsHeadline1;
    @FXML
    private Label newsHeadline2;
    @FXML
    private Label newsHeadline3;
    @FXML
    private Hyperlink newsLink1;
    @FXML
    private Hyperlink newsLink2;
    @FXML
    private Hyperlink newsLink3;

    /**
     * Default StockController method
     * @param stockName - name of company
     */
    public StockController(String stockName) {
        this.stockName = stockName;
    }


    /**
     * Initialise method for StockController - runs automatically once all FXML
     * fields/items have been declared and the default constructor runs
     */
    @FXML
    private void initialize() {
        /* Obtains array of all stocks declared in MainApp */
        String[][] stocks = new MainApp().stocksArray;

        /* Sets stockName and stockSymbol labels from stocks array */
        stockNameLabel.setText(stockName);
        for (int i = 0; i < stocks.length; i++) {
            if (stocks[i][0] == stockName) {
                stockSymbol = stocks[i][1];
                stockSymbolLabel.setText(stockSymbol);
            }
        }

        /* Obtains and displays latest stock price and the ten day percentage
        price change */
        priceLabel.setText(new Stock(stockName, stockSymbol).
                getLatestPrice());
        Double changeValue = new Stock(stockName, stockSymbol).
                getTenDayPriceChange();

        /* Set colour of ten day price change and displays move direction
        depending on value */
        if (changeValue > 0) {
            changeLabel.setText(changeValue.toString() + "% ▲");
            changeLabel.setTextFill(Color.GREEN);   // Green and up if positive
        } else if (changeValue < 0) {
            changeLabel.setText(changeValue.toString() + "% ▼");
            changeLabel.setTextFill(Color.RED);     // Red and down if negative
        } else {
            changeLabel.setText(changeValue.toString() + "% -");
        }

        /* Calls method to display 65 day price chart */
        loadChart(stockSymbol, 66);

        /* Fills historical stock data table with data from DayData model */
        historicalTable.setItems(getDayData());

        /* Sets the the value of properties CellValueFactories to properties
        of stock data*/
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().
                dateProperty());
        openColumn.setCellValueFactory(cellData -> cellData.getValue().
                openPriceProperty());
        highColumn.setCellValueFactory(cellData -> cellData.getValue().
                highPriceProperty());
        lowColumn.setCellValueFactory(cellData -> cellData.getValue().
                lowPriceProperty());
        closeColumn.setCellValueFactory(cellData -> cellData.getValue().
                closePriceProperty());
        volumeColumn.setCellValueFactory(cellData -> cellData.getValue().
                volumeProperty());
        adjCloseColumn.setCellValueFactory(cellData -> cellData.getValue().
                adjClosePriceProperty());

        /* Calls methods to load and display historical data and news headlines
        in their respective tabs in the bottom pane */
        loadHistoricalData();
        displayNewsHeadlines();
    }


    /**
     * Method loads and displays price chart in chartPane
     *
     * @param stockSymbol - stock symbol of company
     * @param dateRange - number of days for data to display in chart
     */
    private void loadChart(String stockSymbol, Integer dateRange) {
        /* Retrieves stock data and initialises price and dates lists */
        List<String> data = new Stock().retrieveStockData(stockSymbol);
        List<Double> dailyClosePrice = new ArrayList<>();
        List<String> dailyDates = new ArrayList<>();

        /* Iterates through stock data and obtains relevant data for date
        range chosen */
        for (int i = 1; i < dateRange; i++) {
            String[] dayData = data.get(i).split("[,]");
            dailyClosePrice.add(Double.parseDouble(dayData[4]));
            dailyDates.add(dayData[0].substring(2));
        }

        /* Reverses lists to plot in correct order */
        Collections.reverse(dailyClosePrice);
        Collections.reverse(dailyDates);

        /* Clears currently displayed chart and initialises axes */
        chartPane.getChildren().clear();
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        /* Creates priceChart with axes */
        AreaChart<String, Number> priceChart = new AreaChart<>(xAxis, yAxis);

        /* Formats axes, xAxis range, graph layout and labels */
        double minValue = Collections.min(dailyClosePrice) * 0.99;
        double maxValue = Collections.max(dailyClosePrice) * 1.01;
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(minValue);
        yAxis.setUpperBound(maxValue);
        yAxis.setLabel("Close Price (GBp)");
        priceChart.setHorizontalGridLinesVisible(false);
        priceChart.setLegendVisible(false);
        priceChart.setMaxHeight(308);
        priceChart.setMinHeight(308);
        priceChart.setMaxWidth(800);
        priceChart.setMinWidth(800);

        /* Initialises new chart series and adds data to series */
        XYChart.Series series = new XYChart.Series();
        for (int i = 0; i < dailyDates.size(); i++) {
            series.getData().add(new XYChart.Data(dailyDates.get(i),
                    dailyClosePrice.get(i)));
        }
        priceChart.getData().add(series);

        /* Changes colour of area chart depending on whether price has
        increased or decreased over the specific time frame */
        if (dailyClosePrice.get(0) < dailyClosePrice.get(dailyClosePrice.size()-1)) {
            Node fill = series.getNode().lookup(".chart-series-area-fill");
            fill.setStyle("-fx-fill: rgba(61, 234, 136, 0.43)");
            Node line = series.getNode().lookup(".chart-series-area-line");
            line.setStyle("-fx-stroke: rgb(51, 153, 56)");
        }

        /* Displays chart in chartPane */
        chartPane.getChildren().add(priceChart);
    }

    /**
     * Method loads and displays volume chart in chartPane
     * @param stockSymbol - stock symbol of company
     * @param dateRange - number of days for data to display in chart
     */
    @FXML
    private void loadVolumeChart(String stockSymbol, Integer dateRange) {
        /* Retrieves stock data and initialises volume and dates lists */
        List<String> data = new Stock().retrieveStockData(stockSymbol);
        List<Integer> dailyVolume = new ArrayList<>();
        List<String> dailyDates = new ArrayList<>();

        /* Iterates through stock data and obtains relevant data for date
        range chosen */
        for (int i = 1; i < dateRange; i++) {
            String[] dayData = data.get(i).split("[,]");
            dailyVolume.add(Integer.parseInt(dayData[5]));
            dailyDates.add(dayData[0].substring(2));
        }

        /* Reverses lists to plot in correct order */
        Collections.reverse(dailyVolume);
        Collections.reverse(dailyDates);

        /* Clears currently displayed chart and initialises axes */
        chartPane.getChildren().clear();
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        /* Creates priceChart with axes */
        BarChart<String, Number> volumeChart = new BarChart<>(xAxis, yAxis);

        /* Formats graph sizing and yAxis label */
        yAxis.setLabel("Volume (GBp)");
        volumeChart.setLegendVisible(false);
        volumeChart.setMaxWidth(800);
        volumeChart.setMaxHeight(308);
        volumeChart.setMinWidth(800);
        volumeChart.setMinHeight(308);

        /* Initialises new chart series and adds data to series */
        XYChart.Series series = new XYChart.Series();
        for (int i = 0; i < dailyDates.size(); i++) {
            series.getData().add(new XYChart.Data(dailyDates.get(i),
                    dailyVolume.get(i)));
        }
        volumeChart.getData().add(series);

        /* Displays chart in chartPane */
        chartPane.getChildren().add(volumeChart);

    }

    /* Reloads and displays 7 day price chart on '7D' button click */
    @FXML
    private void reloadPriceChart7() {
        loadChart(stockSymbol, 9);
    }

    /* Reloads and displays 10 day price chart on '10D' button click */
    @FXML
    private void reloadPriceChart10() {
        loadChart(stockSymbol, 12);
    }

    /* Reloads and displays 31 day chart on '31D' button click */
    @FXML
    private void reloadPriceChart31() {
        loadChart(stockSymbol, 33);
    }

    /* Reloads and displays 65 day chart on '65D' button click */
    @FXML
    private void reloadPriceChart65() {
        loadChart(stockSymbol, 66);
    }

    /* Reloads and displays 7 day volume chart on '7D' button click */
    @FXML
    private void reloadVolChart7() {
        loadVolumeChart(stockSymbol, 9);
    }

    /* Reloads and displays 10 day volume chart on '10D' button click */
    @FXML
    private void reloadVolChart10() {
        loadVolumeChart(stockSymbol, 12);
    }

    /* Reloads and displays 31 day volume chart on '31D' button click */
    @FXML
    private void reloadVolChart31() {
        loadVolumeChart(stockSymbol, 33);
    }

    /* Reloads and displays 65 day volume chart on '65D' button click */
    @FXML
    private void reloadVolChart65() {
        loadVolumeChart(stockSymbol, 66);
    }

    /**
     * Adds daily stock data to ObservableList to insert into TableView for
     * historical data table in bottom pane
     */
    private void loadHistoricalData() {
        /* Iterates through every day's data and adds to dayData
        Observable list */
        for (int i = 1; i < 66; i++) {
            dayData.add(new DayData(stockSymbol, i));
        }
    }

    private ObservableList<DayData> getDayData() {
        return dayData;
    }

    /**
     * Method obtains and displays up to 3 news headlines (with images and
     * links) relating to the stock - news gathered from News API
     * (https://newsapi.org/)
     */
    private void displayNewsHeadlines() {
        /* Formats stock name for use in URL for API request */
        String stockString = stockName.replaceAll("\\s", "%20");

        /* API key needed for authentication */
        String apiKey = "1a48424aae9a4de28b11ae30ee91069a";

        try {
            /* Concatenates string parameters to construct URL */
            URL url = new URL("https://newsapi.org/v2/everything?q=\"" +
                              stockString + "\"&language=en&apiKey=" + apiKey);

            /* Makes HTTP connection and sends GET request to API */
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            /* BufferedReader initialised to read incoming HTTP response */
            BufferedReader input = new BufferedReader(new InputStreamReader(
                                                      con.getInputStream()));

            /* StringBuffer and input line initialised  */
            StringBuffer response = new StringBuffer();
            String inLine;

            /* Reads each line obtained by BufferedReader input from HTTP
            response and appends line to response StringBuffer */
            while ((inLine = input.readLine()) != null) {
                response.append(inLine);
            }
            input.close();

            /* Converts response StringBuffer to String */
            String json = response.toString();

            /* Obtains news article titles from JSON response using regex */
            List<String> allHeadlines = new ArrayList<>();
            Pattern headline = Pattern.compile("\"title\":\"(.*?)\"");
            Matcher headlines = headline.matcher(json);
            while (headlines.find()) {
                allHeadlines.add(headlines.group(1));
            }

            /* Obtains news HyperLinks from JSON response using regex */
            List<String> allLinks = new ArrayList<>();
            Pattern link = Pattern.compile("\"url\":\"(.*?)\"");
            Matcher links = link.matcher(json);
            while (links.find()) {
                allLinks.add(links.group(1));
            }

            /* Obtains news HyperLinks from JSON response using regex */
            List<String> allImgLinks = new ArrayList<>();
            Pattern imgLink = Pattern.compile("\"urlToImage\":\"(.*?)\"");
            Matcher imgLinks = imgLink.matcher(json);
            while (imgLinks.find()) {
                allImgLinks.add(imgLinks.group(1));
            }

            /* Works out number of articles obtained from API request */
            int numHeadlines = allHeadlines.size();
            int numLinks = allLinks.size();
            int numImgs = allImgLinks.size();
            int[] numNews = new int[]{numHeadlines, numLinks, numImgs};
            Arrays.sort(numNews);
            int numArticles = numNews[0];


            /* Sets default text for before link */
            String newsText = "Click here to find out more: ";

            /* Each article with corresponding links and images that are
            obtained from the API request are then inserted to the News
            Headlines tab in the bottom pane with clickable HyperLink to
            follow link */
            if (numArticles == 0) {
                newsHeadline1.setText("No stories found.");

                /* For one article */
            } else if (numArticles == 1) {
                /* Sets headline, link text and image to Labels/ImageViews
                from FXML file */
                newsHeadline1.setText(allHeadlines.get(0));
                newsLink1.setText(newsText + allLinks.get(0));
                Image img1 = new Image(allImgLinks.get(0));
                newsImage1.setImage(img1);
                newsImage1.setPreserveRatio(true);
                newsImage1.setSmooth(true);

                /* Hides other Labels and ImageViews */
                newsHeadline2.setVisible(false);
                newsImage2.setVisible(false);
                newsLink2.setVisible(false);
                newsHeadline3.setVisible(false);
                newsImage3.setVisible(false);
                newsLink3.setVisible(false);

                /* Makes HyperLink clickable and open in default browser */
                newsLink1.setOnAction((ActionEvent e) -> {
                    try {
                        Desktop.getDesktop().browse(new URI(allLinks.get(0)));

                    } catch (URISyntaxException i) {
                        i.printStackTrace();
                    } catch (IOException i) {
                        i.printStackTrace();
                    }
                });

                /* For two articles */
            } else if (numArticles == 2) {
                /* Sets headlines, link text and images to Labels/ImageViews
                from FXML file */
                newsHeadline1.setText(allHeadlines.get(0));
                newsLink1.setText(newsText + allLinks.get(0));
                Image img1 = new Image(allImgLinks.get(0));
                newsImage1.setImage(img1);
                newsImage1.setPreserveRatio(true);
                newsImage1.setSmooth(true);
                newsHeadline2.setText(allHeadlines.get(1));
                newsLink2.setText(newsText + allLinks.get(1));
                Image img2 = new Image(allImgLinks.get(1));
                newsImage2.setImage(img2);
                newsImage2.setPreserveRatio(true);
                newsImage2.setSmooth(true);

                /* Hides other Labels and ImageViews */
                newsHeadline3.setVisible(false);
                newsImage3.setVisible(false);
                newsLink3.setVisible(false);

                /* Makes HyperLinks clickable and open in default browser */
                newsLink1.setOnAction((ActionEvent e) -> {
                    try {
                        Desktop.getDesktop().browse(new URI(allLinks.get(0)));

                    } catch (URISyntaxException i) {
                        i.printStackTrace();
                    } catch (IOException i) {
                        i.printStackTrace();
                    }
                });
                newsLink2.setOnAction((ActionEvent e) -> {
                    try {
                        Desktop.getDesktop().browse(new URI(allLinks.get(1)));

                    } catch (URISyntaxException i) {
                        i.printStackTrace();
                    } catch (IOException i) {
                        i.printStackTrace();
                    }
                });

                /* For three articles */
            } else {
                /* Sets headlines, link text and images to Labels/ImageViews
                from FXML file */
                newsHeadline1.setText(allHeadlines.get(0));
                newsLink1.setText(newsText + allLinks.get(0));
                Image img1 = new Image(allImgLinks.get(0));
                newsImage1.setImage(img1);
                newsImage1.setPreserveRatio(true);
                newsImage1.setSmooth(true);
                newsHeadline2.setText(allHeadlines.get(1));
                newsLink2.setText(newsText + allLinks.get(1));
                Image img2 = new Image(allImgLinks.get(1));
                newsImage2.setImage(img2);
                newsImage2.setPreserveRatio(true);
                newsImage2.setSmooth(true);
                newsHeadline3.setText(allHeadlines.get(2));
                newsLink3.setText(newsText + allLinks.get(2));
                Image img3 = new Image(allImgLinks.get(2));
                newsImage3.setImage(img3);
                newsImage3.setPreserveRatio(true);
                newsImage3.setSmooth(true);
                /* Makes HyperLinks clickable and open in default browser */
                newsLink1.setOnAction((ActionEvent e) -> {
                    try {
                        Desktop.getDesktop().browse(new URI(allLinks.get(0)));

                    } catch (URISyntaxException i) {
                        i.printStackTrace();
                    } catch (IOException i) {
                        i.printStackTrace();
                    }
                });
                newsLink2.setOnAction((ActionEvent e) -> {
                    try {
                        Desktop.getDesktop().browse(new URI(allLinks.get(1)));

                    } catch (URISyntaxException i) {
                        i.printStackTrace();
                    } catch (IOException i) {
                        i.printStackTrace();
                    }
                });
                newsLink2.setOnAction((ActionEvent e) -> {
                    try {
                        Desktop.getDesktop().browse(new URI(allLinks.get(2)));

                    } catch (URISyntaxException i) {
                        i.printStackTrace();
                    } catch (IOException i) {
                        i.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

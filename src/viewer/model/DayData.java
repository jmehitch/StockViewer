package viewer.model;

import javafx.beans.property.*;

/**
 * The model class for data for each day for a stock - defines a day object
 * and obtains the data in its variables/properties
 *
 * @author - 1854326
 */
public class DayData {

    /* Initialises all instance variables - properties to be inputted into
    historical data table in detailed stock view tab */
    private final StringProperty date;
    private final StringProperty openPrice;
    private final StringProperty highPrice;
    private final StringProperty lowPrice;
    private final StringProperty closePrice;
    private final StringProperty volume;
    private final StringProperty adjClosePrice;


    /**
     * DayData constructor method
     * @param stockSymbol - stock symbol
     * @param day - date for data to obtain
     */
    public DayData(String stockSymbol, Integer day) {

        /* Retrieves stock data using Stock method and creates an array of the
        data for the given date */
        String stockData = new Stock().retrieveStockData(stockSymbol).get(day);
        String[] stockDataSplit = stockData.split("[,]");

        /* Each piece of data is obtained from array created above,
        stored as SimpleStringProperties to input into ObservableList for
        TableView 8*/
        this.date = new SimpleStringProperty(stockDataSplit[0]);
        this.openPrice = new SimpleStringProperty(stockDataSplit[1]);
        this.highPrice = new SimpleStringProperty(stockDataSplit[2]);
        this.lowPrice = new SimpleStringProperty(stockDataSplit[3]);
        this.closePrice = new SimpleStringProperty(stockDataSplit[4]);
        this.volume = new SimpleStringProperty(stockDataSplit[5]);
        this.adjClosePrice = new SimpleStringProperty(stockDataSplit[6]);
    }

    /* Public method to access and return date */
    public String getDate() {
        return date.get();
    }

    /* Public method to access and return date property */
    public StringProperty dateProperty() {
        return date;
    }

    /* Public method to access and return open price property */
    public StringProperty openPriceProperty() {
        return openPrice;
    }

    /* Public method to access and return high price property */
    public StringProperty highPriceProperty() {
        return highPrice;
    }

    /* Public method to access and return low price property */
    public StringProperty lowPriceProperty() {
        return lowPrice;
    }

    /* Public method to access and return close price property */
    public StringProperty closePriceProperty() {
        return closePrice;
    }

    /* Public method to access and return volume value */
    public String getVolume() {
        return volume.get();
    }

    /* Public method to access and return volume property */
    public StringProperty volumeProperty() {
        return volume;
    }

    /* Public method to access and return adjusted close price property */
    public StringProperty adjClosePriceProperty() {
        return adjClosePrice;
    }

}


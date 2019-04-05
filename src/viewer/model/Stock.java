package viewer.model;

import javafx.beans.property.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The model class for a stock - defines a stock object and its variables/
 * properties
 *
 * @author - 1854326
 */
public class Stock {

    /* Declares stock's instance variables - properties used to be inputted
    into stock table on homeLayout */
    private final StringProperty companyName;
    private final StringProperty stockSymbol;
    private final StringProperty latestPrice;
    private final DoubleProperty priceChange;

    /**
    * Default stock constructor
    */
    public Stock() {
        this.companyName = null;
        this.stockSymbol = null;
        this.latestPrice = null;
        this.priceChange = null;
    }

    /**
     * Stock constructor that sets variable values using companyName
     * and stockSymbol as parameters
     *
     * @param companyName - name of company
     * @param stockSymbol - stock symbol of company
     */
    public Stock(String companyName, String stockSymbol) {
        this.companyName = new SimpleStringProperty(companyName);
        this.stockSymbol = new SimpleStringProperty(stockSymbol);

        /* Obtains and sets latest closing price property */
        String stockData = retrieveStockData(stockSymbol).get(1);
        String[] stockDataSplit = stockData.split("[,]");
        String closingPrice = stockDataSplit[4];
        this.latestPrice = new SimpleStringProperty(closingPrice);

        /* Calculates and sets percentage 10 day price change variable */
        String dayTenData = retrieveStockData(stockSymbol).get(11);
        String[] dayTenDataSplit = dayTenData.split("[,]");
        double closingPriceNum = Double.parseDouble(stockDataSplit[4]);
        double dayTenClosingPrice = Double.parseDouble(dayTenDataSplit[4]);
        double tenDayPriceChange = ((closingPriceNum-dayTenClosingPrice)
                                    /closingPriceNum)*100;
        tenDayPriceChange = Math.round(tenDayPriceChange*100);
        tenDayPriceChange = tenDayPriceChange/100;
        this.priceChange = new SimpleDoubleProperty(tenDayPriceChange);
    }

    /**
     * Retrieves stock data for company from CSV files in StockData folder
     *
     * @param stockSymbol - company stockSymbol
     * @return lines - ArrayList of strings with stock data of each day on each
     *                 line
     */
    public List<String> retrieveStockData(String stockSymbol) {
        /* Gets CSV file name from stockSymbol parameter */
        String[] parts = stockSymbol.split("[.]");
        String fileName = parts[0] + ".csv";
        List<String> lines = new ArrayList<>();

        try {
            /* Reads the CSV file data line by line and adds data to lines
            List */
            BufferedReader reader = new BufferedReader(
                    new FileReader("src/StockData/" + fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * Method that calculates and returns the relevant stock data needed for
     * the report of all stocks in an ArrayList
     *
     * @param stockSymbol - company stock symbol
     * @return stockReportData - ArrayList of strings of stock data
     */
    public List<String> obtainStockReportData(String stockSymbol) {
        /* Retrieves stock data using retrieveStockData method */
        List<String> data = retrieveStockData(stockSymbol);

        /* Initialises variables needed for stocks report */
        String highDate = "";
        double stockHighest = 0.00;
        String lowDate = "";
        double stockLowest = 100000000.00;
        double closeTotal = 0.00;
        String lastClose = "";

        /* Iterates through stock data and obtains relevant information */
        for (int i=1; i<data.size(); i++) {
            String[] dayData = data.get(i).split("[,]");
            double dayHigh = Double.parseDouble(dayData[2]);
            double dayLow = Double.parseDouble(dayData[3]);
            closeTotal += Double.parseDouble(dayData[4]);
            if (dayHigh > stockHighest) {
                stockHighest = dayHigh;
                highDate = dayData[0];
            }
            if (dayLow < stockLowest) {
                stockLowest = dayLow;
                lowDate = dayData[0];
            }
            if (i==data.size()-1) {
                lastClose = dayData[6];
            }
        }


        /* Rounds average close price to 2 decimal places for presentation */
        double averageClose = closeTotal/data.size();
        averageClose = Math.round(averageClose*100);
        averageClose = averageClose/100;

        /* Stores stock data as strings in List for presenting in report */
        List<String> stockReportData = new ArrayList<>();
        String averageCloseStr = String.valueOf(averageClose);
        String stockHighestStr = String.valueOf(stockHighest);
        String stockLowestStr = String.valueOf(stockLowest);
        stockReportData.add(highDate);
        stockReportData.add(stockHighestStr);
        stockReportData.add(lowDate);
        stockReportData.add(stockLowestStr);
        stockReportData.add(averageCloseStr);
        stockReportData.add(lastClose);

        return stockReportData;
    }

    /* Public method to access and return company name */
    public String getCompanyName() {
        return companyName.get();
    }

    /* Public method to access and return company name property */
    public StringProperty companyNameProperty() {
        return companyName;
    }

    /* Public method to access and return stock symbol */
    public String getStockSymbol() {
        return stockSymbol.get();
    }

    /* Public method to access and return stock symbol property */
    public StringProperty stockSymbolProperty() {
        return stockSymbol;
    }

    /* Public method to access and return latest price of stock */
    public String getLatestPrice() {
        return latestPrice.get();
    }

    /* Public method to access and return latest price property of stock */
    public StringProperty latestPriceProperty() {
        return latestPrice;
    }

    /* Public method to access and return ten day price change of stock */
    public Double getTenDayPriceChange() {
        return priceChange.get();
    }
}


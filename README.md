# StockViewer
GUI for viewing stock data from CSV files

## Skills Developed: 
- OOD in Java (MVC method followed) 
- Creating GUI applications in JavaFX 
- Dealing with REST API requests and responses in Java
- Design and UI improvement

## Application Interface Guide & Screenshots
![Alt text](/screenshots/MainStockScreen.png "Main Stock Screen")

When the application is opened the user is shown the main stock screen, this displays all of the stocks in a table with Company Name, Stock Symbol and Latest Share Price all shown. 

![Alt text](/screenshots/MainScreenWithStockSelected.png "Once Stock Selected Screen")

Once the user selects a stock more details about the stock are displayed in the bottom pane of the application, the stock name, latest share price and the percentage price change over the last ten days are displayed. This stock information changes dynamically when a new stock is selected from the table.

![Alt text](/screenshots/ReportGeneratedOfAllStocks.png "Text File of Stock Report Generated")

There is also a button below the table containing all of the stocks labelled ‘Generate Report of all Stocks’ – this button is used to generate the report text file (shown above). Once clicked the text file is created containing information for each stock and the user is notified with an alert box that this has happened.

![Alt text](/screenshots/DetailedStockViewScreen.png "Detailed Stock View in New Tab")

The user then returns to the main screen and can choose to click the ‘See Detailed Stock View’ button for any of the stocks in the table. 
Once clicked, the detailed stock view opens in a new tab for the selected stock, as shown above for the EasyJet stock.

![Alt text](/screenshots/DetailedStockViewScreenVol.png "Detailed Stock View Showing Volume Chart")

The user can view a range of graphs for the stock’s closing price and volume data. The 65 day volume chart for the EasyJet stock is shown above. 

![Alt text](/screenshots/NewsHeadlines.png "News Headlines for Stock Shown in Bottom Pane of Detailed Views")

Below the chart display is a tab pane, with tabs labelled ‘Historical Stock Data’ and ‘News Headlines’. The historical stock data tab shows all of historical data for the stock in a table. The news headlines tab displays up to three headlines (depending on how many are obtained), with a clickable hyperlink and a corresponding image for the headline within a scrollable pane - this is shown above.

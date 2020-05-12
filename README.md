# msmquote
**msmquote** is a Java application for updating stock quote data and currency exchange rates in Microsoft Money files. The quote data and exchange rates are obtained from the Yahoo Finance quote API.

**msmquote** updates quote data for stocks, bonds, mutual funds and market indices. Currency exchange rate updates are applied to Microsoft Money's currency table.

**msmquote** can also be used to update historical quote data from a CSV file generated using the Yahoo Finance historical prices download facility.

## Getting Started
Download the latest [msmquote JAR](https://github.com/36bits/msmquote/releases) to your machine and run it from the command line as follows:

`java -cp msmquote-3.0.0.jar uk.co.pueblo.msmquote.Update moneyfile.mny password source`

Parameters:
* **moneyfile.mny** is the MS Money file you wish to update
* **password** is the file password if applicable (omit if the file is not password protected)
* **source** is source of the quote data

The **source** parameter can be set to retrieve quote data from one of three sources:

* **Source type 1: Yahoo Finance API with auto-completed URL**

  Use the following source parameter to retrieve quote data using a list of investment and currency exchange rate symbols automatically generated from your Money file: 
   
  `"https://query2.finance.yahoo.com/v7/finance/quote?symbols="`

  See the [Usage Notes](#auto-completed-url) below.

* **Source type 2: Yahoo Finance API with user-completed URL**

  Retrieve quote data using a list of investment and currency exchange rate symbols defined on the command line after the *symbols=* statement in the source parameter.

  For example, the following source parameter will retrieve quota data for Walmart Inc., Tesco PLC, Carrefour SA, the FTSE-100 index and the Pound Sterling/Euro exchange rate:
   
  `"https://query2.finance.yahoo.com/v7/finance/quote?symbols=WMT,TSCO.L,CA.PA,^FTSE,GBPEUR=X"`
   
  Replace the symbols after the *symbols=* statement with those for the investments and currency exchange rates in your Money file that you wish to update. It should be possible to include any symbol for which quote data is available on [Yahoo Finance](https://finance.yahoo.com/). The symbols must match those defined in your Money file. See the [Usage Notes](#user-completed-url) below.

* **Source type 3: CSV file** 

  Quote data will be retrieved from a CSV file generated using the Yahoo Finance historical prices download facility.

  For example, the following source parameter will update quote data for Tesco PLC with historical data contained in a CSV file:
   
  `TSCO.L.csv`

  The filename must be in the format *symbol.csv*, where *symbol* matches the symbol defined in your Money file for the investment that you wish to update.

## Usage Notes
### Auto-Completed URL
* **Selecting which investments to update**

  From the 'Investing' drop-down menu in Microsoft Money go to 'Portfolio-->Update prices-->Pick prices to download'. From there check those investments for which you wish to update quote data when **msmquote** is next run.

* **Selecting which exchange rates to update**

  From the 'Tools' drop-down menu in Microsoft Money go to 'Options-->Currencies'. From there check 'Update exchange rate online' for those currencies for which you wish to obtain an exchange rate when **msmquote** is next run. In order to reduce the amount of data requested from the Yahoo Finance API it is advisable to un-check all the currencies for which an updated exchange rate is not required.
  
* **Default indices**

  A newly created Microsoft Money file includes a default set of market indices for which **msmquote** will attempt to update quote data, even if those indices are not included in any investment account. The symbols for these indices should be updated to match the Yahoo Finance equivalents as follows: from the 'Investing' drop-down menu in Microsoft Money go to 'Portfolio-->Work with investments-->Choose a specific investment' and from there choose the index for which you wish to update the symbol.
     
### User-Completed URL
* **Currency exchange rate symbol format**
  
  The Yahoo Finance quote API understands currency exchange rate symbols in the format 'AAABBB=X', where 'AAA' and 'BBB' are the ISO codes of the currencies that you wish to obtain an exchange rate for. 'AAA' typically should be set to the ISO code of the base currency in your Microsoft Money currency table ('Options-->Currencies' from the Microsoft Money 'Tools' drop-down menu).
  
  For example, if your base currency is EUR then you would use the symbol 'EURCHF=X' to obtain the exchange rate for Swiss Francs to the Euro.  

### General
* **Symbol truncation**
  
  Money limits the maximum length of investment symbols to 12 characters. Where necessary, **msmquote** will truncate symbols received in the quote data to 12 characters, on the assumption that the truncated symbol is unique in the Money file.

## Exit Codes
* **0** Execution completed successfully
* **1** Execution completed with warnings
* **2** Execution terminated due to errors

## Currently Updated Quote Data
* **Equities:** price, open, high, low, volume, day change, 52 week high, 52 week low, bid, ask, capitalisation, shares outstanding, PE, dividend yield.
* **Bonds and indices:** price, open, high, low, volume, day change, 52 week high, 52 week low, bid, ask.
* **Mutual funds:** price, day change, 52 week high, 52 week low.
* **Currencies:** exchange rate.

## Tested Environments
**msmquote** has been tested with the following MS Money versions and operating systems:
* MS Money 2005 (14.0), UK version, on MS Windows 10
* MS Money 2004 (12.0), UK version, on MS Windows 10

The following quote types have been tested:
* UK equities and bonds
* US equities and mutual funds
* Global market indices
* GBP exchange rates

Example tables sizes in the Money files used for testing:
* Security (SEC): 236 rows
* Security price (SP): 106,000 rows
* Currency (CRNC): 69 rows
* Exchange rate (CRNC_EXCHG): 79 rows

## Prerequisites
This project requires a Java runtime environment (JRE) of version 8 or later.

## Author
Jonathan Casiot.

## Licence
This project is licenced under the GNU GPL Version 3 - see the [LICENCE](./LICENSE) file.

## With Thanks To
* Hung Le for Sunriise, the original MS Money quote updater.
* [Yahoo Finance](https://finance.yahoo.com/) for a decent stock quote API.
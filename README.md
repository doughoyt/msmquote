# msmquote
**msmquote** is a Java application for updating Microsoft Money files with stock quotes and currency exchange rates retrieved from the Yahoo Finance quote API or from a CSV file generated by the Yahoo Finance historical prices download facility.
## Getting Started
Download the latest **msmquote** [JAR](https://github.com/36bits/msmquote/releases) to your machine and run as follows:

`java -cp msmquote-2.0.4-beta.jar uk.co.pueblo.msmquote.Update moneyfile.mny password source`

Parameters:
* **moneyfile.mny** is the MS Money file you wish to update
* **password** is the file password if applicable (omit if the file is not password protected)
* **source** is the URL of the Yahoo Finance quote API or the name of a CSV file generated by the Yahoo Finance historical prices download facility

Examples of the **source** parameter:

* `"https://query2.finance.yahoo.com/v7/finance/quote?symbols=WMT,TSCO.L,CA.PA,^FTSE,GBPEUR=X"`

This will update quotes for Walmart Inc., Tesco PLC, Carrefour SA, the FTSE-100 index and the Pound Sterling/Euro exchange rate, using data retrieved from the Yahoo Finance quote API. Replace the symbols after the *symbols=* statement in the URL with those for the quotes you wish to update. It should be possible to include any symbol for which a quote is available on [Yahoo Finance](https://finance.yahoo.com/) and the symbols must match those defined in the Money file.

* `TSCO.L.csv`

This will update quotes for Tesco PLC with historical data from a CSV file generated by the Yahoo Finance historical prices download facility. The filename must be in the format *symbol*.csv, where *symbol* matches a symbol defined in the Money file.

Money limits the maximum length of symbols to 12 characters. Where necessary, **msmquote** will truncate symbols received in the quote data to 12 characters, on the assumption that the truncated symbol is unique in the Money file.

## Exit Codes

* 0 Execution completed successfully
* 1 Execution completed with warnings
* 2 Execution terminated due to errors 

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
* Yahoo for a decent stock quote API.
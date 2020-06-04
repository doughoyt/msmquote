# msmquote
**msmquote** is a Java application for updating investment quote data and currency exchange rates in Microsoft Money files. The quote data and exchange rates are obtained from APIs provided by [Yahoo Finance](https://finance.yahoo.com/).

**msmquote** updates quote data for stocks, bonds, mutual funds, market indices and currency exchange rates. Exchange rate updates are applied to Microsoft Money's exchange rate table.

**msmquote** can also be used to update historical quote data for a single investment with data retrieved from either the Yahoo Finance API or a CSV file generated using the Yahoo Finance historical prices download facility.

## Getting Started
Download the latest [msmquote JAR](https://github.com/36bits/msmquote/releases) to your machine and run it from the command line as follows:

`java -cp msmquote-3.1.2.jar uk.co.pueblo.msmquote.Update moneyfile.mny password source`

Parameters:
* **moneyfile.mny** is the Microsoft Money file you wish to update
* **password** is the file password if applicable (omit if the file is not password protected)
* **source** is the source of the quote data

The **source** parameter can be set to retrieve and update quote data from one of four sources:

1. **The Yahoo Finance API with auto-completed URL**

   Use the following source parameter to update quote data using a list of investment and currency exchange rate symbols automatically generated from your Money file: 
   
   `"https://query2.finance.yahoo.com/v7/finance/quote?symbols="`

   See the [Usage Notes](#auto-completed-url) below.

2. **The Yahoo Finance API with user-completed URL**

   Define your own list of investment and currency exchange rate symbols to update after the `symbols=` statement in the source parameter.

   For example, the following source parameter will update quota data for Walmart Inc., Tesco PLC, Carrefour SA, the FTSE-100 index and the Pound Sterling/Euro exchange rate:
   
   `"https://query2.finance.yahoo.com/v7/finance/quote?symbols=WMT,TSCO.L,CA.PA,^FTSE,GBPEUR=X"`
   
   Replace the symbols after the `symbols=` statement with those for the investments and currency exchange rates in your Money file that you wish to update. It should be possible to include any symbol for which quote data is available on [Yahoo Finance](https://finance.yahoo.com/). The symbols must match those defined in your Money file. See the [Usage Notes](#user-completed-url) below.
   
3. **Historical data from the Yahoo Finance API**

   Update historical quote data for a single investment with data retrieved from the Yahoo Finance API. For example, the following source parameter will update one month's worth of quote data for Tesco PLC (symbol TSCO.L): 
   
   `"https://query2.finance.yahoo.com/v7/finance/chart/TSCO.L?range=1mo&interval=1d&indicators=quote&includeTimestamps=true"`
   
   See the [Usage Notes](#historical-data-api) below.

4. **Historical data from a Yahoo Finance CSV file**

   Update historical quote data for a single investment using a CSV file generated by the Yahoo Finance historical prices download facility. The CSV filename must be in the format `symbol_currency_quotetype.csv`, where:   
   - `symbol` matches the symbol in your Money file that you wish to update
   - `currency` is the three-character Yahoo Finance currency code of the quote data, for example 'GBp'
   - `quotetype` is the quote type, currently one of `EQUITY`, `BOND`, `INDEX` or `MUTUALFUND`.       

   For example, the following source parameter will update quote data for Tesco PLC (symbol TSCO.L) from a CSV file containing quote values in British pence:   
   
   `"TSCO.L_GBp_EQUITY.csv"`

## Usage Notes
### Auto-Completed URL
* **Display a list of the automatically generated symbols**

  To display a list of the symbols generated from your Microsoft Money file **without** updating any quote data, simply append a `?` to the source parameter as follows: 

  `"https://query2.finance.yahoo.com/v7/finance/quote?symbols=?"`

* **Selecting which investments to update in Microsoft Money**

  From the *Investing* drop-down menu in Microsoft Money go to *Portfolio->Update prices->Pick prices to download*. From there check those investments for which you wish to update quote data when **msmquote** is next run.

* **Selecting which exchange rates to update in Microsoft Money**

  From the *Tools* drop-down menu in Microsoft Money go to *Options->Currencies*. From there check *Update exchange rate online* for those currencies for which you wish to obtain an exchange rate when **msmquote** is next run. In order to reduce the amount of data requested from the Yahoo Finance API it is advisable to un-check all the currencies for which an updated exchange rate is not required.
  
* **Default indices in Microsoft Money**

  A newly created Microsoft Money file includes a default set of market indices for which **msmquote** will attempt to update quote data, even if those indices are not included in any investment account. It is advisable to update the symbols for these indices to match the Yahoo Finance equivalents as follows: from the *Investing* drop-down menu in Microsoft Money go to *Portfolio->Work with investments->Choose a specific investment* and from there choose the index for which you wish to update the symbol.
     
### User-Completed URL
* **Yahoo Finance currency exchange rate symbols**
  
  The Yahoo Finance quote API understands currency exchange rate symbols in the format `AAABBB=X`, where `AAA` and `BBB` are the ISO codes of the currencies for which you wish to obtain an exchange rate. `AAA` typically should be set to the ISO code of the base currency in your Microsoft Money currency table (*Options->Currencies* from the Microsoft Money *Tools* drop-down menu).
  
  For example, if your base currency is EUR then you would use the symbol `EURCHF=X` to obtain the exchange rate for Swiss Francs to the Euro.  

### Historical data API
* **Quote data range**

  The *range=* parameter in the URL for the historical data API controls the range of quote data to be retrieved for the given symbol. It takes one of the following arguments: `1d`, `5d`, `1mo`, `3mo`, `6mo`, `1y`, `2y`, `5y`, `10y`, `ytd`, or `max`.

### General
* **Symbol truncation**
  
  Money limits the maximum length of investment symbols to 12 characters. Where necessary, **msmquote** will truncate symbols received in the quote data to 12 characters, on the assumption that the truncated symbol is unique in the Money file.

## Exit Codes
* `0` Execution completed successfully.
* `1` Execution completed with warnings.
* `2` Execution terminated due to errors.

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
Jonathan Casiot  
[E-mail](mailto:jonathan@pueblo.co.uk)  
[Telegram](https://t.me/thirtysixbits)

## Licence
This project is licenced under the GNU GPL Version 3 - see the [LICENCE](./LICENSE) file.

## With Thanks To
* Hung Le for Sunriise, the original MS Money quote updater.
* Yahoo Finance for a decent stock quote API.
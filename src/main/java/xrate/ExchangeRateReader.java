package xrate;

import java.io.*;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.net.*;


/**
 * Provide access to basic currency exchange rate services.
 */
public class ExchangeRateReader {

    private String accessKey;
    private String baseURL;

    /**
     * Construct an exchange rate reader using the given base URL. All requests will
     * then be relative to that URL. If, for example, your source is Xavier Finance,
     * the base URL is http://api.finance.xaviermedia.com/api/ Rates for specific
     * days will be constructed from that URL by appending the year, month, and day;
     * the URL for 25 June 2010, for example, would be
     * http://api.finance.xaviermedia.com/api/2010/06/25.xml
     * 
     * @param baseURL the base URL for requests
     */
    public ExchangeRateReader(String baseURL) {
        /*
         * DON'T DO MUCH HERE! People often try to do a lot here, but the action is
         * actually in the two methods below. All you need to do here is store the
         * provided `baseURL` in a field (which you have to declare) so it will be
         * accessible in the two key functions. (You'll need it there to construct
         * the full URL.)
         */

       this.baseURL = baseURL;

        // Reads the Fixer.io API access key from the appropriate
        // environment variable.
        // You don't have to change this call.
        readAccessKey();
    }

    /**
     * This reads the `fixer_io` access key from from the system environment and
     * assigns it to the field `accessKey`.
     * 
     * You don't have to change anything here.
     */
    private void readAccessKey() {
        // Read the desired environment variable.
        accessKey = System.getenv("FIXER_IO_ACCESS_KEY");
        // If that environment variable isn't defined, then
        // `getenv()` returns `null`. We'll throw a (custom)
        // exception if that happens since the program can't
        // really run if we don't have an access key.
        if (accessKey == null) {
            throw new MissingAccessKeyException();
        }
    }

    /**
     * Get the exchange rate for the specified currency against the base currency
     * (the Euro) on the specified date.
     * 
     * @param currencyCode the currency code for the desired currency
     * @param year         the year as a four digit integer
     * @param month        the month as an integer (1=Jan, 12=Dec)
     * @param day          the day of the month as an integer
     * @return the desired exchange rate
     * @throws IOException if there are problems reading from the server
     */
    public float getExchangeRate(String currencyCode, int year, int month, int day) throws IOException {
        /*
         * Here you should:
         * 
         *   - Construct the appropriate URL
         *     - This needs to have the date (properly formatted)
         *       and access key. See the Fixer.io documentation for
         *       the details.
         *   - Open a stream from the URL
         *   - Construct a Tokener from that stream
         *   - Use that to parse the response into a JSON object
         *   - Extract the desired currency code from that JSON object
         *     - Look at the structure of JSON objects returned by Fixer.io.
         *     - You'll need to extract the "rates" (sub)object from the parsed
         *       JSON object.
         *     - You'll need to get the `float` associated with the desired
         *       currency code from the "rates" object. 
         */

	   	 
	String dayString;
	String monthString;

	// Check if day and month are less than 10
	// If they are, then add a 0 in front
	if(day < 10) {
		dayString  = "0" + String.valueOf(day);
	}
	else{
		dayString = String.valueOf(day);
	}
	
	if(month < 10) {
		monthString = "0" + String.valueOf(month);
	}
	else{
		monthString = String.valueOf(month);
	}
	

	// Construct date for URL request
	String date = String.valueOf(year) + "-" + monthString + "-" + dayString;	
	
	// Construct URL request
	String urlRequest = baseURL + date + "?access_key=" + accessKey;

	// Setup URL and JSON materials
	URL url = new URL(urlRequest);
	InputStream inputStream = url.openStream();
	JSONTokener token = new JSONTokener(inputStream);
	JSONObject object = new JSONObject(token);
	
	// Call helper function to get currency rate
	return getRateForCurrency(object, currencyCode);
    }

    public float getRateForCurrency(JSONObject ratesInfo, String currency) {

	    // Get curency rate from JSON object
	    float currencyRate = ratesInfo.getJSONObject("rates").getFloat(currency);
	    return currencyRate;
    } 


    /**
     * Get the exchange rate of the first specified currency against the second on
     * the specified date.
     * 
     * @param fromCurrency the currency code we're exchanging *from*
     * @param toCurrency   the currency code we're exchanging *to*
     * @param year         the year as a four digit integer
     * @param month        the month as an integer (1=Jan, 12=Dec)
     * @param day          the day of the month as an integer
     * @return the desired exchange rate
     * @throws IOException if there are problems reading from the server
     */
    public float getExchangeRate(String fromCurrency, String toCurrency, int year, int month, int day)
            throws IOException {
        /*
         * This is similar to the previous method except that you have to get
         * the two currency rates and divide one by the other to get their
         * relative exchange rate.
         * 
         * DON'T FORGET HOW TO PROGRAM! Extract helper functions to clarify
         * what's going on, and try to avoid duplicate logic between this and
         * the previous method.
         */
        
	// Get the two currecy rates
        float firstCurrency = getExchangeRate(fromCurrency, year, month, day);
	float secondCurrency = getExchangeRate(toCurrency, year, month, day);

	return firstCurrency / secondCurrency;
    }
}

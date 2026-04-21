/**
 *
 *  @author Prokopczuk Tymoteusz s31466
 *
 */

package zad1;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class Service {
    String countryCode;
    String city;
    String localCurrency;
    Weather weather;
    static String otherCurrencyCode;
    Gson gson = new Gson();

    Service(String country){
        for (String code : Locale.getISOCountries()) {
            Locale l = new Locale("", code);
            if (l.getDisplayCountry(Locale.ENGLISH).equalsIgnoreCase(country)) {
                this.countryCode = code;
                Currency currency = Currency.getInstance(l);
                localCurrency = currency.getCurrencyCode();
                break;
            }else
                countryCode = null;
        }
    }

    public String getWeather(String miasto){
        try (HttpClient httpClient = HttpClient.newBuilder().build()){
            HttpRequest geoRequest = HttpRequest.newBuilder().
                    GET()
                    .uri(URI.create("http://api.openweathermap.org/geo/1.0/direct?q="+miasto+","+countryCode+"&appid=efbc081318c72632bc96104cb3ede4af"))
                    .build();
            HttpResponse<String> geoResponse = httpClient.send(geoRequest, HttpResponse.BodyHandlers.ofString());
            GeoLocation geoLocation = gson.fromJson(geoResponse.body().substring(1, geoResponse.body().length()-1), GeoLocation.class);


            HttpRequest weatherRequest = HttpRequest.newBuilder().
                    GET()
                    .uri(URI.create("https://api.openweathermap.org/data/2.5/weather?lat="+geoLocation.lat+"&lon="+geoLocation.lon+"&units=metric&appid=efbc081318c72632bc96104cb3ede4af"))
                    .build();
            HttpResponse<String> weatherResponse = httpClient.send(weatherRequest, HttpResponse.BodyHandlers.ofString());
            weather = gson.fromJson(weatherResponse.body(), Weather.class);
            city=miasto;
            return weatherResponse.body();

        } catch (IOException e) {
            return city=null;
        } catch (Exception e) {
            return city=null;
        }
    }

    public Double getRateFor(String currency){
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            Currency.getInstance(currency.toUpperCase());

            otherCurrencyCode=currency;
            Double rate1 = this.getNBPRate(localCurrency);
            Double rate2 = this.getNBPRate(otherCurrencyCode);
            return rate2/rate1;
        } catch (IllegalArgumentException e){
            return 0.0;
        }
    }

    public Double getNBPRate() {
        if (localCurrency == null) return 0.0;
        if (localCurrency.equals("PLN")) return 1.0;
        Double rate = fetchFromTable("A", localCurrency);

        if (rate == 0.0) {
            rate = fetchFromTable("B", localCurrency);
        }
        return rate;
    }

    public Double getNBPRate(String currency) {
        if (currency == null) return 0.0;
        if (currency.equals("PLN")) return 1.0;
        Double rate = fetchFromTable("A", currency);

        if (rate == 0.0) {
            rate = fetchFromTable("B", currency);
        }
        return rate;
    }
    private Double fetchFromTable(String tableName, String currency) {
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://api.nbp.pl/api/exchangerates/rates/" + tableName + "/" + currency + "/?format=json"))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                NBP nbp = gson.fromJson(response.body(), NBP.class);
                if (nbp != null && nbp.rates != null && !nbp.rates.isEmpty()) {
                    return nbp.rates.get(0).mid;
                }
            }
        } catch (Exception e) {
        }
        return 0.0;
    }
}

class Weather {
    WeatherMain main;

    @Override
    public String toString() {
        return "" + main;
    }
}

class WeatherMain {
    double temp;

    @Override
    public String toString() {
        return ""+temp;
    }
}

class GeoLocation {
    double lat;
    double lon;
}

class Info {
    double rate;
}

class NBP{
    List<Rates> rates;
}
class Rates{
    double mid;
}

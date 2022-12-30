package com.kryvtsun.postcrossing;

import org.json.*;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.util.function.Supplier;

final class NbuRateSupplier implements Supplier<Double> {
    private static final String NBU_API_URL =
            "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?valcode=%s&json";
    private final String currency;

    public NbuRateSupplier(String currency) {
        this.currency = currency;
    }

    @Override
    public Double get() {
        try {
            var request = HttpRequest.newBuilder()
                    .uri(new URI(String.format(NBU_API_URL, currency)))
                    .GET()
                    .build();

            var response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

            JSONArray array = new JSONArray(response.body());
            JSONObject object = array.getJSONObject(0);
            return object.getDouble("rate");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            return Double.NaN;
        }
    }
}

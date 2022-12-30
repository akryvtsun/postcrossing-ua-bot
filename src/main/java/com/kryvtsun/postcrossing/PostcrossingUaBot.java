package com.kryvtsun.postcrossing;

import org.json.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.*;
import java.net.http.*;

public class PostcrossingUaBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(update.getMessage().getChatId().toString());
            message.setText(getUsRate());

            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private String getUsRate() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?valcode=USD&json"))
                    .GET()
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

            JSONArray array = new JSONArray(response.body());
            JSONObject object = array.getJSONObject(0);
            double rate = object.getDouble("rate");
            return String.format("%.2f грн.", rate);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return "PostcrossingUaBot";
    }

    @Override
    public String getBotToken() {
        return System.getenv("token");
    }
}

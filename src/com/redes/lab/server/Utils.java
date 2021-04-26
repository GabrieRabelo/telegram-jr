package com.redes.lab.server;

import java.time.LocalTime;
import java.util.List;

public class Utils {
    public static String logo = "  ____             _                   _      \n" +
            " / ___| _ __  _ __(_)_ __   __ _      | |_ __ \n" +
            " \\___ \\| '_ \\| '__| | '_ \\ / _` |  _  | | '__|\n" +
            "  ___) | |_) | |  | | | | | (_| | | |_| | |   \n" +
            " |____/| .__/|_|  |_|_| |_|\\__, |  \\___/|_|   \n" +
            "       |_|                 |___/              \n";

    public static String[] helloMessages = {
            "Um %s selvagem chegou no servidor.",
            "Digam olá para o(a) %s", "%s bem vindo ao tele!",
            "%s, you shall pass.",
            "O segurança do grupo aprovou %s neste chat."
    };

    public static String getHour() {
        var localTime = LocalTime.now();
        return localTime.getHour() + ":" + localTime.getMinute();
    }

    public static String extractText(String[] splitText) {
        StringBuilder result = new StringBuilder();
        for (int i = 2; i < splitText.length; i++) {
            result.append(splitText[i]);
            if (i + 1 != splitText.length) {
                result.append(" ");
            }
        }
        return result.toString();
    }

    public static String getClientsAsString(List<Client> clients) {
        StringBuilder result = new StringBuilder("Usuários online (" + clients.size() + "): ");

        for (int i = 0; i < clients.size(); i++) {
            result.append(clients.get(i).getName());

            if (i + 1 != clients.size()) {
                result.append(", ");
            } else {
                result.append(".");
            }
        }
        return result.toString();
    }
}

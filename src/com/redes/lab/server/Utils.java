package com.redes.lab.server;

import java.time.LocalTime;

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
}

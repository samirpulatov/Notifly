package org.notifly.services;

import org.notifly.dto.WeatherInfo;

public class WeatherAdviceGenerator {

    public static String generateAdvice(WeatherInfo weatherInfo){
        if(weatherInfo == null){
            return "Не удалось получать данные о погоде \uD83D\uDE14";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("\uD83C\uDF24 Прогноз погоды на сегодняшнее утро выглядит следующим образом: ").append((int) weatherInfo.temp)
                .append("°C, ").append(weatherInfo.description).append(". Ощущается как ").append((int) weatherInfo.feelsLike)
                .append("°C. Минимальная температура - ").append((int)weatherInfo.temp_min)
                .append("°C, а максимальная - ").append((int)weatherInfo.temp_max).append("°С.").append("\n\n");


        if (weatherInfo.temp < -5) {
            sb.append("Сегодня ледяная погода 🥶 — обязательно тёпло одевайтесь. ");
        }
        else if (weatherInfo.temp < 0) {
            sb.append("Довольно прохладно — куртка точно не помешает 😉. ");
        }
        else if (weatherInfo.temp < 10) {
            sb.append("Лёгкая курточка будет в самый раз. ");
        }
        else if (weatherInfo.temp < 20) {
            sb.append("Свежо, но комфортно — свитер или толстовка подойдут 😌. ");
        }
        else {
            sb.append("Тепло и солнечно 🥵 — можно надевать что-то лёгкое. ");
        }

// Дополнения, без повторов "сегодня" или "сейчас"
        if (weatherInfo.description.contains("rain") || weatherInfo.description.contains("дождь")) {
            sb.append("Возможен дождь 🌧 — зонтик лучше захватить. ");
        }

        if (weatherInfo.description.contains("snow") || weatherInfo.description.contains("снег")) {
            sb.append("Может пойти снег ❄ — стоит одеться потеплее. ");
        }

        if (weatherInfo.wind > 6) {
            sb.append("К тому же, ветер довольно заметный 🌬 — ветровка не повредит. ");
        }





        return sb.toString();

    }
}

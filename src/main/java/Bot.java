import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.FileWriter;
import java.time.LocalDateTime;

public class Bot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String updateJson = objectMapper.writeValueAsString(update);

            FileWriter fileWriter = new FileWriter("src/main/resources/update.json");
            fileWriter.write(updateJson);
            fileWriter.close();

            forWorkWithText(update);
            parsingWebPage();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void forWorkWithText(Update update) {
        if (update.hasMessage()) {
            String text = update.getMessage().getText();
            long idUser = update.getMessage().getFrom().getId();

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(idUser);

            if (text.equals("/start")) {
                sendMessage.setText("Данный тг-бот является инф-м пощником");
            } else if (text.equals("/time")) {
                sendMessage.setText(String.valueOf(LocalDateTime.now()));
            }

            try {
                execute(sendMessage);
            } catch (Exception ex) {
                ex.getMessage();
            }
        }
    }

    public void parsingWebPage() {
        try {
            Document document = Jsoup.connect("https://zonasporta.com/category/boks-i-edinaborstva").get();

            Elements elements = document.select("div[style*=width:35%;text-align:right;float:left;]");


            for (Element elementItem : elements) {
                String template = "window.location.href='/product/";
                int leftIndexForName = elementItem.toString().indexOf(template);
                if (leftIndexForName >= 0) {
                    leftIndexForName += template.length();
                    int rightIndexForName = elementItem.toString().indexOf("'", leftIndexForName);
                    if (rightIndexForName >= 0) {
                        String name = elementItem.toString().substring(leftIndexForName, rightIndexForName);
                        System.out.println(name);
                    }
                }
                Elements elementsSpan = elementItem.select("span");
                for (Element elementSpan : elementsSpan) {
                    String span = elementSpan.toString();
                    if (span.contains("₽")) {
                        //TODO Вырезать и распечатать название товара

                        String price = elementSpan.text();
                        System.out.println("❤" + price + "❤");
                    }
                }
            }

            FileWriter fileWriter = new FileWriter("src/main/resources/index.html");
            fileWriter.write(document.toString());
            fileWriter.close();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


    @Override
    public String getBotUsername() {
        return "@ogar_0612_t_tg_bot";
    }


    @Override
    public String getBotToken() {
        return "8255538180:AAHA9gyRGDYDz2tXNhh_VoRi5TXjWrAgeAo";
    }
}

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;

public class Bot extends TelegramLongPollingBot {
    private String urlIndexCalendar = "https://smart-lab.ru/q/shares/";
    private boolean isCommandSellShare = false;
    private StringBuilder builderShares = new StringBuilder();

    @Override
    public void onUpdateReceived(Update update) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String updateJson = objectMapper.writeValueAsString(update);
            FileWriter fileWriter = new FileWriter("src/main/resources/update.json");
            fileWriter.write(updateJson);
            fileWriter.close();
            forWorkWithText(update);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void forWorkWithText(Update update) {
        if (update.hasMessage()) {
            String text = update.getMessage().getText();
            System.out.println(text);
            long userId = update.getMessage().getFrom().getId();

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(userId);

            getListAllHolidays();
            if (text.equals("/hello")) {
                sendMessage.setText("Вас приветствует информационный тг-бот");
            } else if (text.equals("/time")) {
                sendMessage.setText(String.valueOf(LocalDateTime.now()));
            } else if (text.equals("/get_list_all_holidays")) {
                SendDocument sendDocument = new SendDocument();
                sendDocument.setChatId(userId);
                sendDocument.setDocument(new InputFile(new File("src/main/resources/shares.txt")));

                try {
                    execute(sendDocument);
                } catch (Exception ex) {
                    ex.getMessage();
                }
            } else if (text.equals("/input_share_for_sell")) {
                sendMessage.setText("Введите название акции и цену для её продажи(Сбербанк - 500):");
                isCommandSellShare = true;
            } else if (builderShares.toString().contains(text) && isCommandSellShare) {
                String[] arrayNameAndPriceForSellShare = text.split(" - ");
                for (String currentPart : arrayNameAndPriceForSellShare) {
                    System.out.println("😁" + currentPart + "😁");
                }
            }

            try {
                execute(sendMessage);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void getListAllHolidays() {
        StringBuilder builderLinkAllHolidays = new StringBuilder();

        try {
            /*
            TODO Через connect(urlIndexCalendar) подключение,
             а get() получает html этой страницы
             */
            Document documentIndex = Jsoup.connect(urlIndexCalendar).get();
            FileWriter fileWriter = new FileWriter("src/main/resources/index.html");
            fileWriter.write(documentIndex.toString());
            fileWriter.close();

            Elements elementsShares = documentIndex.select("tr");
            for (int i = 1; i < elementsShares.size(); i++) {
                Element elementShare = elementsShares.get(i);
                /*
                TODO
                 Преобразование кода html каждой акции в текст,
                 чтобы из него можно было
                 вырезать название и цену акции
                 */
                String nameShare = elementShare.select(".trades-table__name").text();
                //TODO Сделать вырезку цены акции и вывести название с ней
                String priceShare = elementShare.select(".trades-table__price").text();
                //System.out.println("\uD83E\uDD28" + nameShare + " - " + priceShare + " руб.\uD83E\uDD28");
                builderLinkAllHolidays.append("\uD83E\uDD28" + nameShare + " - " + priceShare + " руб.\uD83E\uDD28\n");
            }
            builderLinkAllHolidays = builderLinkAllHolidays;
            FileWriter fileWriterShares = new FileWriter("src/main/resources/shares.txt");
            fileWriterShares.write(builderLinkAllHolidays.toString());
            fileWriterShares.close();
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

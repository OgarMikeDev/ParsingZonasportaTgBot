import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileWriter;

public class Bot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        try {
            createMapAllItems();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        forWorkWithText(update);
    }

    public void forWorkWithText(Update update) {
        /*
        TODO
         Делаем проверку на наличие сообщения,
         перед тем
         как его обработать
         и на него ответить
         */
        if (update.hasMessage()) {
            /*
            TODO
             Получение id пользователя,
             кот-й написал боту,
             чтобы ответ прислать именно ему
             */
            Long idUser = update.getMessage().getFrom().getId();
            String text = update.getMessage().getText();

            if (text.equals("/get_all_items")) {
                SendDocument sendDocument = new SendDocument();
                sendDocument.setChatId(idUser);
                sendDocument.setDocument(new InputFile(new File("src/main/resources/mapAllItems.txt")));

                //TODO try и catch нужен, чтобы в случае внешней ошибки, программа не остановилась
                try {
                    execute(sendDocument);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

    public void createMapAllItems() {
        //TODO Получение html-кода веб-страницы для парсинга
        try {
            Document document = Jsoup.connect("https://zonasporta.com/category/nastolnyj-tennis").get();
            //TODO Разделели блоки кода с одинаковой структурой(товары)
            Elements elementsItems = document.select("div[style*=width:35%;text-align:right;float:left;]");
            StringBuilder builderItems = new StringBuilder();
            for (Element elementItem : elementsItems) {
                //TODO Разделили все строки span, включая ту, кот-я содержит цену товара
                Elements elementsSpans = elementItem.select("span");
                for (Element elementSpan : elementsSpans) {
                    if (elementSpan.toString().contains("₽")) {
                        String price = elementSpan.text();
                        builderItems.append("🤷‍♂️" + price + "🤷‍♂️\n");
                    }
                }
            }

            FileWriter fileWriter = new FileWriter("src/main/resources/nastolnyj-tennis.html");
            fileWriter.write(document.toString());
            fileWriter.close();

            FileWriter fileWriterMapAllItems = new FileWriter("src/main/resources/mapAllItems.txt");
            fileWriterMapAllItems.write(builderItems.toString());
            fileWriterMapAllItems.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return "@zona_sporta_ogar_tg_bot";
    }

    @Override
    public String getBotToken() {
        return "8563060361:AAHg51KahSEct_XXF3GTRlRgmbS_YLcgCD8";
    }
}

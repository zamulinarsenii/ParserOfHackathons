package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Objects;

public class Parser {
    private Document page;
    private final Elements events;
    private final String url,fileRoad;


    public Parser(String fileRoad) throws InterruptedException, FileNotFoundException {
        this.fileRoad=fileRoad;
        url = "https://leader-id.ru";
        getPage();
        Element table = page.select("div[class=_3if2AyrCFNVz]").first();
        assert table != null;
        events = table.select("div[data-qa=event]");
        getInfo();
    }

    private void getInfo() throws FileNotFoundException {
        ArrayList<String> info = new ArrayList<>();
        PrintStream file = new PrintStream(fileRoad);
        int i = 0;
        for (Element event : events) {
            info.add("№"+(i+1)+"\n"+event.select("h4").text() + "\n" +
                    event.select("div[class=app-card-event__alert]").text() + "\n" +
                    event.select("div[class=app-card-event__date]").text() + "\n" +
                    event.select("div[class=app-card-event__location]").text() + "\n");
            file.println(info.get(i));
            i++;
        }
    }
    private void getPage() throws InterruptedException {
        WebDriver webDriver = new ChromeDriver();
        webDriver.get(url);
        WebElement resetLocation = webDriver.findElement(By.xpath("//div[@class='header__top-action__item']"));
        resetLocation.click();
        WebElement email = webDriver.findElement(By.xpath("//input[@class='app-input__inner']"));
        email.sendKeys("хакатон\n");
        Thread.sleep(3000);
        WebElement nextBtn = webDriver.findElement(By.xpath("//button[@class='app-button load-more__btn app-button--default app-button--xl']"));
        page = Jsoup.parse(webDriver.getPageSource());
        String[] value = Objects.requireNonNull(page.select("p[class=load-more__display]").first()).text().split(" ");
        int first=0;
        int second = 0;
        try{
            first = Integer.parseInt(value[1]);
        }
        catch (NumberFormatException ex){
            ex.printStackTrace();
        }
        try{
            second = Integer.parseInt(value[4].substring(0, value[4].length()-1));
        }
        catch (NumberFormatException ex){
            ex.printStackTrace();
        }
        while(first < second) {
            nextBtn.click();
            Thread.sleep(500);
            page = Jsoup.parse(webDriver.getPageSource());
            value = Objects.requireNonNull(page.select("p[class=load-more__display]").first()).text().split(" ");
            try{
                first = Integer.parseInt(value[1]);
            }
            catch (NumberFormatException ex){
                ex.printStackTrace();
            }
        }
        webDriver.quit();
    }
}

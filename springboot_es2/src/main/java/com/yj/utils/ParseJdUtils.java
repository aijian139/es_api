package com.yj.utils;

import com.yj.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class ParseJdUtils {

    public static void main(String[] args) throws Exception {
        new ParseJdUtils().parseJd("java");

    }


    public List<Content> parseJd(String keyword) throws Exception {
        //https://search.jd.com/Search?keyword=java&enc=utf-8
        String url = "https://search.jd.com/Search?keyword="+keyword+"enc=utf-8";
        Document document = Jsoup.parse(new URL(url), 50000);

        Element j_goodsList = document.getElementById("J_goodsList");

        //System.out.println(j_goodsList.html());

        Elements elements = j_goodsList.getElementsByTag("li");

       List<Content> contents = new ArrayList<>();
        for (Element element : elements) {
            String img = element.getElementsByTag("img").attr("data-lazy-img");
           // System.out.println(img);
            String price = element.getElementsByClass("p-price").text();
           // System.out.println(price);
            String name = element.getElementsByClass("p-name").text();
           // System.out.println(name);

            Content content = new Content().setImg(img).setName(name).setPrice(price);

            contents.add(content);
        }
        return contents;
    }

}

package com.yj.utils;

import com.yj.entity.Content;
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
public class ParseHtmlUtils {

//    public static void main(String[] args) throws Exception {
//        JsoupUtils.parseJd("java").forEach(content -> {
//            System.out.println(content);
//        });
//    }


    public List<Content> parseJd(String keyword) throws Exception {
        // https://search.jd.com/Search?keyword=java
        String url = "https://search.jd.com/Search?keyword="+keyword+"&enc=utf-8";
        Document document = Jsoup.parse(new URL(url),50000);
        //
        Element j_goodsList = document.getElementById("J_goodsList");
        //System.out.println(j_goodsList);
        // 获取到所有的li 标签
        Elements elements = j_goodsList.getElementsByTag("li");

        //System.out.println(elements.html());
        List<Content> goodList = new ArrayList<>();
        for (Element el:elements
        ) {
            String srcImg = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String name = el.getElementsByClass("p-name").eq(0).text();

            //System.out.println("===================================");
            // 将获取的字段 封装成对象属性
           // System.out.println(srcImg);
           // System.out.println(price);
           // System.out.println(name);
            Content content = new Content().setName(name).setPrice(price).setImg(srcImg);
            goodList.add(content);
        }

        return goodList;
    }
}

package com.playstationbot;

import java.io.IOException;
import java.util.*;

import org.apache.commons.collections4.OrderedMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BlogParser {
    public static List<BlogPost> getBlogPosts(String urlToWatch) {
        List<BlogPost> results = new ArrayList<BlogPost>();

        try {
            Document doc = Jsoup.connect(urlToWatch).get();
            //System.out.println("Page Title: " + doc.title());

            Elements articles = doc.select("article");

            for (Element article : articles) {
                Elements link = article.select("a");

                //System.out.println("Article ID " + article.id() + "; Link: " + link.attr("href"));
                if (link.attr("href") == "")
                    continue;


                results.add(new BlogPost(article.id(), link.attr("href")));
            }
        } catch (IOException e) {
            // TODO Auto-generatksed catch block
            e.printStackTrace();
        }

        return results;
    }
}

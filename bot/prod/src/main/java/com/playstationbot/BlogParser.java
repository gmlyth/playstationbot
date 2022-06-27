package com.playstationbot;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

                BlogPost post = new BlogPost(article.id(), link.attr("href"));

                if(BlogCache.needToPost(post)) {
                    getBlogDetails(post);

                    BlogCache.addPost(post);

                    results.add(post);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generatksed catch block
            e.printStackTrace();
        }

        return results;
    }

    private static void getBlogDetails(BlogPost post) {
        try {
            Document doc = Jsoup.connect(post.getPostLink()).get();

            Elements metas = doc.select("meta");

            for(Element meta : metas) {
                if(meta.attr("name").equals("og:title")) {
                    post.setTitle(meta.attr("content"));
                }
                else if(meta.attr("name").equals("og:description")) {
                    post.setDescription(meta.attr("content"));
                }
                else if(meta.attr("name").equals("article:tag")) {
                    post.addTag(meta.attr("content"));
                }
                else if(meta.attr("name").equals("article:published_time")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                    try {
                        //Strip off the timezone. I really don't care.
                        char[] dateChars = meta.attr("content").toCharArray();
                        char[] newChars = new char[dateChars.length - 6];

                        for(int i = 0; i < newChars.length; i++)
                            newChars[i] = dateChars[i];

                        Date date = sdf.parse(String.valueOf(newChars));
                        post.setPublishedTime(date);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generatksed catch block
            e.printStackTrace();
        }
    }
}

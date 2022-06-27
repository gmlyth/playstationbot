package com.playstationbot;

import java.util.*;

import javax.security.auth.login.LoginException;

import org.apache.commons.collections4.OrderedMap;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PlaystationBot extends ListenerAdapter {
    private static JDA jda = null;
    
    public static void BuildPlaystationBot() {
        //String token = System.getenv("PLAYSTATIONBOT_TOKEN");

        String token = SsmUtility.getSsmParameter("playstationbot-token");

        try {
            jda = JDABuilder.createDefault(token) // The token of the account that is logging in.
                    .addEventListeners(new PlaystationBot())   // An instance of a class that will handle events.
                    .build();
            jda.awaitReady(); // Blocking guarantees that JDA will be completely loaded.
        } catch (LoginException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        while(true) {
            checkForPosts();

            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void checkForPosts() {
        List<BlogPost> posts = BlogParser.getBlogPosts("https://blog.playstation.com");

        for(BlogPost post : posts) {
            if(BlogCache.needToPost(post)) {
                postArticle(post);
            }
        }
        System.out.println("Got here");
    }

    private static void postArticle(BlogPost post) {
        for(Guild guild : jda.getGuilds()) {
            try
            {
                TextChannel channel = guild.getTextChannelsByName("general", true).get(0);
                channel.sendMessage(post.getPostLink()).queue();
            }
            catch (Exception ex) {
                    System.out.println(ex.getStackTrace());
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        Message msg = event.getMessage();
        if (msg.getContentRaw().equals("!ping"))
        {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Pong!"); /* => RestAction<Message> */
        }
    }
}

package com.playstationbot;

import java.util.*;

import javax.security.auth.login.LoginException;

import org.apache.commons.collections4.OrderedMap;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class PlaystationBot extends ListenerAdapter {
    private static JDA jda = null;
    
    public static void BuildPlaystationBot() {
        //String token = System.getenv("PLAYSTATIONBOT_TOKEN");

        System.out.println("hello!!!");
        System.out.println(System.getenv("AWS_ACCESS_KEY_ID"));

        String token = SsmUtility.getSsmParameter("playstationbot-token");

        try {
            jda = JDABuilder.createDefault(token) // The token of the account that is logging in.
                    .addEventListeners(new PlaystationBot())   // An instance of a class that will handle events.
                    .build();

                    List<SlashCommandData> commands = new ArrayList<SlashCommandData>();
                    SlashCommandData commandData = Commands.slash("setchannel", "Set the channel for the bot to post in");
                    commandData.addOption(OptionType.STRING, "channelname", "The name of the channel (no # please)");
                    commands.add(commandData);

                    commandData = Commands.slash("getchannel", "View the channel the bot posts in");
                    commands.add(commandData);

                    commandData = Commands.slash("gettags", "View the list of available tags");
                    commands.add(commandData);

                    for(SlashCommandData command : commands) {
                        jda.upsertCommand(command).queue(); // This can take up to 1 hour to show up in the client
                    }

            SettingCache.initializeFromDynamoDb();
            BlogCache.initializeFromDynamoDb();
            
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
            postArticle(post);
        }
    }

    private static void postArticle(BlogPost post) {
        for(Guild guild : jda.getGuilds()) {
            try
            {
                TextChannel channel = guild.getTextChannelsByName(SettingCache.getSetting(guild.getId(), "channelname"), true).get(0);
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
        // if (msg.getContentRaw().equals("!ping"))
        // {
        //     MessageChannel channel = event.getChannel();
        //     channel.sendMessage("Pong!"); /* => RestAction<Message> */
        // }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        if(event.getName().equals("setchannel")) {
            String channelName = event.getInteraction().getOption("channelname").getAsString();
            SettingCache.insertSetting(event.getGuild().getId(), "channelname", channelName, true);
            event.getChannel().sendMessage("PlaystationBot will now post in #" + channelName).queue();
        }
        else if (event.getName().equals("getchannel")) {
            event.getChannel().sendMessage(SettingCache.getSetting(event.getGuild().getId(), "channelname")).queue();
        }
        else if (event.getName().equals("gettags")) {
            event.getChannel().sendMessage(TagCache.getTagList()).queue();
        }
    }
}

package com.playstationbot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

public class DynamoDbUtility {
    public static List<BlogPost> getBlogPostItems() {
        List<BlogPost> results = new ArrayList<BlogPost>();

        Region region = Region.US_EAST_2;
        DynamoDbClient client = DynamoDbClient.builder()
            .region(region)
            //IMPORTANT! Unloke boto3, the java sdk needs a specific "credentials provider"
            .credentialsProvider(DefaultCredentialsProvider.create()) // use when debugging in VSCODE.
            //.credentialsprovider(ContainerCredentialsProvider.create()) //use when running on FARGATE.
            //.credentialsProvider(EnvironmentVariableCredentialsProvider.create()) //use for testing docker locally.
            .build();

        ScanRequest scanRequest = ScanRequest.builder().tableName("PlaystationBotBlogPost").build();

        ScanResponse result = client.scan(scanRequest);
        for (Map<String, AttributeValue> item : result.items()){
            AttributeValue postIdVal = item.get("post_id");
            AttributeValue publishedTimeVal = item.get("published");
            AttributeValue titleVal = item.get("title");
            AttributeValue descriptionVal = item.get("description");
            AttributeValue postLinkVal = item.get("post_link");

            BlogPost post = new BlogPost(postIdVal.n(), postLinkVal.s());
            post.setTitle(titleVal.s());
            post.setDescription(descriptionVal.s());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            try {
                post.setPublishedTime(sdf.parse(publishedTimeVal.s()));
            } catch (ParseException e) {
                continue;
            }
            results.add(post);
        }
        
        return results;
    }

    public static HashMap<String,HashMap<String, String>> getSettingItems() {
        HashMap<String,HashMap<String, String>>results = new HashMap<String,HashMap<String, String>>();

        Region region = Region.US_EAST_2;
        DynamoDbClient client = DynamoDbClient.builder()
            .region(region)
            //IMPORTANT! Unloke boto3, the java sdk needs a specific "credentials provider"
            .credentialsProvider(DefaultCredentialsProvider.create()) // use when debugging in VSCODE.
            //.credentialsprovider(ContainerCredentialsProvider.create()) //use when running on FARGATE.
            //.credentialsProvider(EnvironmentVariableCredentialsProvider.create()) //use for testing docker locally.
            .build();

        ScanRequest scanRequest = ScanRequest.builder().tableName("PlaystationBotGuildSetting").build();

        ScanResponse result = client.scan(scanRequest);
        for (Map<String, AttributeValue> item : result.items()){
            AttributeValue guildIdVal = item.get("guild_id");
            AttributeValue settingKeyVal = item.get("setting_key");
            AttributeValue settingValueVal = item.get("setting_value");
            
            if(results.containsKey(guildIdVal.n()) == false) {
                results.put(guildIdVal.n(), new HashMap<String, String>());
            }
            results.get(guildIdVal.n()).put(settingKeyVal.s(), settingValueVal.s());
        }
        
        return results;
    }

    public static void upsertSetting(String guildId, String settingKey, String settingValue) {
        Region region = Region.US_EAST_2;
        DynamoDbClient client = DynamoDbClient.builder()
                .region(region)
                // IMPORTANT! Unloke boto3, the java sdk needs a specific "credentials provider"
                .credentialsProvider(DefaultCredentialsProvider.create()) // use when debugging in VSCODE.
                // .credentialsprovider(ContainerCredentialsProvider.create()) //use when
                // running on FARGATE.
                // .credentialsProvider(EnvironmentVariableCredentialsProvider.create()) //use
                // for testing docker locally.
                .build();

        HashMap<String, AttributeValue> keyMap = new HashMap<String, AttributeValue>();

        keyMap.put("guild_id", AttributeValue.builder().n(guildId).build());
        keyMap.put("setting_key", AttributeValue.builder().s(settingKey).build());

        HashMap<String, AttributeValueUpdate> attributeMap = new HashMap<String, AttributeValueUpdate>();

        attributeMap.put("setting_value", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(settingValue).build())
                .action(AttributeAction.PUT)
                .build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName("PlaystationBotGuildSetting")
                .key(keyMap)
                .attributeUpdates(attributeMap)
                .build();

        client.updateItem(request); //Upsert
    }

    public static void upsertBlogPost(BlogPost post) {
        Region region = Region.US_EAST_2;
        DynamoDbClient client = DynamoDbClient.builder()
                .region(region)
                // IMPORTANT! Unloke boto3, the java sdk needs a specific "credentials provider"
                .credentialsProvider(DefaultCredentialsProvider.create()) // use when debugging in VSCODE.
                // .credentialsprovider(ContainerCredentialsProvider.create()) //use when
                // running on FARGATE.
                // .credentialsProvider(EnvironmentVariableCredentialsProvider.create()) //use
                // for testing docker locally.
                .build();

        HashMap<String, AttributeValue> keyMap = new HashMap<String, AttributeValue>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        String publishedString = sdf.format(post.getPublishedTime());

        keyMap.put("post_id", AttributeValue.builder().n(String.valueOf(post.getPostId())).build());
        keyMap.put("published", AttributeValue.builder().s(publishedString).build());

        HashMap<String, AttributeValueUpdate> attributeMap = new HashMap<String, AttributeValueUpdate>();

        attributeMap.put("title", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(post.getTitle()).build())
                .action(AttributeAction.PUT)
                .build());
        attributeMap.put("description", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(post.getDescription()).build())
                .action(AttributeAction.PUT)
                .build());
        attributeMap.put("post_link", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(post.getPostLink()).build())
                .action(AttributeAction.PUT)
                .build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName("PlaystationBotBlogPost")
                .key(keyMap)
                .attributeUpdates(attributeMap)
                .build();

        client.updateItem(request); //Upsert
    }
}
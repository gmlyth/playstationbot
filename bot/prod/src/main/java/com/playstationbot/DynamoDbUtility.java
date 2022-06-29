package com.playstationbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public List<BlogPost> getBlogPostItems() {
        List<BlogPost> results = new ArrayList<BlogPost>();

        Region region = Region.US_EAST_2;
        DynamoDbClient client = DynamoDbClient.builder()
            .region(region)
            //IMPORTANT! Unloke boto3, the java sdk needs a specific "credentials provider"
            .credentialsProvider(DefaultCredentialsProvider.create()) // use when debugging in VSCODE.
            //.credentialsprovider(ContainerCredentialsProvider.create()) //use when running on FARGATE.
            //.credentialsProvider(EnvironmentVariableCredentialsProvider.create()) //use for testing docker locally.
            .build();

        ScanRequest scanRequest = ScanRequest.builder().tableName("BlogPost").build();

        ScanResponse result = client.scan(scanRequest);
        for (Map<String, AttributeValue> item : result.items()){
            
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

        client.updateItem(request); // HOLY FUCK
    }
}
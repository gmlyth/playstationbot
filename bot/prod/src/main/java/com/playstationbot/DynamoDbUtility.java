package com.playstationbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

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
}
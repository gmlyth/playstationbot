package com.playstationbot;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ContainerCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.SsmException;

public class SsmUtility {
    public static String getSsmParameter(String parameterName) {
        String result = null;

        Region region = Region.US_EAST_2;
        SsmClient ssmClient = SsmClient.builder()
                .region(region)
                //IMPORTANT! Unloke boto3, the java sdk needs a specific "credentials provider"
                .credentialsProvider(DefaultCredentialsProvider.create()) // use when debugging in VSCODE.
                //.credentialsprovider(ContainerCredentialsProvider.create()) //use when running on FARGATE.
                //.credentialsProvider(EnvironmentVariableCredentialsProvider.create()) //use for testing docker locally.
                .build();

        try {
            GetParameterRequest parameterRequest = GetParameterRequest.builder()
                .name(parameterName)
                .build();

            GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);
            result = parameterResponse.parameter().value();
        }
        catch (SsmException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return result;
    }
}

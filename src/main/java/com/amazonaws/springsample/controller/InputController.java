package com.amazonaws.springsample.controller;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.xray.AWSXRay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;

@RestController
public class InputController {
    private static final Logger logger = LoggerFactory.getLogger(InputController.class);

    @RequestMapping("/")
    public String home() {
        System.out.println("Hello home!");
        return "Hello Docker World";
    }

    @RequestMapping("/aws-sdk-v1")
    public String awsSdkV1() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-west-2")
                .build();


        ListTablesResult res = client.listTables();
        for (String table : res.getTableNames()) {
            System.out.println(table);
        }

        return "listed tables!";
    }

    @RequestMapping("/aws-sdk-v2")
    public String awsSdkV2() {
        DynamoDbClient client = DynamoDbClient.builder()
                .region(Region.US_WEST_2)
                .build();

        ListTablesResponse res = client.listTables();
        for (String table : res.tableNames()) {
            System.out.println(table);
        }

        return "listed tables!";
    }

    @RequestMapping("/exception")
    public String exception() {
        AWSXRay.createSubsegment("good function", () -> {
            AWSXRay.beginSubsegment("first");
            AWSXRay.endSubsegment();
            AWSXRay.createSubsegment("bad function", () -> {
                int res = 10 / 0;
            });
        });

        return "hello world";
    }
}

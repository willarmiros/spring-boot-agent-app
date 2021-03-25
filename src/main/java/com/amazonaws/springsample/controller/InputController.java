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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@RestController
public class InputController {
    private static final Logger logger = LoggerFactory.getLogger(InputController.class);
    private static final String TABLE_NAME = "employees";
    private static final String[] NAMES = {"alice", "bob", "charlie"};

    private Connection conn;
    private PreparedStatement insertStmt;
    private PreparedStatement queryStmt;

    public InputController() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");  // Load driver
            String connUrl = "jdbc:oracle:thin:@" + System.getenv("ORACLE_DB_URL") + ":1521:" + System.getenv("ORACLE_DB_NAME");
            conn = DriverManager.getConnection(connUrl, System.getenv("ORACLE_DB_USER"), System.getenv("ORACLE_DB_PWD"));
            insertStmt = conn.prepareStatement("insert into " + TABLE_NAME + " values(?, ?, ?)");
            queryStmt = conn.prepareStatement("select * from " + TABLE_NAME + " where id=?");
        } catch (Exception e) {
            logger.error("Failed to connect to Oracle DB", e);
        }
    }

    @RequestMapping("/")
    public String home() {
        logger.info("Hello home!");
        return "Hello Docker World";
    }

    @RequestMapping("/aws-sdk-v1")
    public String awsSdkV1() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-west-2")
                .build();


        ListTablesResult res = client.listTables();
        for (String table : res.getTableNames()) {
            logger.info(table);
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
            logger.info(table);
        }

        return "listed tables!";
    }

    @RequestMapping("/create-table")
    public String createTable() {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeQuery("create table " + TABLE_NAME + "(id number(10),name varchar2(40),age number(3))");
        } catch (SQLException e) {
            return "Encountered exception: \n" + e.toString();
        }

        return "Created table " + TABLE_NAME;
    }

    @RequestMapping("list-tables")
    public String listTables() {
        String ret = "";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select table_name from user_tables");
            while (rs.next()) {
                ret += rs.getString(1) + "\n";
            }
        } catch (SQLException e) {
            return "Encountered exception: \n" + e.toString();
        }

        return ret;
    }

    @RequestMapping("/insert-employees")
    public String insertEmployees() throws SQLException {
        for (int i = 0; i < NAMES.length; i++) {
            insertStmt.setInt(1, i);
            insertStmt.setString(2, NAMES[i]);
            insertStmt.setInt(3, (i+1) * 10);
            insertStmt.execute();
        }

        return "Inserted employees into table " + TABLE_NAME;
    }

    @RequestMapping("/query-table")
    public String queryTable() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from " + TABLE_NAME);

        logger.info("Queried from " + TABLE_NAME);
        StringBuilder sb = new StringBuilder();
        while(rs.next()) {
            sb.append(String.format("employee ID: %d;   name: %s;   age: %d\n",
                    rs.getInt("id"), rs.getString("name"), rs.getInt("age")));
        }

        return "Employees:\n" + sb.toString();
    }

    @RequestMapping("/query-employee")
    public String queryEmployee() throws SQLException {
        queryStmt.setInt(1, 0);
        queryStmt.execute();
        ResultSet rs = queryStmt.getResultSet();
        StringBuilder sb = new StringBuilder();
        while(rs.next()) {
            sb.append(String.format("employee name: %s\n; age: %d", rs.getString("name"), rs.getInt("age")));
        }

        return "Employees:\n" + sb.toString();
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

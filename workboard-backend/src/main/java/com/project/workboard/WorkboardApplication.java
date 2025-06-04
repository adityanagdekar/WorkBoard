package com.project.workboard;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class WorkboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkboardApplication.class, args);
	}
	
	@Autowired
    private DataSource dataSource;

    @PostConstruct
    public void logDbInfo() throws SQLException {
        System.out.println("DB Connection: " + dataSource.getConnection().getMetaData().getURL());
    }

}

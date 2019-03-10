package org.unifi.ft.rehearsal.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;

@Configuration
@EnableMongoRepositories
public class MongoConfiguration extends AbstractMongoConfiguration {

	@Override
	public MongoClient mongoClient() {
		return new MongoClient("localhost");
	}

	@Override
	protected String getDatabaseName() {
		return "rehearsal-repository";
	}
    
}
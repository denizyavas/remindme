package com.deniz.remindme

import com.gmongo.GMongoClient
import com.mongodb.ServerAddress
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

@EnableAutoConfiguration
@ComponentScan(value = "com.deniz")
@PropertySource(ignoreResourceNotFound = true, value = ['classpath:/default.properties', 'classpath:/${environment}.properties'])
class RatpackSpringConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        new PropertySourcesPlaceholderConfigurer()
    }

    @Bean
    @Qualifier("mongoClient")
    @org.springframework.context.annotation.Lazy
    public static GMongoClient mongoClient(
            @Value('${mongo.db.host}') def host,
            @Value('${mongo.db.name}') def name,
            @Value('${mongo.db.user}') def user,
            @Value('${mongo.db.password}') def password
    ) {
        new GMongoClient(new ServerAddress(host))
    }

}

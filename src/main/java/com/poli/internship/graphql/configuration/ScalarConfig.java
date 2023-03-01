package com.poli.internship.graphql.configuration;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class ScalarConfig {
    @Bean
    public RuntimeWiringConfigurer date() {
        return wiringBuilder -> wiringBuilder.scalar(ExtendedScalars.Date);
    }
}

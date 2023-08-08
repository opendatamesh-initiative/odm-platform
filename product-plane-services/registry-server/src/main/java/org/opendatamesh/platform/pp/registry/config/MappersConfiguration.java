package org.opendatamesh.platform.pp.registry.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.LifecycleActivityInfoDPDS;

import org.opendatamesh.platform.core.dpds.model.LifecycleInfoDPDS;
import org.opendatamesh.platform.core.dpds.serde.ActivityInfoDeserializer;

import org.opendatamesh.platform.core.dpds.serde.LifecycleInfoDeserializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;

@Configuration
public class MappersConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;

        /*
         * objectMapper.setSerializationInclusion(Include.NON_EMPTY);
         * 
         * SimpleModule module = new SimpleModule();
         * module.addDeserializer(LifecycleActivityInfoDPDS.class, new
         * ActivityInfoDeserializer());
         * module.addDeserializer(LifecycleInfoDPDS.class, new
         * LifecycleInfoDeserializer());
         * 
         * objectMapper.registerModule(module);
         */
        return objectMapper;
    }

   
    @Bean
    public HttpMessageConverter<Object> createXmlHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(ObjectMapperFactory.JSON_MAPPER);
        return converter;
    }
}

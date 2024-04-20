package io.github.jamienlu.discorridor.spring.boot.autoconfigure;

import io.github.jamienlu.discorridor.spring.boot.autoconfigure.process.HttpMessageConvert;
import io.github.jamienlu.discorridor.serialization.api.SerializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-04-16
 */
@Configuration
@AutoConfigureAfter(DiscorridorAutoConfigure.class)
public class WebConfig extends WebMvcConfigurationSupport {
    @Autowired
    private SerializationService serializationService;
    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(httpMessageConvert());
    }
    @Bean
    public HttpMessageConvert httpMessageConvert() {
        return new HttpMessageConvert(serializationService);
    }
}

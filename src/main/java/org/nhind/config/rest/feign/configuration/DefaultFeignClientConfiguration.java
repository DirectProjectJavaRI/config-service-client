package org.nhind.config.rest.feign.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;

@Configuration
public class DefaultFeignClientConfiguration
{
	
	@Bean
	public ErrorDecoder configServiceFeignClientErrorDecoder()
	{
		return new ConfigServicesErrorDecoder();
	}
	
}

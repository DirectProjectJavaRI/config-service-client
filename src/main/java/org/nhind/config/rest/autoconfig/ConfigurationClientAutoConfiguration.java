package org.nhind.config.rest.autoconfig;

import org.nhind.config.rest.AddressService;
import org.nhind.config.rest.AnchorService;
import org.nhind.config.rest.CertPolicyService;
import org.nhind.config.rest.CertificateService;
import org.nhind.config.rest.DNSService;
import org.nhind.config.rest.DomainService;
import org.nhind.config.rest.SettingService;
import org.nhind.config.rest.TrustBundleService;
import org.nhind.config.rest.exchange.AddressClient;
import org.nhind.config.rest.exchange.AnchorClient;
import org.nhind.config.rest.exchange.CertificateClient;
import org.nhind.config.rest.exchange.CertificatePolicyClient;
import org.nhind.config.rest.exchange.DNSClient;
import org.nhind.config.rest.exchange.DomainClient;
import org.nhind.config.rest.exchange.SettingClient;
import org.nhind.config.rest.exchange.TrustBundleClient;
import org.nhind.config.rest.impl.DefaultAddressService;
import org.nhind.config.rest.impl.DefaultAnchorService;
import org.nhind.config.rest.impl.DefaultCertPolicyService;
import org.nhind.config.rest.impl.DefaultCertificateService;
import org.nhind.config.rest.impl.DefaultDNSService;
import org.nhind.config.rest.impl.DefaultDomainService;
import org.nhind.config.rest.impl.DefaultSettingService;
import org.nhind.config.rest.impl.DefaultTrustBundleService;
import org.nhindirect.common.rest.exchange.DirectRestClientBuilderConfig;
import org.nhindirect.common.rest.exchange.DirectWebClientBuilderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@AutoConfiguration
@ConditionalOnClass(WebClient.class )
@ConditionalOnProperty(name="direct.config.service.url")
@Import(DirectRestClientBuilderConfig.class)
public class ConfigurationClientAutoConfiguration {

	@Value("${direct.config.service.url}")
	protected String serviceIdentifier;
	
	protected HttpServiceProxyFactory configServiceProxyFactory(RestClient.Builder restClientBuilder) {
		
		final var client = restClientBuilder.baseUrl(serviceIdentifier).build();
		return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client)).build();
		
	}
	
	@ConditionalOnMissingBean
	@Bean
	AddressClient directAddressClient(RestClient.Builder restClientBuilder) {
		
		return configServiceProxyFactory(restClientBuilder).createClient(AddressClient.class);
	}
	
	@ConditionalOnMissingBean
	@Bean
	AddressService directAddressService(AddressClient addressClient) {
		
		return new DefaultAddressService(addressClient);
	}
	
	
	@ConditionalOnMissingBean
	@Bean
	AnchorClient directAnchorClient(RestClient.Builder restClientBuilder) {
		
		return configServiceProxyFactory(restClientBuilder).createClient(AnchorClient.class);
	}
	
	@ConditionalOnMissingBean
	@Bean
	AnchorService directAnchorService(AnchorClient anchorClient) {
		
		return new DefaultAnchorService(anchorClient);
	}
	
	@ConditionalOnMissingBean
	@Bean
	CertificateClient directCertificateClient(RestClient.Builder restClientBuilder) {
		
		return configServiceProxyFactory(restClientBuilder).createClient(CertificateClient.class);
	}
	
	@ConditionalOnMissingBean
	@Bean
	CertificateService directCertificateService(CertificateClient certClient) {
		
		return new DefaultCertificateService(certClient);
	};
	
	@ConditionalOnMissingBean
	@Bean
	CertificatePolicyClient directCertificatePolicyClient(RestClient.Builder webClientBuilder) {
		
		return configServiceProxyFactory(webClientBuilder).createClient(CertificatePolicyClient.class);
	}
	
	@ConditionalOnMissingBean
	@Bean
	CertPolicyService directDertificatePolicyService(CertificatePolicyClient certPolicyClient) {
		
		return new DefaultCertPolicyService(certPolicyClient);
	}
	
	@ConditionalOnMissingBean
	@Bean
	DNSClient directDnsClient(RestClient.Builder webClientBuilder) {
		
		return configServiceProxyFactory(webClientBuilder).createClient(DNSClient.class);
	}
	
	@ConditionalOnMissingBean
	@Bean
	DNSService directDnsService(DNSClient dnsClient) {
		
		return new DefaultDNSService(dnsClient);
	}
	
	@ConditionalOnMissingBean
	@Bean
	DomainClient directDomainClient(RestClient.Builder webClientBuilder) {
		
		return configServiceProxyFactory(webClientBuilder).createClient(DomainClient.class);
	}
	
	@ConditionalOnMissingBean
	@Bean
	DomainService directDomainService(DomainClient domainClient) {
		
		return new DefaultDomainService(domainClient);
	}
	
	@ConditionalOnMissingBean
	@Bean
	SettingClient directSettingClient(RestClient.Builder restClientBuilder) {
		
		return configServiceProxyFactory(restClientBuilder).createClient(SettingClient.class);
	}
	
	@ConditionalOnMissingBean
	@Bean
	SettingService defaultSettingService(SettingClient settingsClient) {
		
		return new DefaultSettingService(settingsClient);
	}
	
	@ConditionalOnMissingBean
	@Bean
	TrustBundleClient directTrustBundleClient(RestClient.Builder restClientBuilder) {
		
		return configServiceProxyFactory(restClientBuilder).createClient(TrustBundleClient.class);
	}
	
	@ConditionalOnMissingBean
	@Bean
	TrustBundleService directTrustBundleService(TrustBundleClient trustBundleClient) {
		
		return new DefaultTrustBundleService(trustBundleClient);
	}
}

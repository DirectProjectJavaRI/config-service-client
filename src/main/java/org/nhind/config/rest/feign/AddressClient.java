package org.nhind.config.rest.feign;

import java.util.Collection;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.feign.DefaultFeignClientConfiguration;
import org.nhindirect.config.model.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="direct-config-service", url = "${direct.config.service.url}", configuration=DefaultFeignClientConfiguration.class)
public interface AddressClient
{
	@GetMapping("/address/{address}")
	public Address getAddress(@PathVariable("address") String address) throws ServiceException;
	
    @GetMapping("/address/domain/{domainName}")     
    public Collection<Address> getAddressesByDomain(@PathVariable("domainName") String domainName) throws ServiceException;
    
    @PutMapping(value="/address", consumes = MediaType.APPLICATION_JSON_VALUE)   
    public ResponseEntity<Void> addAddress(@RequestBody Address address) throws ServiceException;
    
    @PostMapping(value="/address", consumes = MediaType.APPLICATION_JSON_VALUE)     
    public void updateAddress(@RequestBody Address address) throws ServiceException;
    
    @DeleteMapping(value="address/{address}", consumes = MediaType.APPLICATION_JSON_VALUE)    
    public ResponseEntity<Void> removeAddress(@PathVariable("address") String address) throws ServiceException; 
}

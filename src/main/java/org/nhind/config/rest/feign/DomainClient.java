package org.nhind.config.rest.feign;

import java.util.Collection;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Domain;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="direct-config-service", url = "${direct.config.service.url}")
public interface DomainClient
{
    @GetMapping("/domain/{domain}")
    public Domain getDomain(@PathVariable("domain") String domain) throws ServiceException;
    
    @GetMapping("/domain")
    public Collection<Domain> searchDomains(@RequestParam(name="domainName", defaultValue="") String domainName,
    		@RequestParam(name="entityStatus", defaultValue="")String entityStatus) throws ServiceException;
    
    @PutMapping("/domain")
    public void addDomain(@RequestBody Domain domain) throws ServiceException;  
    
    @PostMapping(value="/domain", consumes = MediaType.APPLICATION_JSON_VALUE)     
    public void updateDomain(@RequestBody Domain domain) throws ServiceException; 
    
    @DeleteMapping("/domain/{domain}")
    public void removedDomain(@PathVariable("domain") String domain) throws ServiceException;    
}

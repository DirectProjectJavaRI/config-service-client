package org.nhind.config.rest.feign;

import java.util.Collection;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.DNSRecord;
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
public interface DNSClient
{
    @GetMapping("/dns")
    public Collection<DNSRecord> getDNSRecords(@RequestParam(name="type", defaultValue = "-1")int type, 
    		@RequestParam(name="name", defaultValue="") String name) throws ServiceException;
    
    @PutMapping(value="/dns", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addDNSRecord(@RequestBody DNSRecord record) throws ServiceException;
    
    @PostMapping(value="/dns", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateDNSRecord(@RequestBody DNSRecord updateRecord) throws ServiceException;
    
    @DeleteMapping(value="/dns/{ids}")
    public void removeDNSRecordsByIds(@PathVariable("ids") String ids) throws ServiceException;
}

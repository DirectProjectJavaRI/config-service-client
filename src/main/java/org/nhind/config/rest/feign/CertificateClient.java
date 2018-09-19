package org.nhind.config.rest.feign;

import java.util.Collection;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Certificate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="direct-config-service", url = "${direct.config.service.url}")
public interface CertificateClient
{
    @GetMapping("/certificate")
    public Collection<Certificate> getAllCertificates() throws ServiceException;
    
    @GetMapping("/certificate/{owner}")
    public Collection<Certificate> getCertificatesByOwner(@PathVariable("owner") String owner) throws ServiceException;
    
    @GetMapping("/certificate/{owner}/{thumbprint}")
    public Certificate getCertificatesByOwnerAndThumbprint(@PathVariable("owner") String owner, 
    		@PathVariable("thumbprint") String thumbprint) throws ServiceException;
    
    @PutMapping(value="/certificate", consumes = MediaType.APPLICATION_JSON_VALUE)       
    public void addCertificate(@RequestBody Certificate cert) throws ServiceException;

    @DeleteMapping("/certificate/ids/{ids}")   
    public void removeCertificatesByIds(@PathVariable("ids") String ids) throws ServiceException;
    
    @DeleteMapping("/certificate/{owner}")  
    public void removeCertificatesByOwner(@PathVariable("owner") String owner) throws ServiceException;
}

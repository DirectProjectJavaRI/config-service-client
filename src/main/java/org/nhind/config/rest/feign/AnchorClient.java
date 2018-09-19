package org.nhind.config.rest.feign;

import java.util.Collection;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Anchor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="direct-config-service", url = "${direct.config.service.url}")
public interface AnchorClient
{
    @GetMapping("/anchor/{owner}")
    public Collection<Anchor> getAnchorForOwner(@RequestParam(name="incoming", defaultValue="false") boolean incoming, 
    		@RequestParam(name="outgoing", defaultValue="false") boolean outgoing, 
    		@RequestParam(name="thumbprint", defaultValue="") String thumbprint, 
    		@PathVariable("owner") String owner)  throws ServiceException;
    
    @GetMapping("/anchor")
    public Collection<Anchor> getAnchors() throws ServiceException;
    
    @PutMapping(value={"/anchor"}, consumes = MediaType.APPLICATION_JSON_VALUE)     
    public void addAnchor(@RequestBody Anchor anchor) throws ServiceException;
  
    @DeleteMapping("/anchor/ids/{ids}")   
    public void removeAnchorsByIds(@PathVariable("ids")  String ids) throws ServiceException;  
    
    @DeleteMapping("/anchor/{owner}")  
    public void removeAnchorsByOwner(@PathVariable("owner") String owner) throws ServiceException;
    
    
}

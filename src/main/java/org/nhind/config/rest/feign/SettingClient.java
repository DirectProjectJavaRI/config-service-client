package org.nhind.config.rest.feign;

import java.util.Collection;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Setting;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name="direct-config-service", url = "${direct.config.service.url}")
public interface SettingClient 
{
    @GetMapping("/setting")
    public Collection<Setting> getAllSettings() throws ServiceException;
    
    @GetMapping("/setting/{name}")
    public Setting getSettingByName(@PathVariable("name") String name) throws ServiceException;
    
    @PutMapping("/setting/{name}/{value}")  
    public void addSetting(@PathVariable("name") String name, @PathVariable("value") String value) throws ServiceException;
    
    @PostMapping("/setting/{name}/{value}")  
    public void updateSetting(@PathVariable("name") String name, @PathVariable("value") String value) throws ServiceException;
    
    @DeleteMapping("/setting/{name}")
    public void removeSettingByName(@PathVariable("name") String name) throws ServiceException;
}

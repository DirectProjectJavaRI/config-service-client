/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhind.config.rest.feign;

import java.util.Collection;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.feign.DefaultFeignClientConfiguration;
import org.nhindirect.config.model.Setting;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="direct-config-service", url = "${direct.config.service.url}", configuration=DefaultFeignClientConfiguration.class)
public interface SettingClient 
{
    @GetMapping("/setting")
    public Collection<Setting> getAllSettings() throws ServiceException;
    
    @GetMapping("/setting/{name}")
    public Setting getSettingByName(@PathVariable("name") String name) throws ServiceException;
    
    @PutMapping("/setting/{name}/{value}")  
    public void addSetting(@PathVariable("name") String name, @PathVariable("value") String value) throws ServiceException;
    
    @PutMapping("/setting")  
    public void addSetting(@RequestBody Setting setting) throws ServiceException;
    
    @PostMapping("/setting/{name}/{value}")  
    public void updateSetting(@PathVariable("name") String name, @PathVariable("value") String value) throws ServiceException;
    
    @PostMapping("/setting")  
    public void updateSetting(@RequestBody Setting setting) throws ServiceException;
    
    @DeleteMapping("/setting/{name}")
    public void removeSettingByName(@PathVariable("name") String name) throws ServiceException;
}

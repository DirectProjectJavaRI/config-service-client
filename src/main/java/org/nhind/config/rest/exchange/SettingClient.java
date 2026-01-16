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

package org.nhind.config.rest.exchange;

import java.util.Collection;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Setting;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

public interface SettingClient 
{
    @GetExchange("/setting")
    public Collection<Setting> getAllSettings() throws ServiceException;
    
    @GetExchange("/setting/{name}")
    public Setting getSettingByName(@PathVariable("name") String name) throws ServiceException;
    
    @PutExchange(value="/setting/{name}/{value}", contentType=MediaType.APPLICATION_JSON_VALUE,  accept = MediaType.APPLICATION_JSON_VALUE)  
    public void addSetting(@PathVariable("name") String name, @PathVariable("value") String value) throws ServiceException;
    
    @PutExchange(value="/setting", contentType=MediaType.APPLICATION_JSON_VALUE,  accept = MediaType.APPLICATION_JSON_VALUE)  
    public void addSetting(@RequestBody Setting setting) throws ServiceException;
    
    @PostExchange(value="/setting/{name}/{value}", contentType=MediaType.APPLICATION_JSON_VALUE,  accept = MediaType.APPLICATION_JSON_VALUE)  
    public void updateSetting(@PathVariable("name") String name, @PathVariable("value") String value) throws ServiceException;
    
    @PostExchange(value="/setting", contentType=MediaType.APPLICATION_JSON_VALUE,  accept = MediaType.APPLICATION_JSON_VALUE)  
    public void updateSetting(@RequestBody Setting setting) throws ServiceException;
    
    @DeleteExchange("/setting/{name}")
    public void removeSettingByName(@PathVariable("name") String name) throws ServiceException;
}

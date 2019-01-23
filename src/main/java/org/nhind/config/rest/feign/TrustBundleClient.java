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
import org.nhindirect.config.model.TrustBundle;
import org.nhindirect.config.model.TrustBundleDomainReltn;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="direct-config-service", url = "${direct.config.service.url}", configuration=DefaultFeignClientConfiguration.class)
public interface TrustBundleClient
{
    @GetMapping("/trustbundle")
    public Collection<TrustBundle> getTrustBundles(@RequestParam(name="fetchAnchors", defaultValue="true") boolean fetchAnchors) throws ServiceException;
    
    @GetMapping("/trustbundle/domains/{domainName}")
    public Collection<TrustBundleDomainReltn> getTrustBundlesByDomain(@PathVariable("domainName") String domainName, 
    		@RequestParam(name="fetchAnchors", defaultValue="true") boolean fetchAnchors) throws ServiceException;
    
    @GetMapping(value="/trustbundle/domains/bundles/reltns")
    public Collection<TrustBundleDomainReltn> getAllTrustBundleDomainRelts(
    		@RequestParam(name="fetchAnchors", defaultValue="true") boolean fetchAnchors) throws ServiceException;

    @GetMapping("/trustbundle/{bundleName}")
    public TrustBundle getTrustBundleByName(@PathVariable("bundleName") String bundleName) throws ServiceException;
    
    @PutMapping(value="/trustbundle", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addTrustBundle(@RequestBody TrustBundle bundle) throws ServiceException;
    
    @PostMapping("/trustbundle/{bundle}/refreshBundle")
    public void refreshTrustBundle(@PathVariable("bundle") String bundleName) throws ServiceException;
    
    @DeleteMapping("/trustbundle/{bundle}")
    public void deleteBundle(@PathVariable("bundle") String bundleName) throws ServiceException;
    
    @PostMapping(value="/trustbundle/{bundle}/signingCert", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateSigningCert(@PathVariable("bundle") String bundleName, @RequestBody(required=false) byte[] certData) throws ServiceException;
    
    @PostMapping(value="/trustbundle/{bundle}/bundleAttributes", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateBundleAttributes(@PathVariable("bundle") String bundleName, @RequestBody TrustBundle bundleData) throws ServiceException;
    
    @PostMapping("/trustbundle/{bundle}/{domain}")
    public void associateTrustBundleToDomain(@PathVariable("bundle") String bundleName, @PathVariable("domain") String domainName,
    		@RequestParam(name="incoming", defaultValue="true") boolean incoming, @RequestParam(name="outgoing", defaultValue="true") boolean outgoing) throws ServiceException;
    
    @DeleteMapping("/trustbundle/{bundle}/{domain}")
    public void disassociateTrustBundleFromDomain(@PathVariable("bundle") String bundleName, @PathVariable("domain") String domainName) throws ServiceException;
    
    @DeleteMapping("/trustbundle/{domain}/deleteFromDomain")
    public void disassociateTrustBundlesFromDomain(@PathVariable("domain") String domainName) throws ServiceException;
    
    @DeleteMapping("/trustbundle/{bundle}/deleteFromBundle")
    public void disassociateTrustBundleFromDomains(@PathVariable("bundle") String bundleName) throws ServiceException;
}

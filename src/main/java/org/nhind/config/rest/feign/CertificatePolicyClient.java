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
import org.nhindirect.config.model.CertPolicy;
import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.model.CertPolicyGroupDomainReltn;
import org.nhindirect.config.model.CertPolicyGroupUse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="direct-config-service", url = "${direct.config.service.url}", configuration=DefaultFeignClientConfiguration.class)
public interface CertificatePolicyClient
{
    @GetMapping("/certpolicy")
    public Collection<CertPolicy> getPolicies() throws ServiceException;
    
    @GetMapping("/certpolicy/{policyName}")
    public CertPolicy getPolicyByName(@PathVariable("policyName") String policyName) throws ServiceException;
    
    @PutMapping("/certpolicy")  
    public void addPolicy(@RequestBody CertPolicy policy) throws ServiceException;
    
    @DeleteMapping("/certpolicy/{policyName}")   
    public void removePolicyByName(@PathVariable("policyName") String policyName) throws ServiceException;
    
    @PostMapping(value="/certpolicy/{policyName}/policyAttributes", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updatePolicyAttributes(@PathVariable("policyName") String policyName, @RequestBody CertPolicy policyData) throws ServiceException;
    
    @GetMapping("/certpolicy/groups")
    public Collection<CertPolicyGroup> getPolicyGroups() throws ServiceException;
    
    @GetMapping("/certpolicy/groups/{groupName}")
    public CertPolicyGroup getPolicyGroupByName(@PathVariable("groupName") String groupName) throws ServiceException;
    
    @PutMapping(value="/certpolicy/groups", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addPolicyGroup(@RequestBody CertPolicyGroup group) throws ServiceException;
    
    @DeleteMapping("/certpolicy/groups/{groupName}")  
    public void removePolicyGroupByName(@PathVariable("groupName") String groupName) throws ServiceException;
    
    @PostMapping(value="/certpolicy/groups/{groupName}/groupAttributes", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateGroupAttributes(@PathVariable("groupName") String groupName, @RequestBody String newGroupName) throws ServiceException;
    
    @PostMapping(value="/certpolicy/groups/uses/{group}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addPolicyUseToGroup(@PathVariable("group") String groupName, @RequestBody CertPolicyGroupUse use) throws ServiceException;
    
    @PostMapping(value="/certpolicy/groups/uses/{group}/removePolicy", consumes = MediaType.APPLICATION_JSON_VALUE)    
    public void removedPolicyUseFromGroup(@PathVariable("group") String groupName, @RequestBody CertPolicyGroupUse use) throws ServiceException;
    
    @GetMapping("/certpolicy/groups/domain")
    public Collection<CertPolicyGroupDomainReltn> getPolicyGroupDomainReltns() throws ServiceException;
    
    @GetMapping("/certpolicy/groups/domain/{domain}")
    public Collection<CertPolicyGroup> getPolicyGroupsByDomain(@PathVariable("domain") String domainName) throws ServiceException;
    
    @PostMapping("/certpolicy/groups/domain/{group}/{domain}")
    public void associatePolicyGroupToDomain(@PathVariable("group") String groupName, @PathVariable("domain") String domainName) throws ServiceException;
    
    @DeleteMapping("/certpolicy/groups/domain/{group}/{domain}")
    public void disassociatePolicyGroupFromDomain(@PathVariable("group") String groupName, @PathVariable("domain") String domainName) throws ServiceException;
    
    @DeleteMapping("/certpolicy/groups/domain/{domain}/deleteFromDomain")
    public void disassociatePolicyGroupsFromDomain(@PathVariable("domain") String domainName) throws ServiceException;
    
    @DeleteMapping("/certpolicy/groups/domain/{group}/deleteFromGroup")
    public void disassociatePolicyGroupFromDomains(@PathVariable("group") String groupName) throws ServiceException;
}

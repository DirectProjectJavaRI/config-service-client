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
import org.nhindirect.config.model.CertPolicy;
import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.model.CertPolicyGroupDomainReltn;
import org.nhindirect.config.model.CertPolicyGroupUse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

public interface CertificatePolicyClient
{
    @GetExchange("/certpolicy")
    public Collection<CertPolicy> getPolicies() throws ServiceException;
    
    @GetExchange("/certpolicy/{policyName}")
    public CertPolicy getPolicyByName(@PathVariable("policyName") String policyName) throws ServiceException;
    
    @PutExchange("/certpolicy")  
    public void addPolicy(@RequestBody CertPolicy policy) throws ServiceException;
    
    @DeleteExchange("/certpolicy/{policyName}")   
    public void removePolicyByName(@PathVariable("policyName") String policyName) throws ServiceException;
    
    @PostExchange(value="/certpolicy/{policyName}/policyAttributes", contentType=MediaType.APPLICATION_JSON_VALUE,  accept = MediaType.APPLICATION_JSON_VALUE)
    public void updatePolicyAttributes(@PathVariable("policyName") String policyName, @RequestBody CertPolicy policyData) throws ServiceException;
    
    @GetExchange("/certpolicy/groups")
    public Collection<CertPolicyGroup> getPolicyGroups() throws ServiceException;
    
    @GetExchange("/certpolicy/groups/{groupName}")
    public CertPolicyGroup getPolicyGroupByName(@PathVariable("groupName") String groupName) throws ServiceException;
    
    @PutExchange(value="/certpolicy/groups", contentType=MediaType.APPLICATION_JSON_VALUE,  accept = MediaType.APPLICATION_JSON_VALUE)
    public void addPolicyGroup(@RequestBody CertPolicyGroup group) throws ServiceException;
    
    @DeleteExchange("/certpolicy/groups/{groupName}")  
    public void removePolicyGroupByName(@PathVariable("groupName") String groupName) throws ServiceException;
    
    @PostExchange(value="/certpolicy/groups/{groupName}/groupAttributes", contentType=MediaType.APPLICATION_JSON_VALUE,  accept = MediaType.APPLICATION_JSON_VALUE)
    public void updateGroupAttributes(@PathVariable("groupName") String groupName, @RequestBody String newGroupName) throws ServiceException;
    
    @PostExchange(value="/certpolicy/groups/uses/{group}", contentType=MediaType.APPLICATION_JSON_VALUE,  accept = MediaType.APPLICATION_JSON_VALUE)
    public void addPolicyUseToGroup(@PathVariable("group") String groupName, @RequestBody CertPolicyGroupUse use) throws ServiceException;
    
    @PostExchange(value="/certpolicy/groups/uses/{group}/removePolicy", contentType=MediaType.APPLICATION_JSON_VALUE,  accept = MediaType.APPLICATION_JSON_VALUE)    
    public void removedPolicyUseFromGroup(@PathVariable("group") String groupName, @RequestBody CertPolicyGroupUse use) throws ServiceException;
    
    @GetExchange("/certpolicy/groups/domain")
    public Collection<CertPolicyGroupDomainReltn> getPolicyGroupDomainReltns() throws ServiceException;
    
    @GetExchange("/certpolicy/groups/domain/{domain}")
    public Collection<CertPolicyGroup> getPolicyGroupsByDomain(@PathVariable("domain") String domainName) throws ServiceException;
    
    @PostExchange("/certpolicy/groups/domain/{group}/{domain}")
    public void associatePolicyGroupToDomain(@PathVariable("group") String groupName, @PathVariable("domain") String domainName) throws ServiceException;
    
    @DeleteExchange("/certpolicy/groups/domain/{group}/{domain}")
    public void disassociatePolicyGroupFromDomain(@PathVariable("group") String groupName, @PathVariable("domain") String domainName) throws ServiceException;
    
    @DeleteExchange("/certpolicy/groups/domain/{domain}/deleteFromDomain")
    public void disassociatePolicyGroupsFromDomain(@PathVariable("domain") String domainName) throws ServiceException;
    
    @DeleteExchange("/certpolicy/groups/domain/{group}/deleteFromGroup")
    public void disassociatePolicyGroupFromDomains(@PathVariable("group") String groupName) throws ServiceException;
}

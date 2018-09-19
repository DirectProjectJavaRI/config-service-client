package org.nhind.config.rest.feign;

import java.util.Collection;

import org.nhindirect.common.rest.exceptions.ServiceException;
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

@FeignClient(name="direct-config-service", url = "${direct.config.service.url}")
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

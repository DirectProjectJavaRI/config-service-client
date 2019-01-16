package org.nhind.config.rest.impl;

import java.util.Collection;
import java.util.Collections;

import org.nhind.config.rest.CertPolicyService;
import org.nhind.config.rest.feign.CertificatePolicyClient;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.nhindirect.config.model.CertPolicy;
import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.model.CertPolicyGroupDomainReltn;
import org.nhindirect.config.model.CertPolicyGroupUse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultCertPolicyService implements CertPolicyService
{
	protected CertificatePolicyClient certPolClient;
	
    public DefaultCertPolicyService(CertificatePolicyClient certPolClient) 
    {	
    	this.certPolClient = certPolClient;
    }

	@Autowired
	public void setCertificatePolicyClient(CertificatePolicyClient certPolClient)
	{
		this.certPolClient = certPolClient;
	}
    
	@Override
	public Collection<CertPolicy> getPolicies() throws ServiceException 
	{
		final Collection<CertPolicy> policies = certPolClient.getPolicies();
		return (policies == null) ? Collections.emptyList() : policies;
	}

	@Override
	public CertPolicy getPolicyByName(String policyName) throws ServiceException 
	{
		try
		{
			return certPolClient.getPolicyByName(policyName);
		}
		catch (ServiceMethodException e)
		{
			if (e.getResponseCode() == 404)
				return null;
			throw e;
		}
	}

	@Override
	public void addPolicy(CertPolicy policy) throws ServiceException 
	{
		certPolClient.addPolicy(policy);	
	}

	@Override
	public void deletePolicy(String policyName) throws ServiceException 
	{
		certPolClient.removePolicyByName(policyName);	
	}

	@Override
	public void updatePolicy(String policyName, CertPolicy policyAttributes) throws ServiceException 
	{
		certPolClient.updatePolicyAttributes(policyName, policyAttributes);
	}

	@Override
	public Collection<CertPolicyGroup> getPolicyGroups() throws ServiceException 
	{
		final Collection<CertPolicyGroup> groups = certPolClient.getPolicyGroups();
		return (groups == null) ? Collections.emptyList() : groups;
	}

	@Override
	public CertPolicyGroup getPolicyGroup(String groupName) throws ServiceException 
	{
		try
		{
			return certPolClient.getPolicyGroupByName(groupName);		
		}
		catch (ServiceMethodException e)
		{
			if (e.getResponseCode() == 404)
				return null;
			throw e;
		}
	}

	@Override
	public void addPolicyGroup(CertPolicyGroup group) throws ServiceException 
	{
		certPolClient.addPolicyGroup(group);
	}

	@Override
	public void deletePolicyGroup(String groupName) throws ServiceException 
	{
		certPolClient.removePolicyGroupByName(groupName);
	}

	@Override
	public void updatePolicyGroup(String groupName, String newGroupName) throws ServiceException  
	{
		certPolClient.updateGroupAttributes(groupName, newGroupName);
	}

	@Override
	public void addPolicyUseToGroup(String groupName, CertPolicyGroupUse use) throws ServiceException 
	{
		certPolClient.addPolicyUseToGroup(groupName, use);
	}

	@Override
	public void removePolicyUseFromGroup(String groupName, CertPolicyGroupUse use) throws ServiceException 
	{
		certPolClient.removedPolicyUseFromGroup(groupName, use);
	}

	@Override
	public Collection<CertPolicyGroupDomainReltn> getPolicyGroupDomainReltns() throws ServiceException 
	{
		final Collection<CertPolicyGroupDomainReltn> reltns = certPolClient.getPolicyGroupDomainReltns();
		return (reltns == null) ? Collections.emptyList() : reltns;
	}

	@Override
	public Collection<CertPolicyGroup> getPolicyGroupsByDomain(String domainName) throws ServiceException 
	{
		final Collection<CertPolicyGroup> groups = certPolClient.getPolicyGroupsByDomain(domainName);
		return (groups == null) ? Collections.emptyList() : groups;
	}

	@Override
	public void associatePolicyGroupToDomain(String groupName, String domainName) throws ServiceException 
	{
		certPolClient.associatePolicyGroupToDomain(groupName, domainName);
	}

	@Override
	public void disassociatePolicyGroupFromDomain(String groupName, String domainName) throws ServiceException 
	{
		certPolClient.disassociatePolicyGroupFromDomain(groupName, domainName);
	}

	@Override
	public void disassociatePolicyGroupsFromDomain(String domainName) throws ServiceException 
	{
		certPolClient.disassociatePolicyGroupsFromDomain(domainName);
	}

	@Override
	public void disassociatePolicyGroupFromDomains(String groupName) throws ServiceException 
	{
		certPolClient.disassociatePolicyGroupFromDomains(groupName);

	}
}

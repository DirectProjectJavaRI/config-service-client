package org.nhind.config.rest.impl;

import java.util.Collection;
import java.util.Collections;

import org.nhind.config.rest.DomainService;
import org.nhind.config.rest.feign.DomainClient;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultDomainService implements DomainService
{
	protected DomainClient domainClient;
	
    public DefaultDomainService(DomainClient domainClient) 
    {	
        this.domainClient = domainClient;
    }

	@Autowired
	public void setDomainClient(DomainClient domainClient)
	{
		this.domainClient = domainClient;
	}
    
	@Override
	public Domain getDomain(String domainName) throws ServiceException
	{
		try
		{
			return domainClient.getDomain(domainName);
		}
		catch (ServiceMethodException e)
		{
			if (e.getResponseCode() == 404)
				return null;
			throw e;
		}
	}

	@Override
	public Collection<Domain> searchDomains(String domainName, EntityStatus status) throws ServiceException
	{
		final String statusString = (status == null) ? "" : status.toString();
		final Collection<Domain> domains = domainClient.searchDomains(domainName, statusString);
		return (domains == null) ? Collections.emptyList() : domains;
	}

	@Override
	public void addDomain(Domain domain) throws ServiceException
	{
		domainClient.addDomain(domain);
	}

	@Override
	public void updateDomain(Domain domain) throws ServiceException
	{
		domainClient.updateDomain(domain);
	}

	@Override
	public void deleteDomain(String domainName) throws ServiceException
	{
		domainClient.removedDomain(domainName);
	}
}

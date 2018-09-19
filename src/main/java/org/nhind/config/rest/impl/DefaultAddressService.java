package org.nhind.config.rest.impl;

import java.util.Collection;
import java.util.Collections;

import org.nhind.config.rest.AddressService;
import org.nhind.config.rest.feign.AddressClient;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.nhindirect.config.model.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultAddressService implements AddressService
{
	protected AddressClient addrClient;
	
    public DefaultAddressService(AddressClient addrClient) 
    {	
    	this.addrClient = addrClient;
    }

	@Autowired
	public void setAddressClient(AddressClient addrClient)
	{
		this.addrClient = addrClient;
	}
	
	@Override
	public Address getAddress(String addressName) throws ServiceException 
	{
		try
		{
			return addrClient.getAddress(addressName);
		}
		catch (ServiceMethodException e)
		{
			if (e.getResponseCode() == 404)
				return null;
			throw e;
		}
	}

	@Override
	public Collection<Address> getAddressesByDomain(String domainName) throws ServiceException 
	{
		final Collection<Address> addrs = addrClient.getAddressesByDomain(domainName);
		return (addrs == null) ? Collections.emptyList() : addrs;
	}

	@Override
	public void addAddress(Address address) throws ServiceException 
	{
		addrClient.addAddress(address);

	}

	@Override
	public void updateAddress(Address address) throws ServiceException 
	{
		addrClient.updateAddress(address);
	}

	@Override
	public void deleteAddress(String address) throws ServiceException 
	{	
		addrClient.removeAddress(address);
	}    
}

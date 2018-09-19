package org.nhind.config.rest.impl;

import java.util.Collection;
import java.util.Collections;

import org.nhind.config.rest.DNSService;
import org.nhind.config.rest.feign.DNSClient;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.DNSRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultDNSService implements DNSService
{
	protected DNSClient dnsClient;
	
    public DefaultDNSService(DNSClient dnsClient) 
    {	
        this.dnsClient = dnsClient;
    }
    
	@Autowired
	public void setAddressClient(DNSClient dnsClient)
	{
		this.dnsClient = dnsClient;
	}

	@Override
	public Collection<DNSRecord> getDNSRecord(int type, String name) throws ServiceException 
	{
		final Collection<DNSRecord> records = dnsClient.getDNSRecords(type, name);
		return (records == null) ? Collections.emptyList() : records;	
	}

	@Override
	public void addDNSRecord(DNSRecord record) throws ServiceException 
	{
		dnsClient.addDNSRecord(record);	
	}

	@Override
	public void updatedDNSRecord(DNSRecord record) throws ServiceException 
	{
		dnsClient.updateDNSRecord(record);
	}

	@Override
	public void deleteDNSRecordsByIds(Collection<Long> ids) throws ServiceException 
	{
    	final StringBuilder builder = new StringBuilder();
    	
    	int cnt = 0;
    	for (Long id : ids)
    	{
    		builder.append(id);
    		if (cnt < ids.size())
    			builder.append(",");
    			
    		++cnt;
    	}
		
		dnsClient.removeDNSRecordsByIds(builder.toString());		
	}
}

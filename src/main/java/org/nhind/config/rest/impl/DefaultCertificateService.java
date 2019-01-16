package org.nhind.config.rest.impl;

import java.util.Collection;
import java.util.Collections;

import org.nhind.config.rest.CertificateService;
import org.nhind.config.rest.feign.CertificateClient;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.nhindirect.config.model.Certificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultCertificateService implements CertificateService
{
	protected CertificateClient certClient;
	
    public DefaultCertificateService(CertificateClient certClient) 
    {	
        this.certClient = certClient;
    }

	@Autowired
	public void setCertificateClient(CertificateClient certClient)
	{
		this.certClient = certClient;
	}
    
	@Override
	public Collection<Certificate> getAllCertificates() throws ServiceException 
	{
		final Collection<Certificate> certs = certClient.getAllCertificates();
		return (certs == null) ? Collections.emptyList() : certs;
	}

	@Override
	public Collection<Certificate> getCertificatesByOwner(String owner) throws ServiceException 
	{
		final Collection<Certificate> certs = certClient.getCertificatesByOwner(owner);
		return (certs == null) ? Collections.emptyList() : certs;	
	}

	@Override
	public Certificate getCertificatesByOwnerAndThumbprint(
			String owner, String thumbprint) throws ServiceException 
	{
		try
		{
			return certClient.getCertificatesByOwnerAndThumbprint(owner, thumbprint);
		}
		catch (ServiceMethodException e)
		{
			if (e.getResponseCode() == 404)
				return null;
			throw e;
		}
	}

	@Override
	public void addCertificate(Certificate cert) throws ServiceException 
	{
		certClient.addCertificate(cert);
	}

	@Override
	public void deleteCertificatesByIds(Collection<Long> ids) throws ServiceException 
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
		
		certClient.removeCertificatesByIds(builder.toString());		
	}

	@Override
	public void deleteCertificateByOwner(String owner) throws ServiceException 
	{
		certClient.removeCertificatesByOwner(owner);
	}
}

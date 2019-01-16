package org.nhind.config.rest.impl;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;

import org.nhind.config.rest.TrustBundleService;
import org.nhind.config.rest.feign.TrustBundleClient;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.nhindirect.config.model.TrustBundle;
import org.nhindirect.config.model.TrustBundleDomainReltn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultTrustBundleService implements TrustBundleService
{
	protected TrustBundleClient bundleClient;
	
    public DefaultTrustBundleService(TrustBundleClient bundleClient) 
    {	
        this.bundleClient = bundleClient;
    }

	@Autowired
	public void setTrustBundleClient(TrustBundleClient bundleClient)
	{
		this.bundleClient = bundleClient;
	}
    
	@Override
	public Collection<TrustBundle> getTrustBundles(boolean fetchAnchors) throws ServiceException 
	{
		final Collection<TrustBundle> bundles = bundleClient.getTrustBundles(fetchAnchors);
		return (bundles == null) ? Collections.emptyList() : bundles;	
	}

	@Override
	public Collection<TrustBundleDomainReltn> getTrustBundlesByDomain(String domainName, boolean fetchAnchors) throws ServiceException 
	{		
		final Collection<TrustBundleDomainReltn> bundles = bundleClient.getTrustBundlesByDomain(domainName, fetchAnchors);
		return (bundles == null) ? Collections.emptyList() : bundles;		
	}

	@Override
	public Collection<TrustBundleDomainReltn> getAllTrustBundleDomainReltns(boolean fetchAnchors) throws ServiceException
	{
		final Collection<TrustBundleDomainReltn> bundles = bundleClient.getAllTrustBundleDomainRelts(fetchAnchors);
		return (bundles == null) ? Collections.emptyList() : bundles;	
	}
	
	@Override
	public TrustBundle getTrustBundle(String bundleName) throws ServiceException 
	{
		try
		{
			return bundleClient.getTrustBundleByName(bundleName);
		}
		catch (ServiceMethodException e)
		{
			if (e.getResponseCode() == 404)
				return null;
			throw e;
		}
	}

	@Override
	public void addTrustBundle(TrustBundle bundle) throws ServiceException 
	{
		bundleClient.addTrustBundle(bundle);
	}

	@Override
	public void refreshTrustBundle(String bundleName) throws ServiceException 
	{
		bundleClient.refreshTrustBundle(bundleName);
	}

	@Override
	public void deleteTrustBundle(String bundleName) throws ServiceException 
	{
		bundleClient.deleteBundle(bundleName);	
	}

	@Override
	public void updateSigningCert(String bundleName, X509Certificate cert) throws ServiceException 
	{
		try
		{
			byte[] certBytes = ((cert == null) ? new byte[0] : cert.getEncoded());
			bundleClient.updateSigningCert(bundleName, certBytes);
		}
		catch (ServiceException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new ServiceException("Invalid Certificate");
		}
	}

	@Override
	public void updateTrustBundleAttributes(String bundleName, TrustBundle bundleData) throws ServiceException 
	{
		bundleClient.updateBundleAttributes(bundleName, bundleData);
	}

	@Override
	public void associateTrustBundleToDomain(String bundleName, String domainName, boolean incoming, boolean outgoing) throws ServiceException 
	{
		bundleClient.associateTrustBundleToDomain(bundleName, domainName, incoming, outgoing);
	}

	@Override
	public void disassociateTrustBundleFromDomain(String bundleName, String domainName) throws ServiceException 
	{
		bundleClient.disassociateTrustBundleFromDomain(bundleName, domainName);		
	}

	@Override
	public void disassociateTrustBundlesFromDomain(String domainName) throws ServiceException 
	{
		bundleClient.disassociateTrustBundlesFromDomain(domainName);
	}

	@Override
	public void disassociateTrustBundleFromDomains(String bundleName) throws ServiceException 
	{
		bundleClient.disassociateTrustBundleFromDomains(bundleName);
	}
}

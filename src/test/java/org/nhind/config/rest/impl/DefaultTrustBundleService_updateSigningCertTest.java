package org.nhind.config.rest.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;

import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;
import org.nhind.config.testbase.TestUtils;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.nhindirect.config.model.TrustBundle;
import org.nhindirect.config.repository.TrustBundleRepository;

import reactor.core.publisher.Mono;

public class DefaultTrustBundleService_updateSigningCertTest extends SpringBaseTest
{
	
	abstract class TestPlan extends BaseTestPlan 
	{
		@Override
		protected void tearDownMocks()
		{

		}

		protected abstract Collection<TrustBundle> getBundlesToAdd();

		protected abstract X509Certificate getNewSigningCertificate() throws Exception;
		
		protected abstract String getBundleToUpdate();
		
		@Override
		protected void performInner() throws Exception
		{				
			
			final Collection<TrustBundle> bundlesToAdd = getBundlesToAdd();
			
			if (bundlesToAdd != null)
			{
				for (TrustBundle addBundle : bundlesToAdd)
				{
					try
					{
						bundleService.addTrustBundle(addBundle);
					}
					catch (ServiceException e)
					{
						throw e;
					}
				}
			}
			
			bundleService.updateSigningCert(getBundleToUpdate(), getNewSigningCertificate());

			
			final TrustBundle getBundle = bundleService.getTrustBundle(getBundleToUpdate());
			doAssertions(getBundle);
			
		}
			
		protected void doAssertions(TrustBundle bundle) throws Exception
		{
			
		}
	}	
	
	@Test
	public void testUpdateSigningCert_assertSigningCertUpdated()  throws Exception
	{
		new TestPlan()
		{
			protected Collection<TrustBundle> bundles;
			
			@Override
			protected Collection<TrustBundle> getBundlesToAdd()
			{
				try
				{
					bundles = new ArrayList<TrustBundle>();
					
					TrustBundle bundle = new TrustBundle();
					bundle.setBundleName("testBundle1");
					File fl = new File("src/test/resources/bundles/providerTestBundle.p7b");
					bundle.setBundleURL(filePrefix + fl.getAbsolutePath());	
					bundle.setRefreshInterval(24);
					bundle.setSigningCertificateData(null);		
					bundles.add(bundle);
					
					return bundles;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			@Override
			protected X509Certificate getNewSigningCertificate() throws Exception
			{
				return TestUtils.loadSigner("bundleSigner.der");
			}
			
			@Override
			protected String getBundleToUpdate()
			{
				return "testBundle1";
			}
			
			protected void doAssertions(TrustBundle bundle) throws Exception
			{
				assertEquals(getBundleToUpdate(), bundle.getBundleName());
				assertEquals(TestUtils.loadSigner("bundleSigner.der"), bundle.getSigningCertificateAsX509Certificate());
			}
		}.perform();
	}	
	
	@Test
	public void testUpdateSigningCert_changeToNull_assertSigningCertUpdated()  throws Exception
	{
		new TestPlan()
		{
			protected Collection<TrustBundle> bundles;
			
			@Override
			protected Collection<TrustBundle> getBundlesToAdd()
			{
				try
				{
					bundles = new ArrayList<TrustBundle>();
					
					TrustBundle bundle = new TrustBundle();
					bundle.setBundleName("testBundle1");
					File fl = new File("src/test/resources/bundles/providerTestBundle.p7b");
					bundle.setBundleURL(filePrefix + fl.getAbsolutePath());	
					bundle.setRefreshInterval(24);
					bundle.setSigningCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());		
					bundles.add(bundle);
					
					return bundles;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			@Override
			protected X509Certificate getNewSigningCertificate() throws Exception
			{
				return null;
			}
			
			@Override
			protected String getBundleToUpdate()
			{
				return "testBundle1";
			}
			
			protected void doAssertions(TrustBundle bundle) throws Exception
			{
				assertEquals(getBundleToUpdate(), bundle.getBundleName());
				assertNull(bundle.getSigningCertificateData());
			}
		}.perform();
	}			
	
	@Test
	public void testUpdateSigningCert_nonExistentBundle_assertNotFound()  throws Exception
	{
		new TestPlan()
		{
			protected Collection<TrustBundle> bundles;
			
			@Override
			protected Collection<TrustBundle> getBundlesToAdd()
			{
				try
				{
					bundles = new ArrayList<TrustBundle>();
					
					TrustBundle bundle = new TrustBundle();
					bundle.setBundleName("testBundle1");
					File fl = new File("src/test/resources/bundles/providerTestBundle.p7b");
					bundle.setBundleURL(filePrefix + fl.getAbsolutePath());	
					bundle.setRefreshInterval(24);
					bundle.setSigningCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());		
					bundles.add(bundle);
					
					return bundles;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			@Override
			protected X509Certificate getNewSigningCertificate() throws Exception
			{
				return null;
			}
			
			@Override
			protected String getBundleToUpdate()
			{
				return "testBundle2";
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(404, ex.getResponseCode());
			}
		}.perform();
	}		
	
	@Test
	public void testUpdateSigningCert_invalidCert_assertBadRequest()  throws Exception
	{
		new TestPlan()
		{
			protected Collection<TrustBundle> bundles;
			
			@Override
			protected Collection<TrustBundle> getBundlesToAdd()
			{
				try
				{
					bundles = new ArrayList<TrustBundle>();
					
					TrustBundle bundle = new TrustBundle();
					bundle.setBundleName("testBundle1");
					File fl = new File("src/test/resources/bundles/providerTestBundle.p7b");
					bundle.setBundleURL(filePrefix + fl.getAbsolutePath());	
					bundle.setRefreshInterval(24);
					bundle.setSigningCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());		
					bundles.add(bundle);
					
					return bundles;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			@Override
			protected X509Certificate getNewSigningCertificate() throws Exception
			{
				X509Certificate mockCert = mock(X509Certificate.class);
				when(mockCert.getEncoded()).thenReturn(new byte[] {124,3,2,1});
				return mockCert;
			}
			
			@Override
			protected String getBundleToUpdate()
			{
				return "testBundle1";
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(400, ex.getResponseCode());
			}
		}.perform();
	}			
	
	@Test
	public void testUpdateSigningCert_errorInLookup_assertServiceError()  throws Exception
	{
		new TestPlan()
		{	
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					TrustBundleRepository mockDAO = mock(TrustBundleRepository.class);
					doThrow(new RuntimeException()).when(mockDAO).findByBundleNameIgnoreCase(eq("testBundle1"));
					
					bundleResource.setTrustBundleRepository(mockDAO);
				}
				catch (Throwable t)
				{
					throw new RuntimeException(t);
				}
			}
			
			@Override
			protected void tearDownMocks()
			{
				super.tearDownMocks();
				
				bundleResource.setTrustBundleRepository(bundleRepo);
			}		
			
			@Override
			protected Collection<TrustBundle> getBundlesToAdd()
			{
				return null;
			}
			
			@Override
			protected X509Certificate getNewSigningCertificate() throws Exception
			{
				return null;
			}
			
			@Override
			protected String getBundleToUpdate()
			{
				return "testBundle1";
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(500, ex.getResponseCode());
			}
		}.perform();
	}	
	
	@Test
	public void testUpdateSigningCert_errorUpdate_assertServiceError()  throws Exception
	{
		new TestPlan()
		{	
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					TrustBundleRepository mockDAO = mock(TrustBundleRepository.class);
					
					final org.nhindirect.config.store.TrustBundle bundle = new org.nhindirect.config.store.TrustBundle();
					bundle.setBundleName("Test");
					
					when(mockDAO.findByBundleNameIgnoreCase("testBundle1")).thenReturn(Mono.just(bundle));
					doThrow(new RuntimeException()).when(mockDAO).save((org.nhindirect.config.store.TrustBundle)any());
					
					bundleResource.setTrustBundleRepository(mockDAO);
				}
				catch (Throwable t)
				{
					throw new RuntimeException(t);
				}
			}
			
			@Override
			protected void tearDownMocks()
			{
				super.tearDownMocks();
				
				bundleResource.setTrustBundleRepository(bundleRepo);
			}		
			
			@Override
			protected Collection<TrustBundle> getBundlesToAdd()
			{
				return null;
			}
			
			@Override
			protected X509Certificate getNewSigningCertificate() throws Exception
			{
				return null;
			}
			
			@Override
			protected String getBundleToUpdate()
			{
				return "testBundle1";
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(500, ex.getResponseCode());
			}
		}.perform();
	}			
}

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

public class DefaultTrustBundleService_updateTrustBundleAttributesTest extends SpringBaseTest
{
	abstract class TestPlan extends BaseTestPlan 
	{
		
		@Override
		protected void tearDownMocks()
		{

		}

		protected abstract Collection<TrustBundle> getBundlesToAdd();

		protected abstract String getBundleToUpdate();
		
		protected abstract TrustBundle getBundleDataToUpdate() throws Exception;
		
		protected abstract String getBundleUpdatedName();
			
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
			

			bundleService.updateTrustBundleAttributes(getBundleToUpdate(), getBundleDataToUpdate());
		
			final TrustBundle getBundle = bundleService.getTrustBundle(getBundleUpdatedName());
			doAssertions(getBundle);

			
		}
			
		protected void doAssertions(TrustBundle bundle) throws Exception
		{
			
		}
	}	
	
	@Test
	public void testUpdateBundleAttributes_changeName_assertNameChanged()  throws Exception
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
			protected TrustBundle getBundleDataToUpdate() throws Exception
			{
				final TrustBundle bundleData = new TrustBundle();
				bundleData.setBundleName("testBundle2");
				
				return bundleData;
				
			}
			
			@Override
			protected String getBundleToUpdate()
			{
				return "testBundle1";
			}
			
			@Override 
			protected String getBundleUpdatedName()
			{
				return "testBundle2";
			}
			
			protected void doAssertions(TrustBundle bundle) throws Exception
			{
				final TrustBundle addedBundle = this.bundles.iterator().next();
				
				assertEquals(getBundleUpdatedName(), bundle.getBundleName());
				assertNull(bundle.getSigningCertificateAsX509Certificate());
				assertEquals(addedBundle.getBundleURL(), bundle.getBundleURL());
				assertEquals(0, bundle.getRefreshInterval());
			}
		}.perform();
	}	
	
	@Test
	public void testUpdateBundleAttributes_newSigningCert_assertSigningCertChanged()  throws Exception
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
			protected TrustBundle getBundleDataToUpdate() throws Exception
			{
				final TrustBundle bundleData = new TrustBundle();
				bundleData.setBundleName("testBundle2");
				bundleData.setSigningCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());
				
				return bundleData;
				
			}
			
			@Override
			protected String getBundleToUpdate()
			{
				return "testBundle1";
			}
			
			@Override 
			protected String getBundleUpdatedName()
			{
				return "testBundle2";
			}
			
			protected void doAssertions(TrustBundle bundle) throws Exception
			{
				final TrustBundle addedBundle = this.bundles.iterator().next();
				
				assertEquals(getBundleUpdatedName(), bundle.getBundleName());
				assertEquals(TestUtils.loadSigner("bundleSigner.der"), bundle.getSigningCertificateAsX509Certificate());
				assertEquals(addedBundle.getBundleURL(), bundle.getBundleURL());
				assertEquals(0, bundle.getRefreshInterval());
			}
		}.perform();
	}			
	
	@Test
	public void testUpdateBundleAttributes_removeSigningCert_assertSigningCertNull()  throws Exception
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
			protected TrustBundle getBundleDataToUpdate() throws Exception
			{
				final TrustBundle bundleData = new TrustBundle();
				bundleData.setSigningCertificateData(null);
				bundleData.setBundleURL("");
				
				return bundleData;
				
			}
			
			@Override
			protected String getBundleToUpdate()
			{
				return "testBundle1";
			}
			
			@Override 
			protected String getBundleUpdatedName()
			{
				return "testBundle1";
			}
			
			protected void doAssertions(TrustBundle bundle) throws Exception
			{
				final TrustBundle addedBundle = this.bundles.iterator().next();
				
				assertEquals(getBundleUpdatedName(), bundle.getBundleName());
				assertNull(bundle.getSigningCertificateAsX509Certificate());
				assertEquals(addedBundle.getBundleURL(), bundle.getBundleURL());
				assertEquals(0, bundle.getRefreshInterval());
			}
		}.perform();
	}	
	
	@Test
	public void testUpdateBundleAttributes_newURL_assertURLChanged()  throws Exception
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
			protected TrustBundle getBundleDataToUpdate() throws Exception
			{
				final TrustBundle bundleData = new TrustBundle();
				File fl = new File("src/test/resources/bundles/invalidBundle.der");
				bundleData.setBundleURL(filePrefix + fl.getAbsolutePath());	
				
				return bundleData;
				
			}
			
			@Override
			protected String getBundleToUpdate()
			{
				return "testBundle1";
			}
			
			@Override 
			protected String getBundleUpdatedName()
			{
				return "testBundle1";
			}
			
			protected void doAssertions(TrustBundle bundle) throws Exception
			{
				
				assertEquals(getBundleUpdatedName(), bundle.getBundleName());
				assertNull(bundle.getSigningCertificateAsX509Certificate());
				assertTrue(bundle.getBundleURL().contains("invalidBundle.der"));
				assertEquals(0, bundle.getRefreshInterval());
			}
		}.perform();
	}			
	
	@Test
	public void testUpdateBundleAttributes_invalidNewCert_assertBadRequest()  throws Exception
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
			protected TrustBundle getBundleDataToUpdate() throws Exception
			{
				final TrustBundle bundleData = new TrustBundle();
				bundleData.setSigningCertificateData(new byte[]{1, 3,2});
				
				return bundleData;
				
			}
			
			@Override
			protected String getBundleToUpdate()
			{
				return "testBundle1";
			}
			
			@Override 
			protected String getBundleUpdatedName()
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
	public void testUpdateBundleAttributes_nonExistentBundle_assertNotFound()  throws Exception
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
			protected TrustBundle getBundleDataToUpdate() throws Exception
			{
				return new TrustBundle();
			}
			
			@Override
			protected String getBundleToUpdate()
			{
				return "testBundle2";
			}
			
			@Override 
			protected String getBundleUpdatedName()
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
	public void testUpdateBundleAttributes_errorInLookup_assertServiceError()  throws Exception
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
			protected TrustBundle getBundleDataToUpdate() throws Exception
			{
				return new TrustBundle();
			}
			
			@Override
			protected String getBundleToUpdate()
			{
				return "testBundle1";
			}
			
			@Override 
			protected String getBundleUpdatedName()
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
	public void testUpdateBundleAttributes_errorInUpdate_assertServiceError()  throws Exception
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
			protected TrustBundle getBundleDataToUpdate() throws Exception
			{
				return new TrustBundle();
			}
			
			@Override
			protected String getBundleToUpdate()
			{
				return "testBundle1";
			}
			
			@Override 
			protected String getBundleUpdatedName()
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

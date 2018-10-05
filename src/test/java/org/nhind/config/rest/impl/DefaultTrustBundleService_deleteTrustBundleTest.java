package org.nhind.config.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.nhindirect.config.model.TrustBundle;
import org.nhindirect.config.repository.TrustBundleRepository;

public class DefaultTrustBundleService_deleteTrustBundleTest extends SpringBaseTest
{
	abstract class TestPlan extends BaseTestPlan 
	{
		
		@Override
		protected void tearDownMocks()
		{

		}
		
		protected abstract Collection<TrustBundle> getBundlesToAdd();
		
		protected abstract String getBundleNameToDelete();
		
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
			
			bundleService.deleteTrustBundle(getBundleNameToDelete());

			doAssertions();

			
		}
			
		protected void doAssertions() throws Exception
		{
			
		}
	}	
	
	@Test
	public void testDeleteBundle_removeExistingBundle_assertBundleRemoved() throws Exception
	{
		new TestPlan()
		{
			
			protected Collection<TrustBundle> bundles;
			
			@Override
			protected Collection<TrustBundle> getBundlesToAdd()
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
			
			@Override
			protected String getBundleNameToDelete()
			{
				return "testBundle1";
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				assertNull(bundleRepo.findByBundleNameIgnoreCase("testBundle1"));
			}
		}.perform();
	}
	
	@Test
	public void testDeleteBundle_nonExistentBundle_assertNotFound() throws Exception
	{
		new TestPlan()
		{
			@Override
			protected Collection<TrustBundle> getBundlesToAdd()
			{
				return null;

			}
			
			@Override
			protected String getBundleNameToDelete()
			{
				return "testBundle1";
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
	public void testDeleteBundle_errorInLookup_assertServiceError() throws Exception
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
			protected String getBundleNameToDelete()
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
	public void testDeleteBundle_errorDelete_assertServiceError() throws Exception
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
					
					when(mockDAO.findByBundleNameIgnoreCase((String)any())).thenReturn(new org.nhindirect.config.store.TrustBundle());
					doThrow(new RuntimeException()).when(mockDAO).deleteById((Long)any());
					
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
			protected String getBundleNameToDelete()
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

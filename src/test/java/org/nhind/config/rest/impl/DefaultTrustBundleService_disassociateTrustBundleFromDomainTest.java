package org.nhind.config.rest.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.Address;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.model.TrustBundle;
import org.nhindirect.config.repository.DomainRepository;
import org.nhindirect.config.repository.TrustBundleDomainReltnRepository;
import org.nhindirect.config.repository.TrustBundleRepository;

import reactor.core.publisher.Mono;

public class DefaultTrustBundleService_disassociateTrustBundleFromDomainTest extends SpringBaseTest
{
		abstract class TestPlan extends BaseTestPlan 
		{
			protected Collection<TrustBundle> bundles;
			
			
			@Override
			protected void tearDownMocks()
			{

			}

			
			
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
			
			protected Domain getDomainToAdd()
			{
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				Domain domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.ENABLED);
				domain.setPostmasterAddress(postmasterAddress);			
				
				return domain;
			}
			
			protected String getBundleNameToAssociate()
			{
				return "testBundle1";
			}
			
			protected String getDomainNameToAssociate()
			{
				return "test.com";
			}
			
			protected abstract String getBundleNameToDisassociate();
			
			protected abstract String getDomainNameToDisassociate();
			
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
				
				final Domain addDomain = getDomainToAdd();
				
				if (addDomain != null)
				{
					try
					{
						domainService.addDomain(addDomain);
					}
					catch (ServiceException e)
					{
						throw e;
					}
				}
				
				// associate the bundle and domain
				if (bundlesToAdd != null && addDomain != null)
					bundleService.associateTrustBundleToDomain(getBundleNameToAssociate(), getDomainNameToAssociate(), true, true);

				bundleService.disassociateTrustBundleFromDomain(getBundleNameToDisassociate(), getDomainNameToDisassociate());

				doAssertions();

			}
				
			protected void doAssertions() throws Exception
			{
				
			}
		}	
		
		@Test
		public void testDisassociateBundleFromDomain_disassociateExistingDomainAndBundle_assertBundlesDisassociated()  throws Exception
		{
			new TestPlan()
			{
				
				@Override
				protected String getBundleNameToDisassociate()
				{
					return "testBundle1";
				}
				
				@Override
				protected String getDomainNameToDisassociate()
				{
					return "test.com";
				}
				
				protected void doAssertions() throws Exception
				{
					final Collection<org.nhindirect.config.store.TrustBundleDomainReltn> bundleRelts =  
							bundleDomainRepo.findByDomainId(domainRepo.findByDomainNameIgnoreCase(getDomainNameToAssociate()).block().getId()).collectList().block();
					
					assertTrue(bundleRelts.isEmpty());
					
				}
			}.perform();
		}	
		
		@Test
		public void testDisassociateBundleFromDomain_unknownBundle_assertNotFound()  throws Exception
		{
			new TestPlan()
			{
				
				@Override
				protected String getBundleNameToDisassociate()
				{
					return "testBundle1333";
				}
				
				@Override
				protected String getDomainNameToDisassociate()
				{
					return "test.com";
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
		public void testDisassociateBundleFromDomain_unknownDomain_assertNotFound()  throws Exception
		{
			new TestPlan()
			{
				
				@Override
				protected String getBundleNameToDisassociate()
				{
					return "testBundle1";
				}
				
				@Override
				protected String getDomainNameToDisassociate()
				{
					return "test.com123";
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
		public void testDisassociateBundleFromDomain_errorInBundleLookup_assertServiceError()  throws Exception
		{
			new TestPlan()
			{
				protected Collection<TrustBundle> getBundlesToAdd()
				{
					return null;
				}
				
				protected Domain getDomainToAdd()
				{
					return null;
				}
				
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						TrustBundleRepository mockBundleDAO = mock(TrustBundleRepository.class);
						DomainRepository mockDomainDAO = mock(DomainRepository.class);
						
						doThrow(new RuntimeException()).when(mockBundleDAO).findByBundleNameIgnoreCase((String)any());
						
						bundleResource.setTrustBundleRepository(mockBundleDAO);
						bundleResource.setDomainRepository(mockDomainDAO);
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
					bundleResource.setDomainRepository(domainRepo);
				}
				
				@Override
				protected String getBundleNameToDisassociate()
				{
					return "testBundle1";
				}
				
				@Override
				protected String getDomainNameToDisassociate()
				{
					return "test.com";
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
		public void testDisassociateBundleFromDomain_errorInDomainLookup_assertServiceError()  throws Exception
		{
			new TestPlan()
			{
				protected Collection<TrustBundle> getBundlesToAdd()
				{
					return null;
				}
				
				protected Domain getDomainToAdd()
				{
					return null;
				}
				
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						
						TrustBundleRepository mockBundleDAO = mock(TrustBundleRepository.class);
						DomainRepository mockDomainDAO = mock(DomainRepository.class);
						
						final org.nhindirect.config.store.TrustBundle dom = new org.nhindirect.config.store.TrustBundle();
						dom.setBundleName("Test");
						
						when(mockBundleDAO.findByBundleNameIgnoreCase("testBundle1")).thenReturn(Mono.just(dom));
						doThrow(new RuntimeException()).when(mockDomainDAO).findByDomainNameIgnoreCase((String)any());
						
						bundleResource.setTrustBundleRepository(mockBundleDAO);
						bundleResource.setDomainRepository(mockDomainDAO);
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
					bundleResource.setDomainRepository(domainRepo);
				}
				
				@Override
				protected String getBundleNameToDisassociate()
				{
					return "testBundle1";
				}
				
				@Override
				protected String getDomainNameToDisassociate()
				{
					return "test.com";
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
		public void testDisassociateBundleFromDomain_errorInDisassociate_assertServiceError()  throws Exception
		{
			new TestPlan()
			{
				protected Collection<TrustBundle> getBundlesToAdd()
				{
					return null;
				}
				
				protected Domain getDomainToAdd()
				{
					return null;
				}
				
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						TrustBundleRepository mockBundleDAO = mock(TrustBundleRepository.class);
						DomainRepository mockDomainDAO = mock(DomainRepository.class);
						TrustBundleDomainReltnRepository reltnDAO = mock(TrustBundleDomainReltnRepository.class);
						
						final org.nhindirect.config.store.TrustBundle bundle = new org.nhindirect.config.store.TrustBundle();
						bundle.setBundleName("Test");
						
						when(mockBundleDAO.findByBundleNameIgnoreCase("testBundle1")).thenReturn(Mono.just(bundle));
						
						final org.nhindirect.config.store.Domain dom = new org.nhindirect.config.store.Domain();
						dom.setDomainName("Test");
						
						when(mockDomainDAO.findByDomainNameIgnoreCase("test.com")).thenReturn(Mono.just(dom));
						doThrow(new RuntimeException()).when(reltnDAO).deleteByDomainIdAndTrustBundleId(any(), 
								any());
						
						
						bundleResource.setTrustBundleRepository(mockBundleDAO);
						bundleResource.setDomainRepository(mockDomainDAO);
						bundleResource.setTrustBundleDomainReltnRepository(reltnDAO);
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
					bundleResource.setDomainRepository(domainRepo);
					bundleResource.setTrustBundleDomainReltnRepository(bundleDomainRepo);
				}
				
				@Override
				protected String getBundleNameToDisassociate()
				{
					return "testBundle1";
				}
				
				@Override
				protected String getDomainNameToDisassociate()
				{
					return "test.com";
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

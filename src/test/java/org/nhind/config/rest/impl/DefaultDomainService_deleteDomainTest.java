package org.nhind.config.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.Address;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.repository.DomainRepository;

public class DefaultDomainService_deleteDomainTest extends SpringBaseTest
{
		abstract class TestPlan extends BaseTestPlan 
		{
			
			@Override
			protected void tearDownMocks()
			{

			}
			protected abstract Domain getDomainToAdd();
			
			protected abstract String getDomainNameToRemove();
			
			@Override
			protected void performInner() throws Exception
			{				
				
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
				
				try
				{
					domainService.deleteDomain(getDomainNameToRemove());
				}
				catch (ServiceException e)
				{
					throw e;
				}
				
				doAssertions();
			}
			
			
			protected void doAssertions() throws Exception
			{
				
			}
		}	

		
		@Test
		public void testRemoveDomain_removeExistingDomain_assertDomainRemoved() throws Exception
		{
			new TestPlan()
			{
				protected Domain domain;
				
				@Override
				protected Domain getDomainToAdd()
				{
					final Address postmasterAddress = new Address();
					postmasterAddress.setEmailAddress("me@test.com");
					
					domain = new Domain();
					
					domain.setDomainName("test.com");
					domain.setStatus(EntityStatus.ENABLED);
					domain.setPostmasterAddress(postmasterAddress);			
					
					return domain;
				}
				
				@Override
				protected String getDomainNameToRemove()
				{
					return "test.com";
				}
				
				@Override
				protected void doAssertions() throws Exception
				{
					assertNull(domainRepo.findByDomainNameIgnoreCase("@test.com"));
				}
			}.perform();
		}
		
		@Test
		public void testRemoveDomain_removeExistingDomainWithAddresses_assertDomainRemoved() throws Exception
		{
			new TestPlan()
			{
				protected Domain domain;
				
				@Override
				protected Domain getDomainToAdd()
				{
					final Address postmasterAddress = new Address();
					postmasterAddress.setEmailAddress("me@test.com");
					
					domain = new Domain();
					
					final Address addr = new Address();
					addr.setEmailAddress("you@test.com");
					addr.setStatus(EntityStatus.ENABLED);
					Collection<Address> addrs = new ArrayList<Address>();
					addrs.add(addr);
					
					domain.setDomainName("test.com");
					domain.setStatus(EntityStatus.ENABLED);
					domain.setPostmasterAddress(postmasterAddress);			
					domain.setAddresses(addrs);
					
					return domain;
				}
				
				@Override
				protected String getDomainNameToRemove()
				{
					return "test.com";
				}
				
				@Override
				protected void doAssertions() throws Exception
				{
					assertNull(domainRepo.findByDomainNameIgnoreCase("@test.com"));
				}
			}.perform();
		}		
		
		@Test
		public void testRemoveDomain_nonExxistentDomain_assertNotFound() throws Exception
		{
			new TestPlan()
			{
				protected Domain domain;
				
				@Override
				protected Domain getDomainToAdd()
				{
					final Address postmasterAddress = new Address();
					postmasterAddress.setEmailAddress("me@test.com");
					
					domain = new Domain();
					
					domain.setDomainName("test.com");
					domain.setStatus(EntityStatus.ENABLED);
					domain.setPostmasterAddress(postmasterAddress);			
					
					return domain;
				}
				
				@Override
				protected String getDomainNameToRemove()
				{
					return "test2.com";
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
		public void testRemoveDomain_errorInLookup_assertServerError() throws Exception
		{
			new TestPlan()
			{

				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						DomainRepository mockDAO = mock(DomainRepository.class);
						doThrow(new RuntimeException()).when(mockDAO).findByDomainNameIgnoreCase(eq("test.com"));
						
						domainResource.setDomainRepository(mockDAO);
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
					
					domainResource.setDomainRepository(domainRepo);
				}
				
				
				@Override
				protected Domain getDomainToAdd()
				{
					return null;
				}
				
				@Override
				protected String getDomainNameToRemove()
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
		public void testRemoveDomain_errorInDelete_assertServerError() throws Exception
		{
			new TestPlan()
			{
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						DomainRepository mockDAO = mock(DomainRepository.class);
						when(mockDAO.findByDomainNameIgnoreCase((String)any())).thenReturn(new org.nhindirect.config.store.Domain());
						doThrow(new RuntimeException()).when(mockDAO).deleteByDomainNameIgnoreCase(eq("test.com"));
						
						domainResource.setDomainRepository(mockDAO);
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
					
					domainResource.setDomainRepository(domainRepo);
				}
				
				
				@Override
				protected Domain getDomainToAdd()
				{
					return null;
				}
				
				@Override
				protected String getDomainNameToRemove()
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

package org.nhind.config.rest.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.Address;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.repository.DomainRepository;


public class DefaultDomainService_addDomainTest extends SpringBaseTest
{
	abstract class TestPlan extends BaseTestPlan 
	{
		
		@Override
		protected void tearDownMocks()
		{

		}
		protected abstract Domain getDomainToAdd();
		
		@Override
		protected void performInner() throws Exception
		{				
			
			final Domain addDomain = getDomainToAdd();
		
			
			try
			{
				domainService.addDomain(addDomain);
			}
			catch (ServiceException e)
			{
				throw e;
			}
			
			try
			{
				final Domain getDomain = domainService.getDomain(addDomain.getDomainName());
				doAssertions(getDomain);
			}
			catch (ServiceException e)
			{
				throw e;
			}
			
		}
		
		
		protected void doAssertions(Domain domain) throws Exception
		{
			
		}
	}	
	
	@Test
	public void testAddDomain_addNewDomain_assertDomainCreated() throws Exception
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
			protected void doAssertions(Domain domain) throws Exception
			{
				assertNotNull(domain);
				assertEquals(this.domain.getDomainName(), domain.getDomainName());
				assertEquals(this.domain.getStatus(), domain.getStatus());
				assertEquals(this.domain.getPostmasterAddress().getEmailAddress(), domain.getPostmasterAddress().getEmailAddress());
			}
		}.perform();
	}
		
	@Test
	public void testAddDomain_addNewDomain_alreadyExists_assertConfilic() throws Exception
	{
		new TestPlan()
		{
			protected Domain domain;
			
			@Override
			protected void setupMocks()
			{
				super.setupMocks();
				
				org.nhindirect.config.store.Domain domain = new org.nhindirect.config.store.Domain();
				domain.setDomainName("test.com");
				domain.setStatus(org.nhindirect.config.store.EntityStatus.ENABLED.ordinal());
				domainRepo.save(domain).block();
				
			}
			
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
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(409, ex.getResponseCode());
			}
		}.perform();
	}	
	
	
	@Test
	public void testAddDomain_errorInLookup_assertServerError() throws Exception
	{
		new TestPlan()
		{
			protected Domain domain;
			
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					DomainRepository mockDAO = mock(DomainRepository.class);
					doThrow(new RuntimeException()).when(mockDAO).findByDomainNameIgnoreCase((String)any());
					
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
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.ENABLED);
				domain.setPostmasterAddress(postmasterAddress);			
				
				return domain;
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
	public void testAddDomain_errorInAdd_assertServerError() throws Exception
	{
		new TestPlan()
		{
			protected Domain domain;	
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					DomainRepository mockDAO = mock(DomainRepository.class);
					doThrow(new RuntimeException()).when(mockDAO).save((org.nhindirect.config.store.Domain)any());
					
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
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.ENABLED);
				domain.setPostmasterAddress(postmasterAddress);			
				
				return domain;
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

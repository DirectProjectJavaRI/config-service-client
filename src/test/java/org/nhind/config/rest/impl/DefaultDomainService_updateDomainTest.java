package org.nhind.config.rest.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.Address;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.repository.DomainRepository;

import reactor.core.publisher.Mono;


public class DefaultDomainService_updateDomainTest extends SpringBaseTest
{
	abstract class TestPlan extends BaseTestPlan 
	{
		@Override
		protected void tearDownMocks()
		{

		}
		protected abstract Domain getDomainToAdd();
		
		protected abstract Domain getDomainToUpdate();
		
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
				domainService.updateDomain(getDomainToUpdate());
			}
			catch (ServiceException e)
			{
				throw e;
			}
			
			try
			{
				final Domain getDomain = domainService.getDomain(getDomainToUpdate().getDomainName());
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
	public void testUpdateDomain_updateExistingDomain_assertDomainUpdated() throws Exception
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
			
			protected Domain getDomainToUpdate()
			{				
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.NEW);	
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
	public void testUpdateDomain_nonExistentDomain_assertNonFound() throws Exception
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
			
			protected Domain getDomainToUpdate()
			{				
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test2.com");
				
				domain = new Domain();
				
				domain.setDomainName("test2.com");
				domain.setStatus(EntityStatus.NEW);	
				domain.setPostmasterAddress(postmasterAddress);			
				
				return domain;
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
	public void testUpdateDomain_errorInDomain_assertServerError() throws Exception
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
			
			protected Domain getDomainToUpdate()
			{				
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.NEW);	
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
	public void testUpdateDomain_errorInUpdate_assertServerError() throws Exception
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
					
					final org.nhindirect.config.store.Domain dom = new org.nhindirect.config.store.Domain();
					dom.setDomainName("Test");
					
					when(mockDAO.findByDomainNameIgnoreCase((String)any())).thenReturn(Mono.just(dom));
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
				return null;
			}
			
			protected Domain getDomainToUpdate()
			{				
				final Address postmasterAddress = new Address();
				postmasterAddress.setEmailAddress("me@test.com");
				
				domain = new Domain();
				
				domain.setDomainName("test.com");
				domain.setStatus(EntityStatus.NEW);	
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

package org.nhind.config.rest.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.Address;
import org.nhindirect.config.repository.AddressRepository;
import org.nhindirect.config.repository.DomainRepository;

public class DefaultAddressService_updateAddressTest extends SpringBaseTest
{
	abstract class TestPlan extends BaseTestPlan 
	{
		@Override
		protected void tearDownMocks()
		{

		}
		
		protected abstract Address getAddressToAdd();
		
		protected abstract String getDomainToAdd();
		
		protected abstract Address getAddressToUpdate();
		
		@Override
		protected void performInner() throws Exception
		{				
			
			final Address addAddress = getAddressToAdd();
			final String domainName = getDomainToAdd();
			
			if (domainName != null && !domainName.isEmpty())
			{
				final org.nhindirect.config.store.Domain domain = new org.nhindirect.config.store.Domain();
				domain.setDomainName(domainName);
				domain.setStatus(org.nhindirect.config.store.EntityStatus.ENABLED.ordinal());
				domainRepo.save(domain).block();
				
				if (addAddress != null)
					addAddress.setDomainName(domainName);
			}
			
			if (addAddress != null)
			{
				try
				{
					addressService.addAddress(addAddress);
				}
				catch (ServiceException e)
				{
					throw e;
				}
			}
			
			try
			{
				addressService.updateAddress(getAddressToUpdate());
			}
			catch (ServiceException e)
			{
				throw e;
			}
			
			try
			{
				final Address getAddress = addressService.getAddress(getAddressToUpdate().getEmailAddress());
				doAssertions(getAddress);
			}
			catch (ServiceException e)
			{
				throw e;
			}
			
		}
		
		
		protected void doAssertions(Address address) throws Exception
		{
			
		}
	}		
	
	@Test
	public void testUpdateAddress_updateExistingAddress_assertAddressUpdated() throws Exception
	{
		new TestPlan()
		{
			protected Address address;
			
			@Override
			protected  Address getAddressToAdd()
			{
				address = new Address();
				
				address.setEmailAddress("me@test.com");
				address.setType("email");
				address.setEndpoint("none");
				address.setDisplayName("me");
				
				return address;
			}
			
			@Override
			protected String getDomainToAdd()
			{
				return "test.com";
			}
			
			protected Address getAddressToUpdate()
			{
				address = new Address();
				
				address.setEmailAddress("me@test.com");
				address.setType("XD");
				address.setEndpoint("http://you.me.com");
				address.setDisplayName("me");
				address.setDomainName(getDomainToAdd());
				
				return address;
			}
			
			@Override
			protected void doAssertions(Address address) throws Exception
			{
				assertNotNull(address);
				assertEquals(this.address.getEmailAddress(), address.getEmailAddress());
				assertEquals(this.address.getType(), address.getType());
				assertEquals(this.address.getEndpoint(), address.getEndpoint());
				assertEquals(this.address.getDisplayName(), address.getDisplayName());
				assertEquals(this.address.getDomainName(), address.getDomainName());
			}
		}.perform();
	}	
	
	@Test
	public void testUpdateAddress_nonExistentDomain_assertNotFound() throws Exception
	{
		new TestPlan()
		{
			protected Address address;
			
			@Override
			protected  Address getAddressToAdd()
			{
				address = new Address();
				
				address.setEmailAddress("me@test.com");
				address.setType("email");
				address.setEndpoint("none");
				address.setDisplayName("me");
				
				return address;
			}
			
			@Override
			protected String getDomainToAdd()
			{
				return "test.com";
			}
			
			protected Address getAddressToUpdate()
			{
				address = new Address();
				
				address.setEmailAddress("me@test.com");
				address.setType("XD");
				address.setEndpoint("http://you.me.com");
				address.setDisplayName("me");
				address.setDomainName("test2.com");
				
				return address;
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
	public void testUpdateAddress_nonExistentAddress_assertNotFound() throws Exception
	{
		new TestPlan()
		{
			protected Address address;
			
			@Override
			protected  Address getAddressToAdd()
			{
				address = new Address();
				
				address.setEmailAddress("me@test.com");
				address.setType("email");
				address.setEndpoint("none");
				address.setDisplayName("me");
				
				return address;
			}
			
			@Override
			protected String getDomainToAdd()
			{
				return "test.com";
			}
			
			protected Address getAddressToUpdate()
			{
				address = new Address();
				
				address.setEmailAddress("me2@test.com");
				address.setType("XD");
				address.setEndpoint("http://you.me.com");
				address.setDisplayName("me");
				address.setDomainName(getDomainToAdd());
				
				return address;
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
	public void testUpdateAddress_emptyDomainName_assertBadRequest() throws Exception
	{
		new TestPlan()
		{
			protected Address address;
			
			@Override
			protected  Address getAddressToAdd()
			{
				address = new Address();
				
				address.setEmailAddress("me@test.com");
				address.setType("email");
				address.setEndpoint("none");
				address.setDisplayName("me");
				
				return address;
			}
			
			@Override
			protected String getDomainToAdd()
			{
				return "test.com";
			}
			
			protected Address getAddressToUpdate()
			{
				address = new Address();
				
				address.setEmailAddress("me2@test.com");
				address.setType("XD");
				address.setEndpoint("http://you.me.com");
				address.setDisplayName("me");
				address.setDomainName("");
				
				return address;
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
	public void testUpdateAddress_nullDomainName_assertBadRequest() throws Exception
	{
		new TestPlan()
		{
			protected Address address;
			
			@Override
			protected  Address getAddressToAdd()
			{
				address = new Address();
				
				address.setEmailAddress("me@test.com");
				address.setType("email");
				address.setEndpoint("none");
				address.setDisplayName("me");
				
				return address;
			}
			
			@Override
			protected String getDomainToAdd()
			{
				return "test.com";
			}
			
			protected Address getAddressToUpdate()
			{
				address = new Address();
				
				address.setEmailAddress("me2@test.com");
				address.setType("XD");
				address.setEndpoint("http://you.me.com");
				address.setDisplayName("me");
				address.setDomainName(null);
				
				return address;
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
	public void testRemoveAddress_nonErrorInDomainLookup_assertServerError() throws Exception
	{
		new TestPlan()
		{
			protected Address address;
			
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();

					DomainRepository mockDAO = mock(DomainRepository.class);
					doThrow(new RuntimeException()).when(mockDAO).findByDomainNameIgnoreCase(eq("test.com"));
					
					addressResource.setDomainRepository(mockDAO);
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
				
				addressResource.setDomainRepository(domainRepo);
			}
			
			@Override
			protected  Address getAddressToAdd()
			{
				
				return null;
			}
			
			@Override
			protected String getDomainToAdd()
			{
				return null;
			}
			
			@Override
			protected Address getAddressToUpdate()
			{
				address = new Address();
				
				address.setEmailAddress("me@test.com");
				address.setType("XD");
				address.setEndpoint("http://you.me.com");
				address.setDisplayName("me");
				address.setDomainName("test.com");
				
				return address;
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
	public void testRemoveAddress_nonErrorInAddressLookup_assertServerError() throws Exception
	{
		new TestPlan()
		{
			protected Address address;
			
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					AddressRepository mockDAO = mock(AddressRepository.class);
					doThrow(new RuntimeException()).when(mockDAO).findByEmailAddressIgnoreCase(eq("me@test.com"));
					
					addressResource.setAddressRepository(mockDAO);
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
				
				addressResource.setAddressRepository(addressRepo);
			}
			
			@Override
			protected  Address getAddressToAdd()
			{
				
				return null;
			}
			
			@Override
			protected String getDomainToAdd()
			{
				return "test.com";
			}
			
			@Override
			protected Address getAddressToUpdate()
			{
				address = new Address();
				
				address.setEmailAddress("me@test.com");
				address.setType("XD");
				address.setEndpoint("http://you.me.com");
				address.setDisplayName("me");
				address.setDomainName("test.com");
				
				return address;
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

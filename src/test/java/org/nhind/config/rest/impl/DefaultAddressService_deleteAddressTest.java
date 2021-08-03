package org.nhind.config.rest.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

public class DefaultAddressService_deleteAddressTest extends SpringBaseTest
{

		abstract class TestPlan extends BaseTestPlan 
		{
			
			@Override
			protected void tearDownMocks()
			{

			}
			
			protected abstract Address getAddressToAdd();
			
			protected abstract String getDomainToAdd();
			
			protected abstract String getAddressNameToRemove();
			
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
					addressService.deleteAddress(getAddressNameToRemove());
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
		public void testRemoveAddress_removeExistingAddress_assertAddressRemoved() throws Exception
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
				
				@Override
				protected String getAddressNameToRemove()
				{
					return "me@test.com";
				}
				
				@Override
				protected void doAssertions() throws Exception
				{
					assertNull(addressRepo.findByEmailAddressIgnoreCase("me@test.com").block());
				}
			}.perform();
		}	
		
		@Test
		public void testRemoveAddress_nonExistentAddress_assertNotFound() throws Exception
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
				
				@Override
				protected String getAddressNameToRemove()
				{
					return "me@test2.com";
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
		public void testRemoveAddress_nonErrorInLookup_assertServerError() throws Exception
		{
			new TestPlan()
			{

				
				@Override
				protected void setupMocks()
				{
					try
					{
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
				protected String getAddressNameToRemove()
				{
					return "me@test.com";
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

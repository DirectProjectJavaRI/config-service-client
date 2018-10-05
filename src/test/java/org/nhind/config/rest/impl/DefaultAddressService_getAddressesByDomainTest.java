package org.nhind.config.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;


import org.junit.Test;
import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.Address;
import org.nhindirect.config.repository.AddressRepository;
import org.nhindirect.config.repository.DomainRepository;


public class DefaultAddressService_getAddressesByDomainTest extends SpringBaseTest
{
		abstract class TestPlan extends BaseTestPlan 
		{			
			@Override
			protected void tearDownMocks()
			{

			}

			protected abstract Address getAddressToAdd();
			
			protected abstract String getDomainToAdd();
			
			protected abstract String getDomainNameToGet();
			
			@Override
			protected void performInner() throws Exception
			{				
				
				final Address addAddress = getAddressToAdd();
				final String domainName = getDomainToAdd();
				
				if (domainName != null && !domainName.isEmpty())
				{
					final org.nhindirect.config.store.Domain domain = new org.nhindirect.config.store.Domain();
					domain.setDomainName(domainName);
					domain.setStatus(org.nhindirect.config.store.EntityStatus.ENABLED);
					domainRepo.save(domain);
					
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
					final Collection<Address> getAddresses = addressService.getAddressesByDomain(getDomainNameToGet());
					doAssertions(getAddresses);
				}
				catch (ServiceMethodException e)
				{
					
					if (e.getResponseCode() == 404 || e.getResponseCode() == 204)
						doAssertions(new ArrayList<Address>());
					else
						throw e;
				}
				
			}
			
			
			protected void doAssertions(Collection<Address> addresses) throws Exception
			{
				
			}
		}	
		
		@Test
		public void testGetAddresseseByDomain_getExistingAddress_assertAddressRetrieved() throws Exception
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
				protected String getDomainNameToGet()
				{
					return "test.com";
				}
				
				@Override
				protected void doAssertions(Collection<Address> addresses) throws Exception
				{
					
					
					assertNotNull(addresses);
					assertEquals(1, addresses.size());
					final Address address = addresses.iterator().next();
					
					assertEquals(this.address.getEmailAddress(), address.getEmailAddress());
					assertEquals(this.address.getType(), address.getType());
					assertEquals(this.address.getEndpoint(), address.getEndpoint());
					assertEquals(this.address.getDisplayName(), address.getDisplayName());
					assertEquals(this.address.getDomainName(), address.getDomainName());
				}
			}.perform();
		}		
		
		@Test
		public void testGetAddressesByDomain_nonExistentDomain_assertNull() throws Exception
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
				protected String getDomainNameToGet()
				{
					return "test2.com";
				}
				
				@Override
				protected void doAssertions(Collection<Address> addresses) throws Exception
				{
					assertTrue(addresses.isEmpty());
				}
			}.perform();
		}	
		
		@Test
		public void testGetAddressesByDomain_nonExistentAddress_assertNull() throws Exception
		{
			new TestPlan()
			{
				
				@Override
				protected  Address getAddressToAdd()
				{
					return null;
				}
				
				@Override
				protected String getDomainToAdd()
				{
					return "test2.com";
				}
				
				@Override
				protected String getDomainNameToGet()
				{
					return "test2.com";
				}
				
				@Override
				protected void doAssertions(Collection<Address> addresses) throws Exception
				{
					assertTrue(addresses.isEmpty());
				}
			}.perform();
		}	
		
		@Test
		public void testGetAddress_errorInDomainLookup_assertServerError() throws Exception
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
						doThrow(new RuntimeException()).when(mockDAO).findByDomainNameIgnoreCase(eq("blowup.com"));
						
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
				protected String getDomainNameToGet()
				{
					return "blowup.com";
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
		public void testGetAddress_errorInAddressLookup_assertServerError() throws Exception
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
						doThrow(new RuntimeException()).when(mockDAO).findByDomain((org.nhindirect.config.store.Domain)any());
						
						addressResource.setAddressRepository(mockDAO);					}
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
				protected String getDomainNameToGet()
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
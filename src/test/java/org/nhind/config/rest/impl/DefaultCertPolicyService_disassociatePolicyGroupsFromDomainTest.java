package org.nhind.config.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import org.nhindirect.config.model.CertPolicy;
import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.repository.CertPolicyGroupDomainReltnRepository;
import org.nhindirect.config.repository.DomainRepository;


public class DefaultCertPolicyService_disassociatePolicyGroupsFromDomainTest extends SpringBaseTest
{
	
	abstract class TestPlan extends BaseTestPlan 
	{
		protected Collection<CertPolicyGroup> groups;
		
		protected Collection<CertPolicy> policies;
		
		@Override
		protected void tearDownMocks()
		{

		}
		
		
		protected  Collection<CertPolicyGroup> getGroupsToAdd()
		{
			try
			{
				groups = new ArrayList<CertPolicyGroup>();
				
				CertPolicyGroup group = new CertPolicyGroup();
				group.setPolicyGroupName("Group1");
				groups.add(group);
				
				group = new CertPolicyGroup();
				group.setPolicyGroupName("Group2");
				groups.add(group);
				
				return groups;
			}
			catch (Exception e)
			{
				throw new RuntimeException (e);
			}
		}
		
		
		protected  Domain getDomainToAdd()
		{
			final Address postmasterAddress = new Address();
			postmasterAddress.setEmailAddress("me@test.com");
			
			Domain domain = new Domain();
			
			domain.setDomainName("test.com");
			domain.setStatus(EntityStatus.ENABLED);
			domain.setPostmasterAddress(postmasterAddress);			
			
			return domain;
		}
		
		
		
		protected  String getGroupNameToAssociate()
		{
			return "Group1";
		}
		
		protected  String getDomainNameToAssociate()
		{
			return "test.com";
		}
		
		protected abstract String getDomainNameToDisassociate();
		
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
			
			final Collection<CertPolicyGroup> groupsToAdd = getGroupsToAdd();
			
			if (groupsToAdd != null)
			{
				for (CertPolicyGroup addGroup : groupsToAdd)
				{
					try
					{
						certPolService.addPolicyGroup(addGroup);
					}
					catch (ServiceException e)
					{
						throw e;
					}
				}
			}
			

			// associate the bundle and domain
			if (groupsToAdd != null && addDomain != null)
				certPolService.associatePolicyGroupToDomain(getGroupNameToAssociate(), getDomainNameToAssociate());
			
			// disassociate
			certPolService.disassociatePolicyGroupsFromDomain(getDomainNameToDisassociate());

			doAssertions();
			
		}
			
		protected void doAssertions() throws Exception
		{
			
		}
	}	
	
	@Test
	public void testDisassociatePolicyGroupsFromDomain_assertGroupDomainDisassociated()  throws Exception
	{
		new TestPlan()
		{
			
			@Override
			protected String getDomainNameToDisassociate()
			{
				return "test.com";
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				final org.nhindirect.config.store.Domain domain = domainRepo.findByDomainNameIgnoreCase(getDomainNameToAssociate());
				
				final Collection<org.nhindirect.config.store.CertPolicyGroupDomainReltn> reltns = groupReltnRepo.findByDomain(domain);
				
				assertEquals(0, reltns.size());
			}
		}.perform();
	}	
	
	@Test
	public void testDisassociatePolicyGroupsFromDomain_unknownDomain_assertNotFound()  throws Exception
	{
		new TestPlan()
		{

			@Override
			protected String getDomainNameToDisassociate()
			{
				return "test.com1";
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
	public void testDisassociatePolicyGroupFromDomain_errorInDomainLookup_assertServiceError()  throws Exception
	{
		new TestPlan()
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					DomainRepository mockDomainDAO = mock(DomainRepository.class);

					doThrow(new RuntimeException()).when(mockDomainDAO).findByDomainNameIgnoreCase((String)any());
					
					certPolResource.setDomainRepository(mockDomainDAO);
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
				
				certPolResource.setDomainRepository(domainRepo);
			}
			
			@Override
			protected  Collection<CertPolicyGroup> getGroupsToAdd()
			{
				return null;
			}
			
			@Override
			protected  Domain getDomainToAdd()
			{
				return null;
			}
			
			@Override
			protected String getDomainNameToDisassociate()
			{
				return "test.com1";
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
	public void testDisassociatePolicyGroupsFromDomain_errorInDisassociate_assertServiceError()  throws Exception
	{
		new TestPlan()
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					CertPolicyGroupDomainReltnRepository mockReltnDAO = mock(CertPolicyGroupDomainReltnRepository.class);
					DomainRepository mockDomainDAO = mock(DomainRepository.class);
					
					when(mockDomainDAO.findByDomainNameIgnoreCase("test.com")).thenReturn(new org.nhindirect.config.store.Domain());
					doThrow(new RuntimeException()).when(mockReltnDAO).deleteByDomain((org.nhindirect.config.store.Domain)any());
					
					certPolResource.setDomainRepository(mockDomainDAO);
					certPolResource.setCertPolicyGroupDomainReltnRepository(mockReltnDAO);
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
				
				certPolResource.setDomainRepository(domainRepo);
				certPolResource.setCertPolicyGroupDomainReltnRepository(groupReltnRepo);
			}
			
			@Override
			protected  Collection<CertPolicyGroup> getGroupsToAdd()
			{
				return null;
			}
			
			@Override
			protected  Domain getDomainToAdd()
			{
				return null;
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

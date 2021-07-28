package org.nhind.config.rest.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

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

import reactor.core.publisher.Mono;

public class DefaultCertPolicyService_getPolicyGroupsByDomainTest extends SpringBaseTest
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
		
		protected abstract String getGroupNameToAssociate();
		
		protected abstract String getDomainNameToAssociate();
		
		protected abstract String getDomainNameToLookup();
		
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
			
			if (addDomain != null & groupsToAdd != null)
				certPolService.associatePolicyGroupToDomain(getGroupNameToAssociate(), getDomainNameToAssociate());
			
			try
			{
				final Collection<CertPolicyGroup> getGroups = certPolService.getPolicyGroupsByDomain(getDomainNameToLookup());
				doAssertions(getGroups);
			}
			catch (ServiceMethodException e)
			{
				if (e.getResponseCode() == 204)
					doAssertions(new ArrayList<CertPolicyGroup>());
				else
					throw e;
			}
			
			
		}
			
		protected void doAssertions(Collection<CertPolicyGroup> groups) throws Exception
		{
			
		}
	}	
	
	@Test
	public void testGetPolicyGroupsByDomain_assertGroupsRetrieved()  throws Exception
	{
		new TestPlan()
		{

			
			@Override
			protected  String getGroupNameToAssociate()
			{
				return "Group1";
			}
			
			@Override
			protected  String getDomainNameToAssociate()
			{
				return "test.com";
			}
			
			@Override 
			protected String getDomainNameToLookup()
			{
				return "test.com";
						
			}
			
			@Override
			protected void doAssertions(Collection<CertPolicyGroup> groups) throws Exception
			{
				
				assertNotNull(groups);
				
				assertEquals(1, groups.size());
				final CertPolicyGroup group = groups.iterator().next();
				
				assertEquals("Group1", group.getPolicyGroupName());
				
			}
		}.perform();
	}		
	
	@Test
	public void testGetPolicyGroupsByDomain_noGroupsInDomain_assertNoGroupsRetrieved()  throws Exception
	{
		new TestPlan()
		{

			@Override
			protected  Collection<CertPolicyGroup> getGroupsToAdd()
			{
				return null;
			}
			
			@Override
			protected  String getGroupNameToAssociate()
			{
				return "Group1";
			}
			
			@Override
			protected  String getDomainNameToAssociate()
			{
				return "test.com";
			}
			
			@Override 
			protected String getDomainNameToLookup()
			{
				return "test.com";
						
			}
			
			@Override
			protected void doAssertions(Collection<CertPolicyGroup> groups) throws Exception
			{
				
				assertNotNull(groups);
				
				assertEquals(0, groups.size());
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetPolicyGroupsByDomain_domainNotFound_assertNotFound()  throws Exception
	{
		new TestPlan()
		{

			@Override
			protected  Collection<CertPolicyGroup> getGroupsToAdd()
			{
				return null;
			}
			
			@Override
			protected  String getGroupNameToAssociate()
			{
				return "Group1";
			}
			
			@Override
			protected  String getDomainNameToAssociate()
			{
				return "test.com";
			}
			
			@Override 
			protected String getDomainNameToLookup()
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
	public void testGetPolicyGroupsByDomain_errorInDomainLookup_assertServiceError()  throws Exception
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
			protected  Domain getDomainToAdd()
			{
				return null;
			}
			
			@Override
			protected  Collection<CertPolicyGroup> getGroupsToAdd()
			{
				return null;
			}
			
			@Override
			protected  String getGroupNameToAssociate()
			{
				return "Group1";
			}
			
			@Override
			protected  String getDomainNameToAssociate()
			{
				return "test.com";
			}
			
			@Override 
			protected String getDomainNameToLookup()
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
	public void testGetPolicyGroupsByDomain_errorInPolicyGroupLookup_assertServiceError()  throws Exception
	{
		new TestPlan()
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					CertPolicyGroupDomainReltnRepository mockPolicyDAO = mock(CertPolicyGroupDomainReltnRepository.class);
					DomainRepository mockDomainDAO = mock(DomainRepository.class);
					
					final org.nhindirect.config.store.Domain dom = new org.nhindirect.config.store.Domain();
					dom.setDomainName("Test");
					
					when(mockDomainDAO.findByDomainNameIgnoreCase((String)any())).thenReturn(Mono.just(dom));
					doThrow(new RuntimeException()).when(mockPolicyDAO).findByDomainId(any());
					
					certPolResource.setCertPolicyGroupDomainReltnRepository(mockPolicyDAO);
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
				
				certPolResource.setCertPolicyGroupDomainReltnRepository(groupDomainReltnRepo);
				certPolResource.setDomainRepository(domainRepo);
			}
			
			@Override
			protected  Domain getDomainToAdd()
			{
				return null;
			}
			
			@Override
			protected  Collection<CertPolicyGroup> getGroupsToAdd()
			{
				return null;
			}
			
			@Override
			protected  String getGroupNameToAssociate()
			{
				return "Group1";
			}
			
			@Override
			protected  String getDomainNameToAssociate()
			{
				return "test.com";
			}
			
			@Override 
			protected String getDomainNameToLookup()
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
}
	

package org.nhind.config.rest.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
				final org.nhindirect.config.store.Domain domain = domainRepo.findByDomainNameIgnoreCase(getDomainNameToAssociate()).block();
				
				final Collection<org.nhindirect.config.store.CertPolicyGroupDomainReltn> reltns = groupDomainReltnRepo.findByDomainId(domain.getId()).collectList().block();
				
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
					
					final org.nhindirect.config.store.Domain dom = new org.nhindirect.config.store.Domain();
					dom.setDomainName("Test");
					
					when(mockDomainDAO.findByDomainNameIgnoreCase("test.com")).thenReturn(Mono.just(dom));
					doThrow(new RuntimeException()).when(mockReltnDAO).deleteByDomainId(any());
					
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
				certPolResource.setCertPolicyGroupDomainReltnRepository(groupDomainReltnRepo);
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

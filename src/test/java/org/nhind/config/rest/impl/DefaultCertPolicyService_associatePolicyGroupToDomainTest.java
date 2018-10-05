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
import org.nhindirect.config.repository.CertPolicyGroupRepository;
import org.nhindirect.config.repository.DomainRepository;


public class DefaultCertPolicyService_associatePolicyGroupToDomainTest extends SpringBaseTest
{
	abstract class TestPlan extends BaseTestPlan 
	{
		protected Collection<CertPolicyGroup> groups;
		
		protected Collection<CertPolicy> policies;
		
		
		@Override
		protected void tearDownMocks()
		{

		}
		
		protected abstract Collection<CertPolicyGroup> getGroupsToAdd();
		
		protected abstract Domain getDomainToAdd();
		
		protected abstract String getGroupNameToAssociate();
		
		protected abstract String getDomainNameToAssociate();
		
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
			

			certPolService.associatePolicyGroupToDomain(getGroupNameToAssociate(), getDomainNameToAssociate());
			doAssertions();
			
		}
			
		protected void doAssertions() throws Exception
		{
			
		}
	}	
	
	@Test
	public void testAssociatePolicyGroupToDomain_assertAssociate()  throws Exception
	{
		new TestPlan()
		{

			@Override
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
			
			@Override
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
			protected void doAssertions() throws Exception
			{
				final org.nhindirect.config.store.Domain domain = domainRepo.findByDomainNameIgnoreCase(getDomainNameToAssociate());
				
				final Collection<org.nhindirect.config.store.CertPolicyGroupDomainReltn> reltns = groupReltnRepo.findByDomain(domain);
				
				assertEquals(1, reltns.size());
				
				final org.nhindirect.config.store.CertPolicyGroupDomainReltn reltn = reltns.iterator().next();
				
				assertEquals(getGroupNameToAssociate(), reltn.getCertPolicyGroup().getPolicyGroupName());
				assertEquals(getDomainNameToAssociate(), reltn.getDomain().getDomainName());
			}
		}.perform();
	}	
	
	@Test
	public void testAssociatePolicyGroupToDomain_policyNotFound_assertNotFound()  throws Exception
	{
		new TestPlan()
		{

			@Override
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
			
			@Override
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
			
			
			@Override
			protected  String getGroupNameToAssociate()
			{
				return "Group4";
			}
			
			@Override
			protected  String getDomainNameToAssociate()
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
	public void testAssociatePolicyGroupToDomain_domainNotFound_assertNotFound()  throws Exception
	{
		new TestPlan()
		{

			@Override
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
			
			@Override
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
			
			
			@Override
			protected  String getGroupNameToAssociate()
			{
				return "Group1";
			}
			
			@Override
			protected  String getDomainNameToAssociate()
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
	public void testAssociatePolicyGroupToDomain_errorInGroupLookup_assertServiceError()  throws Exception
	{
		new TestPlan()
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();

					CertPolicyGroupRepository mockDAO = mock(CertPolicyGroupRepository.class);
					doThrow(new RuntimeException()).when(mockDAO).findByPolicyGroupNameIgnoreCase((String)any());
					
					certPolResource.setCertPolicyGroupRepository(mockDAO);
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
				
				certPolResource.setCertPolicyGroupRepository(policyGroupRepo);
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
			protected  String getGroupNameToAssociate()
			{
				return "Group1";
			}
			
			@Override
			protected  String getDomainNameToAssociate()
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
	public void testAssociatePolicyGroupToDomain_errorInDomainLookup_assertServiceError()  throws Exception
	{
		new TestPlan()
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					CertPolicyGroupRepository mockPolicyDAO = mock(CertPolicyGroupRepository.class);
					DomainRepository mockDomainDAO = mock(DomainRepository.class);
					
					when(mockPolicyDAO.findByPolicyGroupNameIgnoreCase("Group1")).thenReturn(new org.nhindirect.config.store.CertPolicyGroup());
					doThrow(new RuntimeException()).when(mockDomainDAO).findByDomainNameIgnoreCase((String)any());
					
					certPolResource.setCertPolicyGroupRepository(mockPolicyDAO);
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
				
				certPolResource.setCertPolicyGroupRepository(policyGroupRepo);
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
			protected  String getGroupNameToAssociate()
			{
				return "Group1";
			}
			
			@Override
			protected  String getDomainNameToAssociate()
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
	public void testAssociatePolicyGroupToDomain_errorInAssoicate_assertServiceError()  throws Exception
	{
		new TestPlan()
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					CertPolicyGroupRepository mockPolicyDAO = mock(CertPolicyGroupRepository.class);
					DomainRepository mockDomainDAO = mock(DomainRepository.class);
					CertPolicyGroupDomainReltnRepository mockReltn = mock(CertPolicyGroupDomainReltnRepository.class);
					
					when(mockPolicyDAO.findByPolicyGroupNameIgnoreCase("Group1")).thenReturn(new org.nhindirect.config.store.CertPolicyGroup());
					when(mockDomainDAO.findByDomainNameIgnoreCase("test.com")).thenReturn(new org.nhindirect.config.store.Domain());
					doThrow(new RuntimeException()).when(mockReltn).save((org.nhindirect.config.store.CertPolicyGroupDomainReltn)any());
					
					certPolResource.setCertPolicyGroupRepository(mockPolicyDAO);
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
				
				certPolResource.setCertPolicyGroupRepository(policyGroupRepo);
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
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(500, ex.getResponseCode());
			}
		}.perform();
	}			
}

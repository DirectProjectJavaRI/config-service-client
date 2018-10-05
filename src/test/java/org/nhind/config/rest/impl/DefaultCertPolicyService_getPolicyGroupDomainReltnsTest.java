package org.nhind.config.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import org.nhindirect.config.model.CertPolicy;
import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.model.CertPolicyGroupDomainReltn;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.repository.CertPolicyGroupDomainReltnRepository;

public class DefaultCertPolicyService_getPolicyGroupDomainReltnsTest extends SpringBaseTest
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
			
			if (addDomain != null & groupsToAdd != null)
				certPolService.associatePolicyGroupToDomain(getGroupNameToAssociate(), getDomainNameToAssociate());
			
			try
			{
				final Collection<CertPolicyGroupDomainReltn> getReltns = certPolService.getPolicyGroupDomainReltns();
				doAssertions(getReltns);
			}
			catch (ServiceMethodException e)
			{
				if (e.getResponseCode() == 204)
					doAssertions(new ArrayList<CertPolicyGroupDomainReltn>());
				else
					throw e;
			}
			
			
		}
			
		protected void doAssertions(Collection<CertPolicyGroupDomainReltn> reltns) throws Exception
		{
			
		}
	}	
	
	@Test
	public void testGetPolicyGroupDomainReltns_assertReltnsRetrieved()  throws Exception
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
			protected void doAssertions(Collection<CertPolicyGroupDomainReltn> reltns) throws Exception
			{
				
				assertNotNull(reltns);
				
				assertEquals(1, reltns.size());
				
				final CertPolicyGroupDomainReltn reltn = reltns.iterator().next();
				
				assertEquals("test.com", reltn.getDomain().getDomainName());
				assertEquals("Group1", reltn.getPolicyGroup().getPolicyGroupName());
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetPolicyGroupDomainReltns_noReltnsInStore_assertNoReltnsRetrieved()  throws Exception
	{
		new TestPlan()
		{

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
			protected void doAssertions(Collection<CertPolicyGroupDomainReltn> reltns) throws Exception
			{
				
				assertNotNull(reltns);
				
				assertEquals(0, reltns.size());
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetPolicyGroupDomainReltns_errorInLookup_assertServiceError()  throws Exception
	{
		new TestPlan()
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					CertPolicyGroupDomainReltnRepository mockDAO = mock(CertPolicyGroupDomainReltnRepository.class);
					doThrow(new RuntimeException()).when(mockDAO).findAll();
					
					certPolResource.setCertPolicyGroupDomainReltnRepository(mockDAO);
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

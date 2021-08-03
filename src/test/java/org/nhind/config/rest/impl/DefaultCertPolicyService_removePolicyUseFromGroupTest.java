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
import java.util.Arrays;
import java.util.Collection;

import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.CertPolicy;
import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.model.CertPolicyGroupUse;
import org.nhindirect.config.model.CertPolicyUse;
import org.nhindirect.config.repository.CertPolicyGroupDomainReltnRepository;
import org.nhindirect.config.repository.CertPolicyGroupReltnRepository;
import org.nhindirect.config.repository.CertPolicyGroupRepository;
import org.nhindirect.policy.PolicyLexicon;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class DefaultCertPolicyService_removePolicyUseFromGroupTest extends SpringBaseTest
{
	abstract class TestPlan extends BaseTestPlan 
	{
		protected Collection<CertPolicyGroup> groups;
		
		protected Collection<CertPolicy> policies;
		
		@Override
		protected void tearDownMocks()
		{

		}
		
		protected Collection<CertPolicyGroup> getGroupsToAdd()
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
		
		protected Collection<CertPolicy> getPoliciesToAdd()
		{
			try
			{
				policies = new ArrayList<CertPolicy>();
				
				CertPolicy policy = new CertPolicy();
				policy.setPolicyName("Policy1");
				policy.setPolicyData(new byte[] {1,2,3});
				policy.setLexicon(PolicyLexicon.SIMPLE_TEXT_V1);
				policies.add(policy);
				
				policy = new CertPolicy();
				policy.setPolicyName("Policy2");
				policy.setPolicyData(new byte[] {1,2,5,6});
				policy.setLexicon(PolicyLexicon.JAVA_SER);
				policies.add(policy);
				
				return policies;
			}
			catch (Exception e)
			{
				throw new RuntimeException (e);
			}
		}
		
		protected String getGroupNameToAssociate()
		{
			return "Group1";
		}
		
		protected CertPolicyGroupUse getPolicyUseToAssociate()
		{
			final CertPolicyGroupUse use = new CertPolicyGroupUse();
			
			use.setIncoming(true);
			use.setOutgoing(true);
			use.setPolicyUse(CertPolicyUse.TRUST);
			use.setPolicy(policies.iterator().next());
			
			return use;
		}
		
		protected abstract String getGroupToRemoveFrom();
		
		protected abstract CertPolicyGroupUse getPolicyUseToRemove();
		
		@Override
		protected void performInner() throws Exception
		{				
			
			final Collection<CertPolicy> policiesToAdd = getPoliciesToAdd();
			
			if (policiesToAdd != null)
			{
				for (CertPolicy addPolicy : policiesToAdd)
				{
					try
					{
						certPolService.addPolicy(addPolicy);
						
					}
					catch (ServiceException e)
					{
						throw e;
					}
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
			
			// add policy to group
			if (groupsToAdd != null && policiesToAdd != null)
				certPolService.addPolicyUseToGroup(getGroupNameToAssociate(), getPolicyUseToAssociate());
			
			// remove policy from group
			certPolService.removePolicyUseFromGroup(getGroupToRemoveFrom(), getPolicyUseToRemove());
			
			// get the group
			final CertPolicyGroup getGroup = certPolService.getPolicyGroup(getGroupNameToAssociate());
			doAssertions(getGroup);
		}
			
		protected void doAssertions(CertPolicyGroup group) throws Exception
		{
			
		}
	}	
	
	@Test
	public void testRemovePolicyUseFromGroup_assertPolicyRemoved()  throws Exception
	{
		new TestPlan()
		{

			@Override
			protected String getGroupToRemoveFrom()
			{
				return "Group1";
			}
			
			@Override
			protected CertPolicyGroupUse getPolicyUseToRemove()
			{
				return getPolicyUseToAssociate();
			}
			
			@Override
			protected void doAssertions(CertPolicyGroup group) throws Exception
			{
				
				assertNotNull(group);
				
				assertEquals(getGroupNameToAssociate(), group.getPolicyGroupName());
				assertEquals(0, group.getPolicies().size());
				
			}
		}.perform();
	}	
	
	@Test
	public void testRemovePolicyUseFromGroup_nonExistantGroup_assertNotFound()  throws Exception
	{
		new TestPlan()
		{

			@Override
			protected String getGroupToRemoveFrom()
			{
				return "Group3";
			}
			
			@Override
			protected CertPolicyGroupUse getPolicyUseToRemove()
			{
				return getPolicyUseToAssociate();
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
	public void testRemovePolicyUseFromGroup_errorInGroupLookup_assertServiceError()  throws Exception
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
			protected Collection<CertPolicyGroup> getGroupsToAdd()
			{
				return null;
			}
			
			@Override
			protected Collection<CertPolicy> getPoliciesToAdd()
			{
				return null;
			}
			
			@Override
			protected void tearDownMocks()
			{
				super.tearDownMocks();
				
				certPolResource.setCertPolicyGroupRepository(policyGroupRepo);
			}
			
			@Override
			protected String getGroupToRemoveFrom()
			{
				return "Group1";
			}
			
			@Override
			protected CertPolicyGroupUse getPolicyUseToRemove()
			{
				final CertPolicyGroupUse use = new CertPolicyGroupUse();
				
				final CertPolicy policy = new CertPolicy();
				policy.setPolicyName("Policy1");
				policy.setPolicyData(new byte[] {1,2,3});
				policy.setLexicon(PolicyLexicon.SIMPLE_TEXT_V1);
				
				use.setIncoming(true);
				use.setOutgoing(true);
				use.setPolicyUse(CertPolicyUse.PUBLIC_RESOLVER);
				use.setPolicy(policy);
				
				return use;
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
	public void testRemovePolicyUseFromGroup_errorInRemove_assertServiceError()  throws Exception
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
					CertPolicyGroupReltnRepository mockReltnDAO = mock(CertPolicyGroupReltnRepository.class);
					CertPolicyGroupDomainReltnRepository mockReltn = mock(CertPolicyGroupDomainReltnRepository.class);
					
					final org.nhindirect.config.store.CertPolicy policy = new org.nhindirect.config.store.CertPolicy();
					policy.setPolicyName("Policy1");
					
					final org.nhindirect.config.store.CertPolicyGroupReltn reltn = new org.nhindirect.config.store.CertPolicyGroupReltn();
					reltn.setIncoming(true);
					reltn.setOutgoing(true);
					reltn.setPolicyUse(org.nhindirect.config.store.CertPolicyUse.TRUST.ordinal());
					reltn.setPolicyId(policy.getId());
					
					
					final org.nhindirect.config.store.CertPolicyGroup group = new org.nhindirect.config.store.CertPolicyGroup();
					reltn.setPolicyGroupId(group.getId());
					group.setPolicyGroupName("Group1");
					
					
					when(mockReltnDAO.findByPolicyGroupId(any())).thenReturn(Flux.fromIterable(Arrays.asList(reltn)));
					when(mockDAO.findByPolicyGroupNameIgnoreCase(any())).thenReturn(Mono.just(group));
					doThrow(new RuntimeException()).when(mockDAO).save(any());
					
					certPolResource.setCertPolicyGroupRepository(mockDAO);
					certPolResource.setCertPolicyGroupDomainReltnRepository(mockReltn);
					certPolResource.setCertPolicyGroupReltnRepository(mockReltnDAO);
				}
				catch (Throwable t)
				{
					throw new RuntimeException(t);
				}
			}
			
			@Override
			protected Collection<CertPolicyGroup> getGroupsToAdd()
			{
				return null;
			}
			
			@Override
			protected Collection<CertPolicy> getPoliciesToAdd()
			{
				return null;
			}
			
			@Override
			protected void tearDownMocks()
			{
				super.tearDownMocks();
				
				
				certPolResource.setCertPolicyGroupRepository(policyGroupRepo);
				certPolResource.setCertPolicyGroupDomainReltnRepository(groupDomainReltnRepo);
				certPolResource.setCertPolicyGroupReltnRepository(policyGroupReltn);
			}
			
			@Override
			protected String getGroupToRemoveFrom()
			{
				return "Group1";
			}
			
			@Override
			protected CertPolicyGroupUse getPolicyUseToRemove()
			{
				final CertPolicyGroupUse use = new CertPolicyGroupUse();
				
				final CertPolicy policy = new CertPolicy();
				policy.setPolicyName("Policy1");
				policy.setPolicyData(new byte[] {1,2,3});
				policy.setLexicon(PolicyLexicon.SIMPLE_TEXT_V1);
				
				use.setIncoming(true);
				use.setOutgoing(true);
				use.setPolicyUse(CertPolicyUse.TRUST);
				use.setPolicy(policy);
				
				return use;
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

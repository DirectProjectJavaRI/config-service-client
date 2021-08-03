package org.nhind.config.rest.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.repository.CertPolicyGroupRepository;

public class DefaultCertPolicyService_addPolicyGroupTest extends SpringBaseTest
{
	
	abstract class TestPlan extends BaseTestPlan 
	{
		
		@Override
		protected void tearDownMocks()
		{

		}
		
		protected abstract Collection<CertPolicyGroup> getGroupsToAdd();
		
		
		@Override
		protected void performInner() throws Exception
		{				
			
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

			doAssertions();

			
		}
			
		protected void doAssertions() throws Exception
		{
			
		}
	}
	
	@Test
	public void testGetAddPolicyGroup_assertPolicyGroupsAdded()  throws Exception
	{
		new TestPlan()
		{
			protected Collection<CertPolicyGroup> groups;
			
			@Override
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

			@Override
			protected void doAssertions() throws Exception
			{
				final Collection<org.nhindirect.config.store.CertPolicyGroup> groups = policyGroupRepo.findAll().collectList().block();
				
				assertNotNull(groups);
				assertEquals(2, groups.size());
				
				final Iterator<CertPolicyGroup> addedGroupsIter = this.groups.iterator();
				
				for (org.nhindirect.config.store.CertPolicyGroup retrievedGroup : groups)
				{	
					final CertPolicyGroup addedGroup = addedGroupsIter.next(); 
					
					assertEquals(addedGroup.getPolicyGroupName(), retrievedGroup.getPolicyGroupName());
				}
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetAddPolicyGroup_addDuplicate_assertConflict()  throws Exception
	{
		new TestPlan()
		{
			protected Collection<CertPolicyGroup> groups;
			
			@Override
			protected Collection<CertPolicyGroup> getGroupsToAdd()
			{
				try
				{
					groups = new ArrayList<CertPolicyGroup>();
					
					CertPolicyGroup group = new CertPolicyGroup();
					group.setPolicyGroupName("Group1");
					groups.add(group);
					
					group = new CertPolicyGroup();
					group.setPolicyGroupName("Group1");
					groups.add(group);
					
					return groups;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}

			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(409, ex.getResponseCode());
			}
		}.perform();
	}		
	
	@Test
	public void testGetAddPolicyGroup_errorInAdd_assertServiceError()  throws Exception
	{
		new TestPlan()
		{		
			protected Collection<CertPolicyGroup> groups;
			
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					CertPolicyGroupRepository mockDAO = mock(CertPolicyGroupRepository.class);
					doThrow(new RuntimeException()).when(mockDAO).save((org.nhindirect.config.store.CertPolicyGroup)any());
					
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
	public void testGetAddPolicyGroup_errorInLookup_assertServiceError()  throws Exception
	{
		new TestPlan()
		{
			protected Collection<CertPolicyGroup> groups;
			
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

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


import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.repository.CertPolicyGroupRepository;


public class DefaultCertPolicyService_updatePolicyGroupTest extends SpringBaseTest
{
	abstract class TestPlan extends BaseTestPlan 
	{
		protected Collection<CertPolicyGroup> groups;
		
		
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
				
				return groups;
			}
			catch (Exception e)
			{
				throw new RuntimeException (e);
			}
		}
		
		protected String getGroupToUpdate()
		{
			return "Group1";
		}

		
		protected abstract String getUpdateGroupAttributes();
		
		protected abstract String getGroupUpdatedName();
		
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
			
			certPolService.updatePolicyGroup(getGroupToUpdate(), getUpdateGroupAttributes());
			
			final CertPolicyGroup getGroup = certPolService.getPolicyGroup(getGroupUpdatedName());
			doAssertions(getGroup);
			
		}
			
		protected void doAssertions(CertPolicyGroup group) throws Exception
		{
			
		}
	}	
	
	@Test
	public void testUpdateGroupAttributes_updateGroupName_assertNameUpdated()  throws Exception
	{
		new TestPlan()
		{
			@Override
			protected String getUpdateGroupAttributes()
			{
				return "Group2";
			}
			
			@Override
			protected String getGroupUpdatedName()
			{
				return "Group2";
			}
			
			@Override
			protected void doAssertions(CertPolicyGroup group) throws Exception
			{
				assertEquals(getUpdateGroupAttributes(), group.getPolicyGroupName());
			}
		}.perform();
	}
	
	@Test
	public void testUpdateGroupAttributes_nonExistantGroup_assertNotFound()  throws Exception
	{
		new TestPlan()
		{
			@Override
			protected String getGroupToUpdate()
			{
				return "Group2";
			}
			
			@Override
			protected String getUpdateGroupAttributes()
			{
				return "Group2";
			}
			
			@Override
			protected String getGroupUpdatedName()
			{
				return "Group2";
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
	public void testUpdateGroupAttributes_errorInLookup_assertServiceError()  throws Exception
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
			
			protected Collection<CertPolicyGroup> getGroupsToAdd()
			{
				return null;
			}
			
			@Override
			protected String getUpdateGroupAttributes()
			{
				return "Group2";
			}
			
			@Override
			protected String getGroupUpdatedName()
			{
				return "Group2";
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
	public void testUpdateGroupAttributes_errorInUpdate_assertServiceError()  throws Exception
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
					when(mockDAO.findByPolicyGroupNameIgnoreCase((String)any())).thenReturn(new org.nhindirect.config.store.CertPolicyGroup());
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
			
			protected Collection<CertPolicyGroup> getGroupsToAdd()
			{
				return null;
			}
			
			@Override
			protected String getUpdateGroupAttributes()
			{
				return "Group2";
			}
			
			@Override
			protected String getGroupUpdatedName()
			{
				return "Group2";
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

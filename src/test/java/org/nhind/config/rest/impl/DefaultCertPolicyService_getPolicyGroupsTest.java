package org.nhind.config.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.nhind.config.client.SpringBaseTest;

import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.nhindirect.config.model.CertPolicyGroup;
import org.nhindirect.config.store.dao.CertPolicyDao;

public class DefaultCertPolicyService_getPolicyGroupsTest extends SpringBaseTest
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
				
				try
				{

					final Collection<CertPolicyGroup> getGroups = certPolService.getPolicyGroups();

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
		public void testGetAllPolicies_assertPoliciesRetrieved()  throws Exception
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
				protected void doAssertions(Collection<CertPolicyGroup> groups) throws Exception
				{
					assertNotNull(groups);
					assertEquals(2, groups.size());
					
					final Iterator<CertPolicyGroup> addedGroupsIter = this.groups.iterator();
					
					for (CertPolicyGroup retrievedGroup : groups)
					{	
						final CertPolicyGroup addedGroup = addedGroupsIter.next(); 
						
						assertEquals(addedGroup.getPolicyGroupName(), retrievedGroup.getPolicyGroupName());
						assertTrue(retrievedGroup.getPolicies().isEmpty());

					}
					
				}
			}.perform();
		}		
		
		@Test
		public void testGetAllPolicyGroups_noGroupsInStore_assertNoPoliciesRetrieved()  throws Exception
		{
			new TestPlan()
			{
				
				@Override
				protected Collection<CertPolicyGroup> getGroupsToAdd()
				{
					return null;
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
		public void testGetAllPolicyGroups_errorInLookup_assertServiceError()  throws Exception
		{
			new TestPlan()
			{		
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();

						CertPolicyDao mockDAO = mock(CertPolicyDao.class);
						doThrow(new RuntimeException()).when(mockDAO).getPolicyGroups();
						
						certPolResource.setCertPolicyDao(mockDAO);
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
					
					certPolResource.setCertPolicyDao(policyDao);
				}	
				
				@Override
				protected Collection<CertPolicyGroup> getGroupsToAdd()
				{
					return null;
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

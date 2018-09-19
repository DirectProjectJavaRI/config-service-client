package org.nhind.config.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
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

import org.nhindirect.config.model.CertPolicy;

import org.nhindirect.config.store.dao.CertPolicyDao;
import org.nhindirect.policy.PolicyLexicon;


public class DefaultCertPolicyService_deletePolicyTest extends SpringBaseTest
{
		abstract class TestPlan extends BaseTestPlan 
		{
			
			@Override
			protected void tearDownMocks()
			{

			}
			
			protected abstract Collection<CertPolicy> getPoliciesToAdd();
			
			protected abstract String getPolicyNameToDelete();
			
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
				
				certPolService.deletePolicy(getPolicyNameToDelete());
			
				doAssertions();
			}
				
			protected void doAssertions() throws Exception
			{
				
			}
		}	
		
		@Test
		public void testremovePolicyByName_removeExistingPolicy_assertPolicyRemoved()  throws Exception
		{
			new TestPlan()
			{
				protected Collection<CertPolicy> policies;
				
				@Override
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
						
						
						return policies;
					}
					catch (Exception e)
					{
						throw new RuntimeException (e);
					}
				}

				@Override
				protected String getPolicyNameToDelete()
				{
					return "Policy1";
				}
				
				@Override
				protected void doAssertions() throws Exception
				{
					assertNull(policyDao.getPolicyByName(getPolicyNameToDelete()));
				}
			}.perform();
		}		
		
		@Test
		public void testremovePolicyByName_nonExistantPolicy_assertNotFound()  throws Exception
		{
			new TestPlan()
			{
				
				@Override
				protected Collection<CertPolicy> getPoliciesToAdd()
				{
					return null;
				}

				@Override
				protected String getPolicyNameToDelete()
				{
					return "Policy1";
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
		public void testremovePolicyByName_errorInLookup_assertServiceError()  throws Exception
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
						doThrow(new RuntimeException()).when(mockDAO).getPolicyByName((String)any());
						
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
				protected Collection<CertPolicy> getPoliciesToAdd()
				{
					return null;
				}

				@Override
				protected String getPolicyNameToDelete()
				{
					return "Policy1";
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
		public void testremovePolicyByName_errorInDelete_assertServiceError()  throws Exception
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
						
						when(mockDAO.getPolicyByName((String)any())).thenReturn(new org.nhindirect.config.store.CertPolicy());
						doThrow(new RuntimeException()).when(mockDAO).deletePolicies((long[])any());
						
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
				protected Collection<CertPolicy> getPoliciesToAdd()
				{
					return null;
				}

				@Override
				protected String getPolicyNameToDelete()
				{
					return "Policy1";
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

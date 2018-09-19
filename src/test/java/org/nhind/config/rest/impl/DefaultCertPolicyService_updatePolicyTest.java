package org.nhind.config.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.CertPolicy;

import org.nhindirect.config.store.dao.CertPolicyDao;
import org.nhindirect.policy.PolicyLexicon;

public class DefaultCertPolicyService_updatePolicyTest extends SpringBaseTest
{
		abstract class TestPlan extends BaseTestPlan 
		{
			protected Collection<CertPolicy> policies;
			
			
			@Override
			protected void tearDownMocks()
			{

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
					
					return policies;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			protected String getPolicyToUpdate()
			{
				return "Policy1";
			}

			
			protected abstract CertPolicy getUpdatePolicyAttributes();
			
			protected abstract String getPolicyUpdatedName();
			
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
				
				certPolService.updatePolicy(getPolicyToUpdate(), getUpdatePolicyAttributes());

				final CertPolicy getPolicy = certPolService.getPolicyByName(getPolicyUpdatedName());
				doAssertions(getPolicy);
			}
				
			protected void doAssertions(CertPolicy policy) throws Exception
			{
				
			}
		}	
		
		@Test
		public void testUpdatePolicyAttributes_assertAttributesChanged()  throws Exception
		{
			new TestPlan()
			{
				@Override
				protected CertPolicy getUpdatePolicyAttributes()
				{
					final CertPolicy policy = new CertPolicy();
					policy.setPolicyName("Policy 2");
					policy.setLexicon(PolicyLexicon.XML);
					policy.setPolicyData(new byte[] {1,3,9,8});
					
					return policy;
				}
				
				@Override
				protected String getPolicyUpdatedName()
				{
					return "Policy 2";
				}

				@Override
				protected void doAssertions(CertPolicy policy) throws Exception
				{
					assertNotNull(policies);

						
					assertEquals("Policy 2", policy.getPolicyName());
					assertTrue(Arrays.equals(new byte[] {1,3,9,8}, policy.getPolicyData()));
					assertEquals(PolicyLexicon.XML, policy.getLexicon());
				}
			}.perform();
		}	
		
		@Test
		public void testUpdatePolicyAttributes_nullNameAndLexiconChange_assertAttributesUpdated()  throws Exception
		{
			new TestPlan()
			{
				@Override
				protected CertPolicy getUpdatePolicyAttributes()
				{
					final CertPolicy policy = new CertPolicy();
					policy.setPolicyData(new byte[] {1,3,9,8});
					
					return policy;
				}
				
				@Override
				protected String getPolicyUpdatedName()
				{
					return "Policy1";
				}

				@Override
				protected void doAssertions(CertPolicy policy) throws Exception
				{
					assertNotNull(policies);

						
					assertEquals("Policy1", policy.getPolicyName());
					assertTrue(Arrays.equals(new byte[] {1,3,9,8}, policy.getPolicyData()));
					assertEquals(PolicyLexicon.SIMPLE_TEXT_V1, policy.getLexicon());
				}
			}.perform();
		}	
		
		@Test
		public void testUpdatePolicyAttributes_nonExistantPolicy_assertNotFound()  throws Exception
		{
			new TestPlan()
			{
				
				@Override
				protected Collection<CertPolicy> getPoliciesToAdd()
				{
					return null;
				}
				
				@Override
				protected CertPolicy getUpdatePolicyAttributes()
				{
					final CertPolicy policy = new CertPolicy();
					policy.setPolicyData(new byte[] {1,3,9,8});
					
					return policy;
				}
				
				@Override
				protected String getPolicyUpdatedName()
				{
					return "Policy4";
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
		public void testUpdatePolicyAttributes_errorInLookup_assertServiceError()  throws Exception
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
				protected CertPolicy getUpdatePolicyAttributes()
				{
					final CertPolicy policy = new CertPolicy();
					policy.setPolicyData(new byte[] {1,3,9,8});
					
					return policy;
				}
				
				@Override
				protected String getPolicyUpdatedName()
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
		public void testUpdatePolicyAttributes_errorInUpdate_assertServiceError()  throws Exception
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
						doThrow(new RuntimeException()).when(mockDAO).updatePolicyAttributes(eq(0L), (String)any(), 
								(PolicyLexicon)any(), (byte[])any());
						
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
				protected CertPolicy getUpdatePolicyAttributes()
				{
					final CertPolicy policy = new CertPolicy();
					policy.setPolicyData(new byte[] {1,3,9,8});
					
					return policy;
				}
				
				@Override
				protected String getPolicyUpdatedName()
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

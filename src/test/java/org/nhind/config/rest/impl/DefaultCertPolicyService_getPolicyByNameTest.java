package org.nhind.config.rest.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.CertPolicy;
import org.nhindirect.config.repository.CertPolicyRepository;
import org.nhindirect.policy.PolicyLexicon;


public class DefaultCertPolicyService_getPolicyByNameTest extends SpringBaseTest
{
	abstract class TestPlan extends BaseTestPlan 
	{
		@Override
		protected void tearDownMocks()
		{

		}	

		protected abstract Collection<CertPolicy> getPoliciesToAdd();
		
		protected abstract String getPolicyToRetrieve();
		
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
			
			try
			{
				
				final CertPolicy getPolicy = certPolService.getPolicyByName(getPolicyToRetrieve());
				doAssertions(getPolicy);
			}
			catch (ServiceMethodException e)
			{
				if (e.getResponseCode() == 404)
					doAssertions(null);
				else
					throw e;
			}
			
		}
			
		protected void doAssertions(CertPolicy policy) throws Exception
		{
			
		}
	}
	
	@Test
	public void testGetPolicyByName_getExistingPolicy_assertPolicyRetrieved()  throws Exception
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
			
			@Override
			protected String getPolicyToRetrieve()
			{
				return "Policy1";
			}
			
			@Override
			protected void doAssertions(CertPolicy policy) throws Exception
			{
				assertNotNull(policy);
				
				final CertPolicy addedPolicy = this.policies.iterator().next();

				assertEquals(addedPolicy.getPolicyName(), policy.getPolicyName());
				assertTrue(Arrays.equals(addedPolicy.getPolicyData(), policy.getPolicyData()));
				assertEquals(addedPolicy.getLexicon(), policy.getLexicon());					
			}
		}.perform();
	}		

	
	@Test
	public void testGetPolicyByName_nonExistantPolicy_assertPolicyNotRetrieved()  throws Exception
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
			
			@Override
			protected String getPolicyToRetrieve()
			{
				return "Policy45";
			}
			
			@Override
			protected void doAssertions(CertPolicy policy) throws Exception
			{
				assertNull(policy);
			
			}
		}.perform();
	}	
	
	@Test
	public void testGetPolicyByName_errorInLookup_assertServiceError()  throws Exception
	{
		new TestPlan()
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					CertPolicyRepository mockDAO = mock(CertPolicyRepository.class);
					doThrow(new RuntimeException()).when(mockDAO).findByPolicyNameIgnoreCase((String)any());
					
					certPolResource.setCertPolicyRepository(mockDAO);
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
				
				certPolResource.setCertPolicyRepository(policyRepo);
			}	
			
			@Override
			protected Collection<CertPolicy> getPoliciesToAdd()
			{
				try
				{
					return null;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			@Override
			protected String getPolicyToRetrieve()
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



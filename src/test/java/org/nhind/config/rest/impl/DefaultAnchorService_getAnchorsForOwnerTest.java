package org.nhind.config.rest.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.nhind.config.testbase.TestUtils;
import org.nhindirect.common.cert.Thumbprint;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.Anchor;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.repository.AnchorRepository;

public class DefaultAnchorService_getAnchorsForOwnerTest extends SpringBaseTest
{
	
	abstract class TestPlan extends BaseTestPlan 
	{
		
		@Override
		protected void tearDownMocks()
		{

		}
		
		protected abstract Collection<Anchor> getAnchorsToAdd();
		
		protected abstract String getOwner();
		
		protected String getIncoming()
		{
			return null;
		}
		
		protected String getOutgoing()
		{
			return null;
		}
	
		
		protected String getThumbprint()
		{
			return null;
		}
		
		@Override
		protected void performInner() throws Exception
		{				
			
			final Collection<Anchor> anchorsToAdd = getAnchorsToAdd();
			
			if (anchorsToAdd != null)
			{
				for (Anchor addAnchor : anchorsToAdd)
				{
					try
					{
						anchorService.addAnchor(addAnchor);
					}
					catch (ServiceException e)
					{
						throw e;
					}
				}
			}
			
			try
			{

				
				final Collection<Anchor> getAnchors = anchorService.getAnchorsForOwner(getOwner(), 
						Boolean.parseBoolean(getIncoming()), Boolean.parseBoolean(getOutgoing()), 
						getThumbprint());
				doAssertions(getAnchors);
			}
			catch (ServiceMethodException e)
			{
				if (e.getResponseCode() == 204)
					doAssertions(new ArrayList<Anchor>());
				else
					throw e;
			}
			
		}
			
		protected void doAssertions(Collection<Anchor> anchors) throws Exception
		{
			
		}
	}	
	
	@Test
	public void testGetAnchorForOwner_getMultiple_noFileters_assertAnchorsRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Anchor> anchors;
			
			@Override
			protected Collection<Anchor> getAnchorsToAdd()
			{
				try
				{
					anchors = new ArrayList<Anchor>();
					
					Anchor anchor = new Anchor();
					anchor.setOwner("test.com");
					anchor.setIncoming(true);
					anchor.setOutgoing(true);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());
								
					anchors.add(anchor);
					
					
				    anchor = new Anchor();
					anchor.setOwner("test.com");
					anchor.setIncoming(true);
					anchor.setOutgoing(true);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("sm1.direct.com Root CA.der").getEncoded());	
					
					anchors.add(anchor);
					
					return anchors;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			@Override
			protected String getOwner()
			{
				return "test.com";
			}
			
			@Override
			protected void doAssertions(Collection<Anchor> anchors) throws Exception
			{
				assertNotNull(anchors);
				assertEquals(2, anchors.size());
				
				final Iterator<Anchor> addedAnchorsIter = this.anchors.iterator();
				
				for (Anchor retrievedAnchor : anchors)
				{
					final Anchor addedAnchor = addedAnchorsIter.next(); 
					assertEquals(addedAnchor.getOwner(), retrievedAnchor.getOwner());
					assertEquals(addedAnchor.getAnchorAsX509Certificate(), retrievedAnchor.getAnchorAsX509Certificate());
					assertEquals(addedAnchor.isIncoming(), retrievedAnchor.isIncoming());
					assertEquals(addedAnchor.isOutgoing(), retrievedAnchor.isOutgoing());
					assertEquals(addedAnchor.getStatus(), retrievedAnchor.getStatus());
					assertFalse(retrievedAnchor.getThumbprint().isEmpty());
				}
				
			}
		}.perform();
	}
	
	@Test
	public void testGetAnchorForOwner_getMultiple_incomingOnly_assertAnchorRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Anchor> anchors;
			
			@Override
			protected Collection<Anchor> getAnchorsToAdd()
			{
				try
				{
					anchors = new ArrayList<Anchor>();
					
					Anchor anchor = new Anchor();
					anchor.setOwner("test.com");
					anchor.setIncoming(true);
					anchor.setOutgoing(false);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());
								
					anchors.add(anchor);
					
					
				    anchor = new Anchor();
					anchor.setOwner("test.com");
					anchor.setIncoming(false);
					anchor.setOutgoing(false);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("sm1.direct.com Root CA.der").getEncoded());	
					
					anchors.add(anchor);
					
					return anchors;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			@Override
			protected String getIncoming()
			{
				return "true";
			}
			
			@Override
			protected String getOwner()
			{
				return "test.com";
			}
			
			@Override
			protected void doAssertions(Collection<Anchor> anchors) throws Exception
			{
				assertNotNull(anchors);
				assertEquals(1, anchors.size());
				
				final Iterator<Anchor> retrievedAnchorsIter = anchors.iterator();

				final Anchor retrievedAnchor = retrievedAnchorsIter.next(); 
				assertEquals(TestUtils.loadSigner("bundleSigner.der"), retrievedAnchor.getAnchorAsX509Certificate());

				
			}
		}.perform();
	}	
	
	@Test
	public void testGetAnchorForOwner_getMultiple_outgoingOnly_assertAnchorRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Anchor> anchors;
			
			@Override
			protected Collection<Anchor> getAnchorsToAdd()
			{
				try
				{
					anchors = new ArrayList<Anchor>();
					
					Anchor anchor = new Anchor();
					anchor.setOwner("test.com");
					anchor.setIncoming(false);
					anchor.setOutgoing(true);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());
								
					anchors.add(anchor);
					
					
				    anchor = new Anchor();
					anchor.setOwner("test.com");
					anchor.setIncoming(false);
					anchor.setOutgoing(false);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("sm1.direct.com Root CA.der").getEncoded());	
					
					anchors.add(anchor);
					
					return anchors;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			@Override
			protected String getOutgoing()
			{
				return "true";
			}
			
			@Override
			protected String getOwner()
			{
				return "test.com";
			}
			
			@Override
			protected void doAssertions(Collection<Anchor> anchors) throws Exception
			{
				assertNotNull(anchors);
				assertEquals(1, anchors.size());
				
				final Iterator<Anchor> retrievedAnchorsIter = anchors.iterator();

				final Anchor retrievedAnchor = retrievedAnchorsIter.next(); 
				assertEquals(TestUtils.loadSigner("bundleSigner.der"), retrievedAnchor.getAnchorAsX509Certificate());

				
			}
		}.perform();
	}
	
	@Test
	public void testGetAnchorForOwner_getMultiple_outgoingAndIncomingOnly_assertAnchorRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Anchor> anchors;
			
			@Override
			protected Collection<Anchor> getAnchorsToAdd()
			{
				try
				{
					anchors = new ArrayList<Anchor>();
					
					Anchor anchor = new Anchor();
					anchor.setOwner("test.com");
					anchor.setIncoming(true);
					anchor.setOutgoing(true);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());
								
					anchors.add(anchor);
					
					
				    anchor = new Anchor();
					anchor.setOwner("test.com");
					anchor.setIncoming(true);
					anchor.setOutgoing(false);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("sm1.direct.com Root CA.der").getEncoded());	
					
					anchors.add(anchor);
					
					return anchors;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			@Override
			protected String getOutgoing()
			{
				return "true";
			}
			
			@Override
			protected String getIncoming()
			{
				return "true";
			}
			
			@Override
			protected String getOwner()
			{
				return "test.com";
			}
			
			@Override
			protected void doAssertions(Collection<Anchor> anchors) throws Exception
			{
				assertNotNull(anchors);
				assertEquals(1, anchors.size());
				
				final Iterator<Anchor> retrievedAnchorsIter = anchors.iterator();

				final Anchor retrievedAnchor = retrievedAnchorsIter.next(); 
				assertEquals(TestUtils.loadSigner("bundleSigner.der"), retrievedAnchor.getAnchorAsX509Certificate());

				
			}
		}.perform();
	}	
	
	@Test
	public void testGetAnchorForOwner_getMultiple_specificThumbprint_assertAnchorRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Anchor> anchors;
			
			@Override
			protected Collection<Anchor> getAnchorsToAdd()
			{
				try
				{
					anchors = new ArrayList<Anchor>();
					
					Anchor anchor = new Anchor();
					anchor.setOwner("test.com");
					anchor.setIncoming(true);
					anchor.setOutgoing(true);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());
								
					anchors.add(anchor);
					
					
				    anchor = new Anchor();
					anchor.setOwner("test.com");
					anchor.setIncoming(true);
					anchor.setOutgoing(true);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("sm1.direct.com Root CA.der").getEncoded());	
					
					anchors.add(anchor);
					
					return anchors;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			@Override
			protected String getThumbprint()
			{
				try
				{
					return Thumbprint.toThumbprint(TestUtils.loadSigner("bundleSigner.der")).toString();
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}
			
			@Override
			protected String getOwner()
			{
				return "test.com";
			}
			
			@Override
			protected void doAssertions(Collection<Anchor> anchors) throws Exception
			{
				assertNotNull(anchors);
				assertEquals(1, anchors.size());
				
				final Iterator<Anchor> retrievedAnchorsIter = anchors.iterator();

				final Anchor retrievedAnchor = retrievedAnchorsIter.next(); 
				assertEquals(TestUtils.loadSigner("bundleSigner.der"), retrievedAnchor.getAnchorAsX509Certificate());

				
			}
		}.perform();
	}	
	
	@Test
	public void testGetAnchorForOwner_getMultiple_specificOwner_assertAnchorRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Anchor> anchors;
			
			@Override
			protected Collection<Anchor> getAnchorsToAdd()
			{
				try
				{
					anchors = new ArrayList<Anchor>();
					
					Anchor anchor = new Anchor();
					anchor.setOwner("test2.com");
					anchor.setIncoming(true);
					anchor.setOutgoing(true);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());
								
					anchors.add(anchor);
					
					
				    anchor = new Anchor();
					anchor.setOwner("test.com");
					anchor.setIncoming(true);
					anchor.setOutgoing(true);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("sm1.direct.com Root CA.der").getEncoded());	
					
					anchors.add(anchor);
					
					return anchors;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}

			
			@Override
			protected String getOwner()
			{
				return "test2.com";
			}
			
			@Override
			protected void doAssertions(Collection<Anchor> anchors) throws Exception
			{
				assertNotNull(anchors);
				assertEquals(1, anchors.size());
				
				final Iterator<Anchor> retrievedAnchorsIter = anchors.iterator();

				final Anchor retrievedAnchor = retrievedAnchorsIter.next(); 
				assertEquals(TestUtils.loadSigner("bundleSigner.der"), retrievedAnchor.getAnchorAsX509Certificate());

				
			}
		}.perform();
	}	
	
	@Test
	public void testGetAnchorForOwner_ownerNotInStore_assertNoAnchorRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Anchor> anchors;
			
			@Override
			protected Collection<Anchor> getAnchorsToAdd()
			{
				try
				{
					anchors = new ArrayList<Anchor>();
					
					Anchor anchor = new Anchor();
					anchor.setOwner("test.com");
					anchor.setIncoming(true);
					anchor.setOutgoing(true);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());
								
					anchors.add(anchor);
					
					
				    anchor = new Anchor();
					anchor.setOwner("test.com");
					anchor.setIncoming(true);
					anchor.setOutgoing(true);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("sm1.direct.com Root CA.der").getEncoded());	
					
					anchors.add(anchor);
					
					return anchors;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}

			
			@Override
			protected String getOwner()
			{
				return "test2.com";
			}
			
			@Override
			protected void doAssertions(Collection<Anchor> anchors) throws Exception
			{
				assertNotNull(anchors);
				assertTrue(anchors.isEmpty());
				
			}
		}.perform();
	}
	
	@Test
	public void testGetAnchorForOwner_nonMatchingThumbprint_assertNoAnchorRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Anchor> anchors;
			
			@Override
			protected Collection<Anchor> getAnchorsToAdd()
			{
				try
				{
					anchors = new ArrayList<Anchor>();
					
					Anchor anchor = new Anchor();
					anchor.setOwner("test.com");
					anchor.setIncoming(true);
					anchor.setOutgoing(true);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());
								
					anchors.add(anchor);
					
					
				    anchor = new Anchor();
					anchor.setOwner("test.com");
					anchor.setIncoming(true);
					anchor.setOutgoing(true);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("sm1.direct.com Root CA.der").getEncoded());	
					
					anchors.add(anchor);
					
					return anchors;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}

		
			@Override
			protected String getThumbprint()
			{
				return "1234";
			}
			
			@Override
			protected String getOwner()
			{
				return "test.com";
			}
			
			@Override
			protected void doAssertions(Collection<Anchor> anchors) throws Exception
			{
				assertNotNull(anchors);
				assertTrue(anchors.isEmpty());
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetAnchorForOwner_errorInLookup_assertServerError() throws Exception
	{
		new TestPlan()
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					AnchorRepository mockDAO = mock(AnchorRepository.class);
					doThrow(new RuntimeException()).when(mockDAO).findByOwnerIgnoreCase((String)any());
					
					anchorResource.setAnchorRepository(mockDAO);
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
				
				anchorResource.setAnchorRepository(anchorRepo);
			}
			
			@Override
			protected Collection<Anchor> getAnchorsToAdd()
			{
				return null;
			}
			
			@Override
			protected String getOwner()
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

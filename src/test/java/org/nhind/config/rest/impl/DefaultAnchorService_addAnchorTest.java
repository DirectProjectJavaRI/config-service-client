package org.nhind.config.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;
import org.nhind.config.testbase.TestUtils;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.nhindirect.config.model.Anchor;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.store.dao.AnchorDao;
import org.nhindirect.stagent.cert.Thumbprint;


public class DefaultAnchorService_addAnchorTest extends SpringBaseTest
{
		abstract class TestPlan extends BaseTestPlan 
		{
			@Override
			protected void tearDownMocks()
			{

			}
			

			protected abstract Collection<Anchor> getAnchorsToAdd();
			
			@Override
			protected void performInner() throws Exception
			{				
				
				final Collection<Anchor> anchorsToAdd = getAnchorsToAdd();

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

				doAssertions();
			}
				
			protected void doAssertions() throws Exception
			{
				
			}
		}	
		
		@Test
		public void testAddAnchor_assertAnchorsAdded() throws Exception
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
						anchor.setOwner("test2.com");
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
				protected void doAssertions() throws Exception
				{
					List<org.nhindirect.config.store.Anchor> anchors = anchorDao.listAll();
					
					assertNotNull(anchors);
					assertEquals(2, anchors.size());
					
					final Iterator<Anchor> addedAnchorsIter = this.anchors.iterator();
					
					for (org.nhindirect.config.store.Anchor retrievedAnchor : anchors)
					{
						final Anchor addedAnchor = addedAnchorsIter.next(); 
						assertEquals(addedAnchor.getOwner(), retrievedAnchor.getOwner());
						assertEquals(addedAnchor.getAnchorAsX509Certificate(), retrievedAnchor.toCertificate());
						assertEquals(addedAnchor.isIncoming(), retrievedAnchor.isIncoming());
						assertEquals(addedAnchor.isOutgoing(), retrievedAnchor.isOutgoing());
						assertFalse(retrievedAnchor.getThumbprint().isEmpty());
					}
					
				}
			}.perform();
		}	
		
		@Test
		public void testAddAnchor_thumbprintEmpty_assertAnchorsAdded() throws Exception
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
						anchor.setThumbprint("");
									
						anchors.add(anchor);
						
						
					    anchor = new Anchor();
						anchor.setOwner("test2.com");
						anchor.setIncoming(true);
						anchor.setOutgoing(true);
						anchor.setStatus(EntityStatus.ENABLED);
						anchor.setCertificateData(TestUtils.loadSigner("sm1.direct.com Root CA.der").getEncoded());	
						anchor.setThumbprint("");
						
						anchors.add(anchor);
						
						return anchors;
					}
					catch (Exception e)
					{
						throw new RuntimeException (e);
					}
				}

				
				@Override
				protected void doAssertions() throws Exception
				{
					List<org.nhindirect.config.store.Anchor> anchors = anchorDao.listAll();
					
					assertNotNull(anchors);
					assertEquals(2, anchors.size());
					
					final Iterator<Anchor> addedAnchorsIter = this.anchors.iterator();
					
					for (org.nhindirect.config.store.Anchor retrievedAnchor : anchors)
					{
						final Anchor addedAnchor = addedAnchorsIter.next(); 
						assertEquals(addedAnchor.getOwner(), retrievedAnchor.getOwner());
						assertEquals(addedAnchor.getAnchorAsX509Certificate(), retrievedAnchor.toCertificate());
						assertEquals(addedAnchor.isIncoming(), retrievedAnchor.isIncoming());
						assertEquals(addedAnchor.isOutgoing(), retrievedAnchor.isOutgoing());
						assertFalse(retrievedAnchor.getThumbprint().isEmpty());
					}
					
				}
			}.perform();
		}		
		
		@Test
		public void testAddAnchor_thumbprintPreset_assertAnchorsAdded() throws Exception
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
						anchor.setThumbprint(Thumbprint.toThumbprint(anchor.getAnchorAsX509Certificate()).toString());
									
						anchors.add(anchor);
						
						
					    anchor = new Anchor();
						anchor.setOwner("test2.com");
						anchor.setIncoming(true);
						anchor.setOutgoing(true);
						anchor.setStatus(EntityStatus.ENABLED);
						anchor.setCertificateData(TestUtils.loadSigner("sm1.direct.com Root CA.der").getEncoded());	
						anchor.setThumbprint(Thumbprint.toThumbprint(anchor.getAnchorAsX509Certificate()).toString());
						
						anchors.add(anchor);
						
						return anchors;
					}
					catch (Exception e)
					{
						throw new RuntimeException (e);
					}
				}

				
				@Override
				protected void doAssertions() throws Exception
				{
					List<org.nhindirect.config.store.Anchor> anchors = anchorDao.listAll();
					
					assertNotNull(anchors);
					assertEquals(2, anchors.size());
					
					final Iterator<Anchor> addedAnchorsIter = this.anchors.iterator();
					
					for (org.nhindirect.config.store.Anchor retrievedAnchor : anchors)
					{
						final Anchor addedAnchor = addedAnchorsIter.next(); 
						assertEquals(addedAnchor.getOwner(), retrievedAnchor.getOwner());
						assertEquals(addedAnchor.getAnchorAsX509Certificate(), retrievedAnchor.toCertificate());
						assertEquals(addedAnchor.isIncoming(), retrievedAnchor.isIncoming());
						assertEquals(addedAnchor.isOutgoing(), retrievedAnchor.isOutgoing());
						assertFalse(retrievedAnchor.getThumbprint().isEmpty());
					}
					
				}
			}.perform();
		}		
		
		@Test
		public void testAddAnchor_addAnchorTwice_assertConflict() throws Exception
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
						anchor.setCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());
									
						anchors.add(anchor);
						
						return anchors;
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
		public void testAddAnchor_errorInLookup_assertServierError() throws Exception
		{
			new TestPlan()
			{
				protected Collection<Anchor> anchors;
				
				@SuppressWarnings("unchecked")
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();

						AnchorDao mockDAO = mock(AnchorDao.class);
						doThrow(new RuntimeException()).when(mockDAO).list((List<String>)any());
						
						anchorResource.setAnchorDao(mockDAO);
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
					
					anchorResource.setAnchorDao(anchorDao);
				}			
				
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
						
						return anchors;
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
		public void testAddAnchor_errorInAdd_assertServierError() throws Exception
		{
			new TestPlan()
			{
				protected Collection<Anchor> anchors;
				
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();

						AnchorDao mockDAO = mock(AnchorDao.class);
						doThrow(new RuntimeException()).when(mockDAO).add((org.nhindirect.config.store.Anchor)any());
						
						anchorResource.setAnchorDao(mockDAO);
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
					
					anchorResource.setAnchorDao(anchorDao);
				}			
				
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
						
						return anchors;
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

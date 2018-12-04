package org.nhind.config.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;
import org.nhind.config.testbase.TestUtils;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.nhindirect.config.model.DNSRecord;
import org.nhindirect.config.model.utils.DNSUtils;
import org.nhindirect.config.repository.DNSRepository;
import org.xbill.DNS.Type;

public class DefaultDNSService_getDNSRecordTest extends SpringBaseTest
{
	abstract class TestPlan extends BaseTestPlan 
	{
		protected Collection<DNSRecord> records;
		
		
		@Override
		protected void tearDownMocks()
		{

		}
		
		protected Collection<DNSRecord> getDNSRecordsToAdd()
		{
			try
			{
				records = new ArrayList<DNSRecord>();
				
				DNSRecord record = DNSUtils.createARecord("myserver.com", 3600, "10.232.12.43");			
				records.add(record);
				
				
				record = DNSUtils.createARecord("myserver.com", 3600, "10.232.12.44");						
				records.add(record);
				
				record = DNSUtils.createARecord("myserver2.com", 3600, "10.232.12.99");						
				records.add(record);
				
				record = DNSUtils.createX509CERTRecord("gm2552@securehealthemail.com", 3600, TestUtils.loadCert("gm2552.der"));					
				records.add(record);
				
				record = DNSUtils.createMXRecord("myserver.com", "10.232.12.77", 3600, 2);
				records.add(record);
				
				return records;
			}
			catch (Exception e)
			{
				throw new RuntimeException (e);
			}
		}

		protected abstract String getTypeToRetrieve();
		
		protected abstract String getNameToRetrieve();
		
		@Override
		protected void performInner() throws Exception
		{				
			
			final Collection<DNSRecord> recsToAdd = getDNSRecordsToAdd();
			
			if (recsToAdd != null)
			{
				for (DNSRecord addRec : recsToAdd)
				{
					try
					{
						dnsService.addDNSRecord(addRec);
					}
					catch (ServiceException e)
					{
						throw e;
					}
				}
			}
			
			try
			{
				final int type = getTypeToRetrieve() == null ? -1  : Integer.parseInt(getTypeToRetrieve());
				final String name = getNameToRetrieve() == null ? "" : getNameToRetrieve();

				final Collection<DNSRecord> records = dnsService.getDNSRecord(type, name);
				
				doAssertions(records);
			}
			catch (ServiceMethodException e)
			{
				if (e.getResponseCode() == 204)
					doAssertions(new ArrayList<DNSRecord>());
				else
					throw e;
			}
			
		}
			
		protected void doAssertions(Collection<DNSRecord> records) throws Exception
		{
			
		}
  }

	@Test
	public void testGetDNSRecords_byTypeOnly_assertRecordsRetrieved() throws Exception
	{
		new TestPlan()
		{
				
			@Override
			protected String getTypeToRetrieve()
			{
				return Integer.toString(Type.A);
			}
			
			@Override
			protected String getNameToRetrieve()
			{
				return "";
			}
			
			@Override
			protected void doAssertions(Collection<DNSRecord> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(3, records.size());
				
				final Iterator<DNSRecord> addedRecordsIter = this.records.iterator();
				
				for (DNSRecord retrievedRecord : records)
				{
					final DNSRecord addedRecord = addedRecordsIter.next(); 
					
					assertEquals(addedRecord.getDclass(), retrievedRecord.getDclass());
					assertEquals(Type.A, retrievedRecord.getType());						
					assertTrue(Arrays.equals(addedRecord.getData(), retrievedRecord.getData()));
					assertEquals(addedRecord.getTtl(), retrievedRecord.getTtl());
					assertEquals(addedRecord.getName(), retrievedRecord.getName());
				}
				
			}
		}.perform();
	}
	
	@Test
	public void testGetDNSRecords_byNameOnly_assertRecordsRetrieved() throws Exception
	{
		new TestPlan()
		{
			
			@Override
			protected String getTypeToRetrieve()
			{
				return null;
			}
			
			@Override
			protected String getNameToRetrieve()
			{
				return "myServer.com";
			}
			
			@Override
			protected void doAssertions(Collection<DNSRecord> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(3, records.size());
				
				for (DNSRecord retrievedRecord : records)
				{						
					assertTrue(retrievedRecord.getName().equalsIgnoreCase("myServer.com."));
				}
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetDNSRecords_byNameOnly_dottedSuffix_assertRecordsRetrieved() throws Exception
	{
		new TestPlan()
		{
			
			@Override
			protected String getTypeToRetrieve()
			{
				return null;
			}
			
			@Override
			protected String getNameToRetrieve()
			{
				return "myServer.com.";
			}
			
			@Override
			protected void doAssertions(Collection<DNSRecord> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(3, records.size());
				
				for (DNSRecord retrievedRecord : records)
				{						
					assertTrue(retrievedRecord.getName().equalsIgnoreCase("myServer.com."));
				}
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetDNSRecords_byNameAndType_assertRecordsRetrieved() throws Exception
	{
		new TestPlan()
		{
			
			@Override
			protected String getTypeToRetrieve()
			{
				return Integer.toString(Type.A);
			}
			
			@Override
			protected String getNameToRetrieve()
			{
				return "myServer.com";
			}
			
			@Override
			protected void doAssertions(Collection<DNSRecord> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(2, records.size());
				
				for (DNSRecord retrievedRecord : records)
				{					
					assertEquals(Type.A, retrievedRecord.getType());	
					assertTrue(retrievedRecord.getName().equalsIgnoreCase("myServer.com."));
				}
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetDNSRecords_byNameAndType_dottedSuffix_assertRecordsRetrieved() throws Exception
	{
		new TestPlan()
		{
			
			@Override
			protected String getTypeToRetrieve()
			{
				return Integer.toString(Type.A);
			}
			
			@Override
			protected String getNameToRetrieve()
			{
				return "myServer.com.";
			}
			
			@Override
			protected void doAssertions(Collection<DNSRecord> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(2, records.size());
				
				for (DNSRecord retrievedRecord : records)
				{					
					assertEquals(Type.A, retrievedRecord.getType());	
					assertTrue(retrievedRecord.getName().equalsIgnoreCase("myServer.com."));
				}
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetDNSRecords_getAllWithAnyType_assertRecordsRetrieved() throws Exception
	{
		new TestPlan()
		{
			
			@Override
			protected String getTypeToRetrieve()
			{
				return Integer.toString(Type.ANY);
			}
			
			@Override
			protected String getNameToRetrieve()
			{
				return "";
			}
			
			@Override
			protected void doAssertions(Collection<DNSRecord> records) throws Exception
			{
				assertNotNull(records);
				assertEquals(this.records.size(), records.size());
				
				final Iterator<DNSRecord> addedRecordsIter = this.records.iterator();
				
				for (DNSRecord retrievedRecord : records)
				{
					final DNSRecord addedRecord = addedRecordsIter.next(); 
					
					assertEquals(addedRecord.getDclass(), retrievedRecord.getDclass());
					assertEquals(addedRecord.getType(), retrievedRecord.getType());						
					assertTrue(Arrays.equals(addedRecord.getData(), retrievedRecord.getData()));
					assertEquals(addedRecord.getTtl(), retrievedRecord.getTtl());
					assertEquals(addedRecord.getName(), retrievedRecord.getName());
				}
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetDNSRecords_typeNotInStore_assertNoRecordsRetrieved() throws Exception
	{
		new TestPlan()
		{
			
			@Override
			protected String getTypeToRetrieve()
			{
				return Integer.toString(Type.A6);
			}
			
			@Override
			protected String getNameToRetrieve()
			{
				return "";
			}
			
			@Override
			protected void doAssertions(Collection<DNSRecord> records) throws Exception
			{
				assertNotNull(records);
				assertTrue(records.isEmpty());
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetDNSRecords_noTypeOrName_assertBadRequest() throws Exception
	{
		new TestPlan()
		{
			
			@Override
			protected String getTypeToRetrieve()
			{
				return "-1";
			}
			
			@Override
			protected String getNameToRetrieve()
			{
				return "";
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof ServiceMethodException);
				ServiceMethodException ex = (ServiceMethodException)exception;
				assertEquals(400, ex.getResponseCode());
			}
		}.perform();
	}	
	
	@Test
	public void testGetDNSRecords_errorInLookup_assertServiceError() throws Exception
	{
		new TestPlan()
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					DNSRepository mockDAO = mock(DNSRepository.class);
					doThrow(new RuntimeException()).when(mockDAO).findByNameIgnoreCase((String)any());
					
					dnsResource.setDNSRepository(mockDAO);
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
				
				dnsResource.setDNSRepository(dnsRepo);
			}
			
			
			@Override
			protected String getTypeToRetrieve()
			{
				return "-1";
			}
			
			@Override
			protected String getNameToRetrieve()
			{
				return "myserver.com";
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

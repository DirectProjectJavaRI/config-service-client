package org.nhind.config.rest.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;

import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.nhindirect.config.model.DNSRecord;
import org.nhindirect.config.model.utils.DNSUtils;
import org.nhindirect.config.repository.DNSRepository;
import org.nhindirect.config.resources.util.EntityModelConversion;

import reactor.core.publisher.Mono;

public class DefaultDNSService_updateDNSRecordTest extends SpringBaseTest
{
		abstract class TestPlan extends BaseTestPlan 
		{
			protected DNSRecord addedRecord;
			
			@Override
			protected void tearDownMocks()
			{

			}
			
			protected DNSRecord getDNSRecordToAdd()
			{

				addedRecord = DNSUtils.createARecord("myserver.com", 3600, "10.232.12.43");			
				return addedRecord;
			}
			
			protected abstract DNSRecord getRecordToUpdate();
			
			@Override
			protected void performInner() throws Exception
			{				
				
				final DNSRecord addRecord = getDNSRecordToAdd();
				
				if (addRecord != null)
				{
					try
					{
						dnsService.addDNSRecord(addRecord);
					}
					catch (ServiceException e)
					{
						throw e;
					}
				}
				
				final DNSRecord recordToUpdate = getRecordToUpdate();
				
				try
				{
					dnsService.updatedDNSRecord(recordToUpdate);
				}
				catch (ServiceException e)
				{
					throw e;
				}
				
				try
				{
					final Collection<DNSRecord> getRecords = dnsService.getDNSRecord(recordToUpdate.getType(), recordToUpdate.getName()); 
					doAssertions(getRecords);
				}
				catch (ServiceException e)
				{
					throw e;
				}
				
			}
			
			
			protected void doAssertions(Collection<DNSRecord> records) throws Exception
			{
				
			}
		}	
		
		@Test
		public void testUpdateDNSRecord_updateExistingRecord_assertRecordUpdated() throws Exception
		{
			new TestPlan()
			{
				protected DNSRecord updatedRecord;
				
				@Override
				protected DNSRecord getRecordToUpdate()
				{				
					Collection<org.nhindirect.config.store.DNSRecord> records = dnsRepo.findByNameIgnoreCaseAndType(addedRecord.getName(), addedRecord.getType()).collectList().block();
					
					// should be one record
					assertEquals(1, records.size());
					
					org.nhindirect.config.store.DNSRecord record = records.iterator().next();
					record.setName("server2.com.");
					
					updatedRecord = EntityModelConversion.toModelDNSRecord(record);
					
					return updatedRecord;
				}
				
				@Override
				protected void doAssertions(Collection<DNSRecord> records) throws Exception
				{
					assertEquals(1, records.size());
					
					DNSRecord record = records.iterator().next();
					
					assertEquals("server2.com.", record.getName());
					assertTrue(Arrays.equals(updatedRecord.getData(), record.getData()));
				}
			}.perform();
		}	
		
		@Test
		public void testUpdateDNSRecord_updateExistingRecord_noDottedSuffix_assertRecordUpdated() throws Exception
		{
			new TestPlan()
			{
				protected DNSRecord updatedRecord;
				
				@Override
				protected DNSRecord getRecordToUpdate()
				{				
					Collection<org.nhindirect.config.store.DNSRecord> records = dnsRepo.findByNameIgnoreCaseAndType(addedRecord.getName(), addedRecord.getType()).collectList().block();
					
					// should be one record
					assertEquals(1, records.size());
					
					org.nhindirect.config.store.DNSRecord record = records.iterator().next();
					record.setName("server2.com");
					
					updatedRecord = EntityModelConversion.toModelDNSRecord(record);
					
					return updatedRecord;
				}
				
				@Override
				protected void doAssertions(Collection<DNSRecord> records) throws Exception
				{
					assertEquals(1, records.size());
					
					DNSRecord record = records.iterator().next();
					
					assertEquals("server2.com.", record.getName());
					assertTrue(Arrays.equals(updatedRecord.getData(), record.getData()));
				}
			}.perform();
		}	
		
		@Test
		public void testUpdateDNSRecord_recordDoesntExist_assertNotFound() throws Exception
		{
			new TestPlan()
			{
				protected DNSRecord updatedRecord;
				
				@Override
				protected DNSRecord getRecordToUpdate()
				{				
					updatedRecord = DNSUtils.createARecord("myserver.com", 3600, "10.232.12.43");		
					updatedRecord.setId(1233);
					return updatedRecord;
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
		public void testUpdateDNSRecord_errorInLookup_assertServerError() throws Exception
		{
			new TestPlan()
			{
				protected DNSRecord updatedRecord;
				
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						DNSRepository mockDAO = mock(DNSRepository.class);
						
						doThrow(new RuntimeException()).when(mockDAO).findById(eq(1233L));
						
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
				protected DNSRecord getDNSRecordToAdd()
				{
					return null;
				}
				
				@Override
				protected DNSRecord getRecordToUpdate()
				{				
					updatedRecord = DNSUtils.createARecord("myserver.com", 3600, "10.232.12.43");		
					updatedRecord.setId(1233);
					return updatedRecord;
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
		public void testUpdateDNSRecord_errorInUpdate_assertServerError() throws Exception
		{
			new TestPlan()
			{
				protected DNSRecord updatedRecord;

				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						
						DNSRepository mockDAO = mock(DNSRepository.class);
						final org.nhindirect.config.store.DNSRecord op = new org.nhindirect.config.store.DNSRecord();
						op.setName("Test");
						
						when(mockDAO.findById(1233L)).thenReturn(Mono.just(op));
						doThrow(new RuntimeException()).when(mockDAO).save((org.nhindirect.config.store.DNSRecord)any());
						
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
				protected DNSRecord getDNSRecordToAdd()
				{
					return null;
				}
				
				@Override
				protected DNSRecord getRecordToUpdate()
				{				
					updatedRecord = DNSUtils.createARecord("myserver.com", 3600, "10.232.12.43");		
					updatedRecord.setId(1233);
					return updatedRecord;
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

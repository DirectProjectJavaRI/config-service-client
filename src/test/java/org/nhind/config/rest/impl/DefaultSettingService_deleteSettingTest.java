package org.nhind.config.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.Setting;

import org.nhindirect.config.store.dao.SettingDao;

public class DefaultSettingService_deleteSettingTest extends SpringBaseTest
{
		abstract class TestPlan extends BaseTestPlan 
		{
			protected Setting addedSetting;	
			
			@Override
			protected void tearDownMocks()
			{

			}
			protected Setting getSettingToAdd()
			{

				addedSetting = new Setting();
				addedSetting.setName("setting1");
				addedSetting.setValue("value1");
				return addedSetting;
			}
			
			protected abstract String getSettingNameToRemove();
			
			@Override
			protected void performInner() throws Exception
			{				
				final Setting addSetting = getSettingToAdd();
				
				if (addSetting != null)
				{
					try
					{
						settingService.addSetting(addSetting.getName(), addSetting.getValue());
					}
					catch (ServiceException e)
					{
						throw e;
					}
				}
				
				settingService.deleteSetting(getSettingNameToRemove());

				doAssertions();

				
			}
				
			protected void doAssertions() throws Exception
			{
				
			}
		}
		
		@Test
		public void testRemoveSetting_removeExistingSetting_assertSettingRemoved() throws Exception
		{
			new TestPlan()
			{

				@Override
				protected String getSettingNameToRemove()
				{
					return "setting1";
				}

				
				@Override
				protected void doAssertions() throws Exception
				{
					Collection<org.nhindirect.config.store.Setting> retrievedSettings = settingDao.getAll();
					
					assertNotNull(retrievedSettings);
					assertEquals(0, retrievedSettings.size());
				}
			}.perform();
		}	
		
		@Test
		public void testRemoveSetting_settingNotFound_assertNotFound() throws Exception
		{
			new TestPlan()
			{

				@Override
				protected String getSettingNameToRemove()
				{
					return "setting2";
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
		public void testRemoveSetting_errorInLookup_assertServiceError() throws Exception
		{
			new TestPlan()
			{
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						SettingDao mockDAO = mock(SettingDao.class);
						doThrow(new RuntimeException()).when(mockDAO).getByNames(Arrays.asList("setting1"));
						
						settingResource.setSettingDao(mockDAO);
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
					
					settingResource.setSettingDao(settingDao);
				}	
				
				protected Setting getSettingToAdd()
				{
					return null;
				}
				
				
				@Override
				protected String getSettingNameToRemove()
				{
					return "setting1";
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
		public void testRemoveSetting_errorInDelete_assertServiceError() throws Exception
		{
			new TestPlan()
			{

				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();

						SettingDao mockDAO = mock(SettingDao.class);
						org.nhindirect.config.store.Setting setting = new org.nhindirect.config.store.Setting();
						when(mockDAO.getByNames(Arrays.asList("setting1"))).thenReturn(Arrays.asList(setting));
						doThrow(new RuntimeException()).when(mockDAO).delete(eq(Arrays.asList("setting1")));
						
						settingResource.setSettingDao(mockDAO);
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
					
					settingResource.setSettingDao(settingDao);
				}	
				
				protected Setting getSettingToAdd()
				{
					return null;
				}
				
				
				@Override
				protected String getSettingNameToRemove()
				{
					return "setting1";
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

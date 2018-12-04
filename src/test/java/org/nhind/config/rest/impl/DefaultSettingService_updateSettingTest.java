package org.nhind.config.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.Setting;
import org.nhindirect.config.repository.SettingRepository;

public class DefaultSettingService_updateSettingTest extends SpringBaseTest
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
		
		protected abstract String getSettingNameToUpdate();
		
		protected abstract String getSettingValueToUpdate();
		
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
			
			
			try
			{
				settingService.updateSetting(getSettingNameToUpdate(), getSettingValueToUpdate());
			}
			catch (ServiceException e)
			{
				throw e;
			}
			
			final Setting setting = settingService.getSetting(getSettingNameToUpdate());
			
			doAssertions(setting);

			
		}
			
		protected void doAssertions(Setting setting) throws Exception
		{
			
		}
	}
	
	@Test
	public void testUpdateSetting_updateExistingSetting_assertSettingUpdated() throws Exception
	{
		new TestPlan()
		{

			@Override
			protected String getSettingNameToUpdate()
			{
				return "setting1";
			}
			
			protected String getSettingValueToUpdate()
			{
				return "value2";
			}
			
			@Override
			protected void doAssertions(Setting setting) throws Exception
			{
				assertEquals("setting1", setting.getName());
				assertEquals("value2", setting.getValue());
			}
		}.perform();
	}	
	
	@Test
	public void testUpdateSetting_settingNotFound_assertNotFound() throws Exception
	{
		new TestPlan()
		{

			@Override
			protected String getSettingNameToUpdate()
			{
				return "setting2";
			}
			
			protected String getSettingValueToUpdate()
			{
				return "value2";
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
	public void testUpdateSetting_errorInLookup_assertServiceError() throws Exception
	{
		new TestPlan()
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();

					SettingRepository mockDAO = mock(SettingRepository.class);
					doThrow(new RuntimeException()).when(mockDAO).findByNameIgnoreCase(eq("setting1"));
					
					settingResource.setSettingRepository(mockDAO);
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
				
				settingResource.setSettingRepository(settingRepo);
			}	
			
			protected Setting getSettingToAdd()
			{
				return null;
			}
			
			@Override
			protected String getSettingNameToUpdate()
			{
				return "setting1";
			}
			
			protected String getSettingValueToUpdate()
			{
				return "value2";
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
	public void testUpdateSetting_errorInUpdate_assertServiceError() throws Exception
	{
		new TestPlan()
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					SettingRepository mockDAO = mock(SettingRepository.class);
					org.nhindirect.config.store.Setting setting = new org.nhindirect.config.store.Setting();
					when(mockDAO.findByNameIgnoreCase(eq("setting1"))).thenReturn(setting);
					doThrow(new RuntimeException()).when(mockDAO).save((org.nhindirect.config.store.Setting)any());
					
					settingResource.setSettingRepository(mockDAO);
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
				
				settingResource.setSettingRepository(settingRepo);
			}	
			
			protected Setting getSettingToAdd()
			{
				return null;
			}
			
			@Override
			protected String getSettingNameToUpdate()
			{
				return "setting1";
			}
			
			protected String getSettingValueToUpdate()
			{
				return "value2";
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

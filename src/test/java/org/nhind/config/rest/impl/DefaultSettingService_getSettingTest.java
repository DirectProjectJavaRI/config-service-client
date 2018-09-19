package org.nhind.config.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.Setting;

import org.nhindirect.config.store.dao.SettingDao;

public class DefaultSettingService_getSettingTest extends SpringBaseTest
{
	abstract class TestPlan extends BaseTestPlan 
	{
		
		@Override
		protected void tearDownMocks()
		{

		}
		
		protected abstract Collection<Setting> getSettingsToAdd();
		
		protected abstract String getSettingToRetrieve();
		
		@Override
		protected void performInner() throws Exception
		{				
			
			final Collection<Setting> settingsToAdd = getSettingsToAdd();
			
			if (settingsToAdd != null)
			{
				for (Setting addSetting : settingsToAdd)
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
			}
			
			try
			{
				
				final Setting setting = settingService.getSetting(getSettingToRetrieve());
				doAssertions(setting);
			}
			catch (ServiceMethodException e)
			{
				if (e.getResponseCode() == 404)
					doAssertions(null);
				else
					throw e;
			}
			
		}
			
		protected void doAssertions(Setting setting) throws Exception
		{
			
		}
	}	
	
	@Test
	public void testGetSettingByName_assertSettingRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Setting> settings;
			
			@Override
			protected Collection<Setting> getSettingsToAdd()
			{

				settings = new ArrayList<Setting>();
				
				Setting setting = new Setting();					
				setting.setName("setting1");
				setting.setValue("value1");
				settings.add(setting);
				
				setting = new Setting();					
				setting.setName("setting2");
				setting.setValue("value2");
				settings.add(setting);
				
				return settings;

			}

			protected String getSettingToRetrieve()
			{
				return "setting1";
			}
			
			@Override
			protected void doAssertions(Setting setting) throws Exception
			{
				assertNotNull(setting);
				
				final Setting addedSetting = this.settings.iterator().next();
			
				assertEquals(addedSetting.getName(), setting.getName());
				assertEquals(addedSetting.getValue(), setting.getValue());
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetSettingByName_settingNotInStore_assertNoSettingRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Setting> settings;
			
			@Override
			protected Collection<Setting> getSettingsToAdd()
			{

				settings = new ArrayList<Setting>();
				
				Setting setting = new Setting();					
				setting.setName("setting1");
				setting.setValue("value1");
				settings.add(setting);
				
				setting = new Setting();					
				setting.setName("setting2");
				setting.setValue("value2");
				settings.add(setting);
				
				return settings;

			}

			protected String getSettingToRetrieve()
			{
				return "settin51";
			}
			
			@Override
			protected void doAssertions(Setting setting) throws Exception
			{
				assertNull(setting);
				
				
			}
		}.perform();
	}		
	
	@Test
	public void testGetSettingByName_errorInLookup_assertServiceError() throws Exception
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
					doThrow(new RuntimeException()).when(mockDAO).getByNames(Arrays.asList("settin51"));
					
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
			
			@Override
			protected Collection<Setting> getSettingsToAdd()
			{

				return null;

			}

			protected String getSettingToRetrieve()
			{
				return "settin51";
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

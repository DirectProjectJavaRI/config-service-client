package org.nhind.config.rest.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.nhind.config.client.SpringBaseTest;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.Setting;
import org.nhindirect.config.repository.SettingRepository;



public class DefaultSettingService_getSettingsTest extends SpringBaseTest
{
	abstract class TestPlan extends BaseTestPlan 
	{
		
		@Override
		protected void tearDownMocks()
		{

		}
		
		protected abstract Collection<Setting> getSettingsToAdd();
		
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
				final Collection<Setting> getSettings = settingService.getSettings();
				doAssertions(getSettings);
			}
			catch (ServiceMethodException e)
			{
				if (e.getResponseCode() == 204)
					doAssertions(new ArrayList<Setting>());
				else
					throw e;
			}
			
		}
			
		protected void doAssertions(Collection<Setting> setting) throws Exception
		{
			
		}
	}	
	
	@Test
	public void testGetSettings_assertSettingsRetrieved() throws Exception
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

			
			@Override
			protected void doAssertions(Collection<Setting> setting) throws Exception
			{
				assertNotNull(setting);
				assertEquals(2, setting.size());
				
				final Iterator<Setting> addedSettingsIter = this.settings.iterator();
				
				for (Setting retrievedSetting : settings)
				{	
					final Setting addedSetting = addedSettingsIter.next(); 
					
					assertEquals(addedSetting.getName(), retrievedSetting.getName());
					assertEquals(addedSetting.getValue(), retrievedSetting.getValue());
				}
				
			}
		}.perform();
	}	
	
	
	@Test
	public void testGetSettings_noSettingsInStore_assertNoSettingsRetrieved() throws Exception
	{
		new TestPlan()
		{
			
			@Override
			protected Collection<Setting> getSettingsToAdd()
			{
				return null;
			}

			
			@Override
			protected void doAssertions(Collection<Setting> setting) throws Exception
			{
				assertNotNull(setting);
				assertEquals(0, setting.size());
				
				
			}
		}.perform();
	}		
	
	@Test
	public void testGetSettings_errorInLookup_assertServiceError() throws Exception
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
					doThrow(new RuntimeException()).when(mockDAO).findAll();
					
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
			
			@Override
			protected Collection<Setting> getSettingsToAdd()
			{
				return null;
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

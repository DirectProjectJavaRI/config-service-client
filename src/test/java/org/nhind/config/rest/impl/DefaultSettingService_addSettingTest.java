package org.nhind.config.rest.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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


public class DefaultSettingService_addSettingTest extends SpringBaseTest
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
				
				doAssertions();

				
			}
				
			protected void doAssertions() throws Exception
			{
				
			}
		}
		
		@Test
		public void testAddSetting_assertSettingAdded() throws Exception
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
				protected void doAssertions() throws Exception
				{
					Collection<org.nhindirect.config.store.Setting> retrievedSettings = settingRepo.findAll().collectList().block();
					
					assertNotNull(retrievedSettings);
					assertEquals(this.settings.size(), retrievedSettings.size());
					
					final Iterator<Setting> addedSettingsIter = this.settings.iterator();
					
					for (org.nhindirect.config.store.Setting retrievedSetting : retrievedSettings)
					{
						final Setting addedSetting = addedSettingsIter.next(); 
						
						assertEquals(addedSetting.getName(), retrievedSetting.getName());
						assertEquals(addedSetting.getValue(), retrievedSetting.getValue());
					}
					
				}
			}.perform();
		}		
		
		@Test
		public void testAddSetting_addDuplicate_assertConflict() throws Exception
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
					setting.setName("setting1");
					setting.setValue("value2");
					settings.add(setting);
					
					return settings;

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
		public void testAddSetting_errorInLookup_assertServiceError() throws Exception
		{
			new TestPlan()
			{
				protected Collection<Setting> settings;

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
				
				@Override
				protected Collection<Setting> getSettingsToAdd()
				{

					settings = new ArrayList<Setting>();
					
					Setting setting = new Setting();					
					setting.setName("setting1");
					setting.setValue("value1");
					settings.add(setting);
					
					
					return settings;

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
		public void testAddSetting_errorInAdd_assertServiceError() throws Exception
		{
			new TestPlan()
			{
				protected Collection<Setting> settings;

				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						SettingRepository mockDAO = mock(SettingRepository.class);

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
				
				@Override
				protected Collection<Setting> getSettingsToAdd()
				{

					settings = new ArrayList<Setting>();
					
					Setting setting = new Setting();					
					setting.setName("setting1");
					setting.setValue("value1");
					settings.add(setting);
					
					
					return settings;

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

package org.nhind.config.rest.impl;

import java.util.Collection;
import java.util.Collections;

import org.nhind.config.rest.SettingService;
import org.nhind.config.rest.feign.SettingClient;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultSettingService implements SettingService
{
	protected SettingClient settingClient;
	
    public DefaultSettingService(SettingClient settingClient) 
    {	
        this.settingClient = settingClient;
    }

	@Autowired
	public void setSettingClient(SettingClient settingClient)
	{
		this.settingClient = settingClient;
	}
    
	@Override
	public Collection<Setting> getSettings() throws ServiceException 
	{
		final Collection<Setting> settings = settingClient.getAllSettings();
		return (settings == null) ? Collections.emptyList() : settings;
	}

	@Override
	public Setting getSetting(String name) throws ServiceException 
	{
		return settingClient.getSettingByName(name);
	}

	@Override
	public void addSetting(String name, String value) throws ServiceException 
	{	
		settingClient.addSetting(name, value);	
	}

	@Override
	public void updateSetting(String name, String value) throws ServiceException 
	{
		settingClient.updateSetting(name, value);	
	}

	@Override
	public void deleteSetting(String name) throws ServiceException 
	{
		settingClient.removeSettingByName(name);
	}
}

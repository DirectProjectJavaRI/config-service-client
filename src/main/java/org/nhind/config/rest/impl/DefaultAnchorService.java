package org.nhind.config.rest.impl;

import java.util.Collection;
import java.util.Collections;

import org.nhind.config.rest.AnchorService;
import org.nhind.config.rest.feign.AnchorClient;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Anchor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultAnchorService implements AnchorService
{
	protected AnchorClient anchorClient;
	
    public DefaultAnchorService(AnchorClient anchorClient) 
    {	
        this.anchorClient = anchorClient;
    }

	@Autowired
	public void setAnchorClient(AnchorClient anchorClient)
	{
		this.anchorClient = anchorClient;
	}
    
	@Override
	public Collection<Anchor> getAnchors()  throws ServiceException
	{
		final Collection<Anchor> anchors =  anchorClient.getAnchors();
		return (anchors == null) ? Collections.emptyList() : anchors;
	}

	@Override
	public Collection<Anchor> getAnchorsForOwner(String owner,
			boolean incoming, boolean outgoing, String thumbprint) throws ServiceException
	{
		final Collection<Anchor> anchors =  anchorClient.getAnchorForOwner(incoming, outgoing, thumbprint, owner);
		return (anchors == null) ? Collections.emptyList() : anchors;
	}

	@Override
	public void addAnchor(Anchor anchor) throws ServiceException
	{
		anchorClient.addAnchor(anchor);
	}

	@Override
	public void deleteAnchorsByIds(Collection<Long> ids) throws ServiceException
	{
    	final StringBuilder builder = new StringBuilder();
    	
    	int cnt = 0;
    	for (Long id : ids)
    	{
    		builder.append(id);
    		if (cnt < ids.size())
    			builder.append(",");
    			
    		++cnt;
    	}
    	
		anchorClient.removeAnchorsByIds(builder.toString());
	}

	@Override
	public void deleteAnchorsByOwner(String owner) throws ServiceException
	{
		anchorClient.removeAnchorsByOwner(owner);
	}
      
}

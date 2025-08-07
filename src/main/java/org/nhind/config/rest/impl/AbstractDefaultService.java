package org.nhind.config.rest.impl;

import org.nhindirect.common.rest.exceptions.ServiceException;

public class AbstractDefaultService {

	protected ServiceException convertException(Exception e) {
		
		if (e instanceof ServiceException)
			return (ServiceException)e;
		else if (e.getCause() != null && e.getCause() instanceof ServiceException)
			return (ServiceException)e.getCause();
		else
			return new ServiceException();
		
	}
	
}

package org.nhind.config.rest.feign.configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.nhindirect.common.rest.exceptions.AuthorizationException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import feign.Response;
import feign.codec.ErrorDecoder;

public class ConfigServicesErrorDecoder implements ErrorDecoder
{

	@SuppressWarnings("deprecation")
	@Override
	public Exception decode(String methodKey, Response response)
	{
		switch (response.status())
		{
			case 401:
				return new AuthorizationException("Action not authorized");
			case 404: 
				return new ServiceMethodException(404, response.reason());
			default:
			{
		        final ByteArrayOutputStream out = new ByteArrayOutputStream();
		        String body = "";
		        if (response.body() != null) 
		        {
		        	try
					{
						IOUtils.copy(response.body().asInputStream(), out);
						body = out.toString("UTF-8");
					} 
		        	catch (IOException e)
					{

					}
		        	finally
		        	{
		        		IOUtils.closeQuietly(out);
		        	}
		        }
		        return new ServiceMethodException(response.status(),
		                "Unexpected HTTP status code received from target service: " + response.status()
		                        + ". Response body contained: " + body);
			}

		}
	}

}

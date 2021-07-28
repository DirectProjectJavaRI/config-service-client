package org.nhind.config.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter
{	
    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() 
    {
        DefaultHttpFirewall firewall = new DefaultHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }    	
		
    @Override
    public void configure(WebSecurity web) throws Exception 
    {
      super.configure(web);
      web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
    }    
	
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception 
    {
        httpSecurity.authorizeRequests().antMatchers("/").permitAll();
        httpSecurity.csrf().disable();
    }
    
    
    
}


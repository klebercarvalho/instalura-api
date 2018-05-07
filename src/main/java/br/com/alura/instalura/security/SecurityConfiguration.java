package br.com.alura.instalura.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	private UserDetailsService users;
	private TokenAuthenticationService tokenAuthenticationService;
	
	

	public SecurityConfiguration(UserDetailsService users) {
		super();
		this.users = users;
		this.tokenAuthenticationService = new TokenAuthenticationService("tooManySecrets", users);
	}

	
	/*
	 * alots -> eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbG90cyJ9.AQrl-JRJg39KeKYxMfNpnljXwxu0WOa8iYxT1Ih9Be-832MQBrJ5DxHwJzyQ-P-wp9lP49fSCmr_St-kl97nPw
	 * vitor -> eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ2aXRvciJ9.1kUf7_VXsh4ZNkB6uXX2CBlBOM0qSMDWEAyJwwGtStGBeBN7PhOBN-5ui2xmGe-fQlmjRkWWiEnuOEuCMW1WMA
	 */

	@Override
	protected void configure(HttpSecurity http) throws Exception {		
		http.authorizeRequests()
		.antMatchers("/api/public/**").permitAll()
		.antMatchers("/gera/dados").permitAll()
		.antMatchers(HttpMethod.POST, "/api/login").permitAll()
		.antMatchers("/swagger-ui.html").permitAll()
		.antMatchers("/webjars/**").permitAll()
		.antMatchers("/swagger-resources/**").permitAll()
		.antMatchers("/v2/api-docs/**").permitAll()
		.antMatchers("/configuration/**").permitAll()
		.antMatchers("/usuarios/**").permitAll()
		
		.anyRequest().authenticated()
		.and()
		.csrf().disable()
		.addFilterBefore(new StatelessLoginFilter("/api/login", tokenAuthenticationService, users, 
				authenticationManager()), UsernamePasswordAuthenticationFilter.class)		
		.addFilterBefore(new StatelessAuthenticationFilter(tokenAuthenticationService),
                UsernamePasswordAuthenticationFilter.class);

	}
	

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(users);
	}

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Bean
    public TokenAuthenticationService tokenAuthenticationService() {
        return tokenAuthenticationService;
    }    

}

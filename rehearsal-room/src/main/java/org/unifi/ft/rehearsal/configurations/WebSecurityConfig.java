package org.unifi.ft.rehearsal.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.unifi.ft.rehearsal.services.BandService;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String[] PUBLIC_ACCESS_URIS = { "/", "/register" };

	@Autowired
	private BandService bandService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()

				.anyRequest().authenticated()

				.and()

				.formLogin().loginPage("/login").usernameParameter("username").passwordParameter("password")

				.permitAll()

				.and()

				.logout()

				.permitAll();

	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(PUBLIC_ACCESS_URIS);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(bandService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

}

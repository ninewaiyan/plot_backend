package com.plot.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.plot.models.Wallet;
import com.plot.repositories.WalletRepository;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class AppConfig {
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
		
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.authorizeHttpRequests(Authorize->Authorize.requestMatchers("/api/**").authenticated()
		.anyRequest().permitAll())
		.addFilterBefore(new JwtTokenValidor(),BasicAuthenticationFilter.class)
		.csrf().disable()
		.cors().configurationSource(corsConfigurationSource()).and()
		.httpBasic().and().formLogin();
		
		return http.build();
		
	}

	private CorsConfigurationSource corsConfigurationSource() {
		// TODO Auto-generated method stub
		return new CorsConfigurationSource() {
			
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				// TODO Auto-generated method stub
				CorsConfiguration cfg = new CorsConfiguration();
				cfg.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
				cfg.setAllowedMethods(Collections.singletonList("*"));
				cfg.setAllowCredentials(true);
				cfg.setAllowedHeaders(Collections.singletonList("*"));
				cfg.setExposedHeaders(Arrays.asList("Authorization"));
				cfg.setMaxAge(3600L);
				return cfg;
			}
		};
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public CommandLineRunner createSystemWallet(WalletRepository walletRepository) {
	    return args -> {
	    	
            Optional<Wallet> systemWalletOptional = walletRepository.findByIsSystemWalletTrue();

	        if (systemWalletOptional.isEmpty()) {
	            Wallet systemWallet = new Wallet();
	            systemWallet.setSystemWallet(true);
	            systemWallet.setLikesBalance(1000000);
	            systemWallet.setTotalLikesReceived(0);
	            systemWallet.setUser(null); // System wallet not linked to a user
	            walletRepository.save(systemWallet);
	            System.out.println("[INIT] System wallet created.");
	        } else {
	        	System.out.println(walletRepository.findByIsSystemWalletTrue());
	            System.out.println("[INIT] System wallet already exists.");
	        }
	    };
	}



}

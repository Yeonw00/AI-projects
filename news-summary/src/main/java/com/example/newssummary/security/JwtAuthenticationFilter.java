package com.example.newssummary.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	@Autowired
	private JwtTokenProvider tokenProvider;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	private static final AntPathMatcher matcher = new AntPathMatcher();
	private static final List<String> WHITELIST = List.of(
		"/api/auth/login",
		"/api/auth/signup",
		"/api/auth/refresh",
		"/api/auth/google/**",
		"/api/auth/naver/**",
		"/api/auth/kakao/**", 
		"/oauth2/**",
		"/login/oauth2/**" , 
		"/login", 
		"/error"
	);
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
			throws ServletException, IOException {
		
		String header = request.getHeader("Authorization");
		
		
		if(header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7);
			if(tokenProvider.validateToken(token)) {
				String username = tokenProvider.getUsernameFromToken(token);
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken authentication = 
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		
		chain.doFilter(request, response);
	}
	
	@Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean skip = WHITELIST.stream().anyMatch(p -> matcher.match(p, path));

        return skip;
    }
}

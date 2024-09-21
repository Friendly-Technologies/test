package com.friendly.services.uiservices.auth;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.PostConstruct;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.UNSUPPORTED_AUTH_METHOD;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ImportResource("classpath:settings.xml")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final DefaultSpringSecurityContextSource ldapContextSource;

    @Value("${jwt.token.secret}")
    private String secret;
    @Value("${global.authenticationType:#{null}}")
    private String authenticationTypeString;
    private AuthType authenticationType;
    @Value("#{ldapProperties['ldap.userDnPatterns']}")
    private String userDnPatterns;
    @Value("#{ldapProperties['ldap.groupSearchBase']}")
    private String groupSearchBase;
    @Value("#{ldapProperties['ldap.groupSearchFilter']}")
    private String groupSearchFilter;
    @Value("#{adProperties['ad.domain']}")
    private String adDomain;
    @Value("#{adProperties['ad.url']}")
    private String adUrl;

    @PostConstruct
    public void init() {
        if (authenticationTypeString == null) {
            log.warn("Authentication type string is not provided. Using default value: {}", AuthType.DATABASE);
            this.authenticationType = AuthType.DATABASE;
        } else {
            this.authenticationType = AuthType.fromValue(authenticationTypeString);
        }
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        switch (authenticationType) {
            case DATABASE:
                break;
            case LDAP:
                auth.ldapAuthentication()
                        .userDnPatterns(userDnPatterns)
                        .groupSearchBase(groupSearchBase)
                        .groupSearchFilter(groupSearchFilter)
                        .contextSource(ldapContextSource);
                break;
            case WINDOWS:
                ActiveDirectoryLdapAuthenticationProvider adProvider = new ActiveDirectoryLdapAuthenticationProvider(adDomain, adUrl);
                adProvider.setConvertSubErrorCodesToExceptions(true);
                adProvider.setUseAuthenticationRequestCredentials(true);

                String searchFilter = "(&(objectClass=user)(userPrincipalName={0}))";
                adProvider.setSearchFilter(searchFilter);

                auth.authenticationProvider(adProvider);
                break;
            case SAML:
            default:
                throw new FriendlyIllegalArgumentException(UNSUPPORTED_AUTH_METHOD);
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final JwtFilter jwtFilter = new JwtFilter(secret);

        switch (authenticationType) {
            case DATABASE:
                http.csrf().disable()
                        .authorizeRequests()
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .antMatchers("/**/swagger-ui.html", "/**/v2/api-docs", "/**/swagger-resources/**", "/**/webjars/**",
                                "/**/swagger-ui/**", "/**/**/swagger-resources/**", "/**/**/swagger-resources", "/**/swagger-resources").permitAll()
                        .antMatchers("/**/iotw/Auth/login", "/**/iotw/Auth/logout", "/**/iotw/Auth/type",
                                "/**/iotw/Auth/password/reset", "/**/iotw/User/password/restore",
                                "/**/iotw/System/version", "/**/iotw/System/information",
                                "/**/iotw/Setting//interface/hideForgotPassword",
                                "/**/iotw/Setting/interface/passwordResetRetryCooldown").permitAll()
                        .anyRequest().authenticated()
                        .and()
                        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                        .headers()
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
                        .and()
                        .cors()
                        .and()
                        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                break;
            case LDAP:
                http.csrf().disable()
                        .authorizeRequests()
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .antMatchers("/**/swagger-ui.html", "/**/v2/api-docs", "/**/swagger-resources/**", "/**/webjars/**",
                                "/**/swagger-ui/**", "/**/**/swagger-resources/**", "/**/**/swagger-resources", "/**/swagger-resources").permitAll()
                        .antMatchers("/**/iotw/Auth/login", "/**/iotw/Auth/logout", "/**/iotw/Auth/type",
                                "/**/iotw/Auth/password/reset", "/**/iotw/User/password/restore",
                                "/**/iotw/System/version", "/**/iotw/System/information",
                                "/**/iotw/Setting//interface/hideForgotPassword",
                                "/**/iotw/Setting/interface/passwordResetRetryCooldown").permitAll()
                        .anyRequest().authenticated()
                        .and()
                        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                        .formLogin().disable()
                        .logout().disable()
                        .headers()
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
                        .and()
                        .cors()
                        .and()
                        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                break;
            case WINDOWS:
                http.csrf().disable()
                        .authorizeRequests()
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .antMatchers("/**/swagger-ui.html", "/**/v2/api-docs", "/**/swagger-resources/**", "/**/webjars/**",
                                "/**/swagger-ui/**", "/**/**/swagger-resources/**", "/**/**/swagger-resources", "/**/swagger-resources").permitAll()
                        .antMatchers("/**/iotw/Auth/login", "/**/iotw/Auth/logout", "/**/iotw/Auth/type",
                                "/**/iotw/Auth/password/reset", "/**/iotw/User/password/restore",
                                "/**/iotw/System/version", "/**/iotw/System/information",
                                "/**/iotw/Setting//interface/hideForgotPassword",
                                "/**/iotw/Setting/interface/passwordResetRetryCooldown").permitAll()
                        .anyRequest().authenticated()
                        .and()
                        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                        .formLogin().disable()
                        .logout().disable()
                        .headers()
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
                        .and()
                        .cors()
                        .and()
                        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                break;
            case SAML:
            default:
                throw new FriendlyIllegalArgumentException(UNSUPPORTED_AUTH_METHOD);
        }
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials (cookies, auth headers, etc.)
        config.setAllowCredentials(true);

        // Allow requests from any origin with credentials
        //config.addAllowedOrigin("http://iot.friendly-tech.com:85");
        config.addAllowedOrigin("*");

        // Allow all headers
        config.addAllowedHeader("*");

        // Allow all necessary HTTP methods
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");

        // Apply this CORS configuration to all paths
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
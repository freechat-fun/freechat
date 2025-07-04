package fun.freechat.config;

import fun.freechat.access.auth.ApiSwitchUserFilter;
import fun.freechat.access.auth.ApiTokenAuthenticationProvider;
import fun.freechat.access.auth.OAuth2TokenBasedRememberMeServices;
import fun.freechat.access.auth.customizer.OAuth2AuthorizationRequestCustomizer;
import fun.freechat.access.auth.handler.OAuth2AuthenticationFailureHandler;
import fun.freechat.access.auth.handler.OAuth2AuthenticationSuccessHandler;
import fun.freechat.access.user.SysUserDetailsManager;
import fun.freechat.access.user.SysUserPasswordEncoder;
import fun.freechat.api.access.RoleBasedPermissionEvaluator;
import fun.freechat.service.account.SysAuthorityService;
import fun.freechat.service.account.SysBindService;
import fun.freechat.service.account.SysUserService;
import fun.freechat.service.common.EncryptionService;
import fun.freechat.service.organization.OrgService;
import fun.freechat.util.AppMetaUtils;
import fun.freechat.util.AuthorityUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@SuppressWarnings("unused")
public class SecurityConfig {
    @Value("${auth.aes.key}")
    private String aesKey;
    @Value("${auth.role.adminUri:#{null}}")
    private String[] adminUri;
    @Value("${auth.role.bizAdminUri:#{null}}")
    private String[] bizAdminUri;
    @Value("${auth.role.apiUri:#{null}}")
    private String[] apiUri;
    @Value("${auth.role.privateUri:#{null}}")
    private String[] privateUri;
    @Value("${auth.role.publicUri:#{null}}")
    private String[] publicUri;
    @Value("${auth.login.uri}")
    private String loginUri;
    @Value("${auth.login.processingUri}")
    private String loginProcessingUri;
    @Value("${auth.login.oauth2.successUri}")
    private String oauth2LoginSuccessUri;
    @Value("${auth.login.oauth2.failureUri}")
    private String oauth2LoginFailureUri;
    @Value("${auth.login.portal.successUri}")
    private String portalLoginSuccessUri;
    @Value("${auth.login.portal.failureUri}")
    private String portalLoginFailureUri;
    @Value("${auth.login.oauth2.baseUri:#{T(org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter).DEFAULT_AUTHORIZATION_REQUEST_BASE_URI}}")
    private String authorizationRequestBaseUri;
    @Value("${auth.impersonate.headerName}")
    private String impersonateHeaderName;
    @Value("${auth.impersonate.autoRegister}")
    private Boolean impersonateAutoRegister;
    @Value("${auth.logout.uri}")
    private String logoutUri;
    @Value("${auth.logout.successUri}")
    private String logoutSuccessUri;
    @Value("${auth.rememberMe.alwaysRemember:false}")
    private Boolean alwaysRemember;

    private void configPrivatePath(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        if (ArrayUtils.isNotEmpty(privateUri)) {
            registry.requestMatchers(privateUri).denyAll();
        }
    }

    private void configPublicPath(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        if (ArrayUtils.isNotEmpty(publicUri)) {
            registry.requestMatchers(publicUri).permitAll().requestMatchers(oauth2LoginFailureUri).permitAll();
        }
    }

    private void configAdminPath(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        if (ArrayUtils.isNotEmpty(adminUri)) {
            registry.requestMatchers(adminUri).hasRole(AuthorityUtils.adminRole());
        }
    }

    private void configBizAdminPath(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        if (ArrayUtils.isNotEmpty(bizAdminUri)) {
            registry.requestMatchers(bizAdminUri).hasAnyRole(
                    AuthorityUtils.adminRole(), AuthorityUtils.bizRole());
        }
    }

    private void configApiPath(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        /*
        if (ArrayUtils.isNotEmpty(apiUri)) {
            registry.antMatchers(apiUri).hasRole(AuthorityUtils.clientRole());
        }
        */
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           ClientRegistrationRepository clientRegistrationRepository,
                                           OAuth2AuthorizationRequestCustomizer oAuth2AuthorizationRequestCustomizer,
                                           OAuth2AuthorizedClientService oAuth2ClientService,
                                           UserDetailsManager userDetailsManager,
                                           OrgService orgService,
                                           ApiTokenAuthenticationProvider apiTokenAuthenticationProvider,
                                           RedissonClient redissonClient
                                           ) throws Exception {
        http.authorizeHttpRequests(registry -> {
            configPrivatePath(registry);
            configPublicPath(registry);
            configAdminPath(registry);
            configApiPath(registry);
            registry.anyRequest().authenticated();
        });

        http.userDetailsService(userDetailsManager);

        http.logout(logout ->
                logout.logoutUrl(logoutUri)
                        .logoutSuccessUrl(logoutSuccessUri)
                        .permitAll()
        );

        http.formLogin(portal ->
                portal.loginPage(loginUri)
                        .loginProcessingUrl(loginProcessingUri)
                        .defaultSuccessUrl(portalLoginSuccessUri)
                        .failureUrl(portalLoginFailureUri)
                        .permitAll()
        );

        SysUserDetailsManager sysUserDetailsManager = (SysUserDetailsManager) userDetailsManager;

        DefaultOAuth2AuthorizationRequestResolver oAuth2RequestResolver =  new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, authorizationRequestBaseUri);
        oAuth2RequestResolver.setAuthorizationRequestCustomizer(oAuth2AuthorizationRequestCustomizer);

        OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler = new OAuth2AuthenticationSuccessHandler(
                oAuth2ClientService, sysUserDetailsManager, oAuth2AuthorizationRequestCustomizer);
        oAuth2SuccessHandler.setDefaultTargetUrl(oauth2LoginSuccessUri);

        OAuth2AuthenticationFailureHandler oAuth2FailureHandler = new OAuth2AuthenticationFailureHandler(
                oAuth2AuthorizationRequestCustomizer);
        oAuth2FailureHandler.setDefaultFailureUrl(oauth2LoginFailureUri);

        http.oauth2Login(oauth2 ->
                oauth2.loginPage(loginUri)
                        .authorizationEndpoint(endpoint ->
                                endpoint.authorizationRequestResolver(oAuth2RequestResolver))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                        .permitAll()
        );

        if (ArrayUtils.isNotEmpty(apiUri)) {
            http.addFilterAfter(
                    apiAuthenticationFilter(apiTokenAuthenticationProvider), UsernamePasswordAuthenticationFilter.class);
            http.addFilter(apiSwitchUserFilter(sysUserDetailsManager, orgService));
            http.cors(corsConf -> corsConf.configurationSource(corsConfigurationSource()));
            http.csrf(csrfConf -> csrfConf.ignoringRequestMatchers(apiUri));
        }

        http.rememberMe(rememberMe ->
                rememberMe.rememberMeServices(rememberMeServices(userDetailsManager, redissonClient)));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(EncryptionService encryptionService) {
        return new SysUserPasswordEncoder(encryptionService);
    }

    @Bean
    public UserDetailsManager users(SysUserService userService,
                                    SysBindService bindService,
                                    SysAuthorityService authorityService,
                                    PasswordEncoder passwordEncoder) {
        return new SysUserDetailsManager(userService, bindService, authorityService, passwordEncoder);
    }

    private RequestMatcher toRequestMatcher(String[] paths) {
        return new OrRequestMatcher(Arrays.stream(paths)
                .map(path -> PathPatternRequestMatcher.withDefaults().matcher(path))
                .map(RequestMatcher.class::cast)
                .toList());
    }

    @Bean
    public static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            RoleBasedPermissionEvaluator roleBasedPermissionEvaluator) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(roleBasedPermissionEvaluator);
        return handler;
    }

    private RememberMeServices rememberMeServices(
            UserDetailsService userDetailsService, RedissonClient redissonClient) {
        OAuth2TokenBasedRememberMeServices rememberMeServices =
                new OAuth2TokenBasedRememberMeServices(aesKey, userDetailsService, redissonClient);
        rememberMeServices.setAlwaysRemember(alwaysRemember);
        return rememberMeServices;
    }

    private AuthenticationFilter apiAuthenticationFilter(ApiTokenAuthenticationProvider provider) {
        AuthenticationFilter filter = new AuthenticationFilter(provider::authenticate, provider);
        filter.setRequestMatcher(toRequestMatcher(apiUri));
        filter.setSuccessHandler((request, response, authentication) -> {});
        return filter;
    }

    private SwitchUserFilter apiSwitchUserFilter(SysUserDetailsManager sysUserDetailsManager,
                                                 OrgService orgService) {
        ApiSwitchUserFilter filter = new ApiSwitchUserFilter();
        filter.setHeaderName(impersonateHeaderName);
        filter.setAutoRegister(impersonateAutoRegister);
        filter.setUserDetailsService(sysUserDetailsManager);
        filter.setOrgService(orgService);
        filter.setSwitchUserMatcher(toRequestMatcher(apiUri));
        filter.setSuccessHandler((request, response, authentication) -> {});
        filter.setFailureHandler((request, response, exception) -> {});
        return filter;
    }

    private CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        if (AppMetaUtils.isTestEnv()) {
            List<String> allowedOriginList = List.of(
                    "http://localhost",
                    "http://localhost:3000",
                    "http://127.0.0.1",
                    "http://127.0.0.1:8080"
            );

            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedMethods(List.of("*"));
            configuration.setAllowedOriginPatterns(allowedOriginList);
            configuration.setAllowCredentials(true);
            configuration.applyPermitDefaultValues();

            for (String uri : apiUri) {
                source.registerCorsConfiguration(uri, configuration);
            }
            for (String uri : publicUri) {
                source.registerCorsConfiguration(uri, configuration);
            }
        }

        return source;
    }
}

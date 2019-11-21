package ca.gc.dfo.psffs.configuration;

import ca.gc.dfo.psffs.controllers.*;
import ca.gc.dfo.psffs.web.security.SecurityHelper;
import ca.gc.dfo.spring_commons.commons_offline_wet.configuration.EAccessWebSecurityConfigAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends EAccessWebSecurityConfigAdapter
{
    private static final String[] DISABLE_CSRF_FOR_PATTERNS = new String[]{
            ChecksumObjectController.CHECKSUM_OBJECTS_PATH, LengthFrequencyController.LENGTH_FREQUENCY_SYNC_DATA_PATH,
            LengthFrequencyController.LENGTH_FREQUENCY_DELETE_PATH, SamplingDataController.SAMPLING_DATA_FORM_PATH,
            SamplingDataController.SAMPLING_DATA_PATCH_PATH, ObserverController.OBSERVER_DELETE_PATH,
            LengthFrequencyController.LENGTH_FREQUENCY_MARK_PATH, ObserverController.OBSERVER_MARK_PATH,
            ServerToolsController.EXTRACT_EXECUTE_PATH, SamplingDataController.SAMPLING_DATA_MARK_PATH
    };

    class CsrfRequestMatcher implements RequestMatcher
    {
        private AntPathRequestMatcher[] requestMatchers;
        private Pattern allowedMethods = Pattern.compile("^(GET|PATCH|HEAD|TRACE|OPTIONS)$");
        CsrfRequestMatcher(String[] disableCSRFForPatterns)
        {
            AntPathRequestMatcher[] requestMatchers = new AntPathRequestMatcher[disableCSRFForPatterns.length];
            for (int x = 0; x < disableCSRFForPatterns.length; x++) {
                requestMatchers[x] = new AntPathRequestMatcher(disableCSRFForPatterns[x]);
            }
            this.requestMatchers = requestMatchers;
        }

        @Override
        public boolean matches(HttpServletRequest request)
        {
            boolean answer = true;
            if (allowedMethods.matcher(request.getMethod().toUpperCase()).matches()) {
                answer = false;
            } else {
                for (AntPathRequestMatcher matcher : this.requestMatchers) {
                    if (matcher.matches(request)) {
                        answer = false;
                        break;
                    }
                }
            }
            return answer;
        }
    }

    @Override
    public void configure(WebSecurity web)
    {
        web
            .ignoring().antMatchers("/css/**", "/img/**", "/js/**", "/console/**");
    }

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception
    {
        http
            .headers().frameOptions().sameOrigin()
                .and()
                    .headers().httpStrictTransportSecurity().disable()
                .and()
                    .csrf().requireCsrfProtectionMatcher(new CsrfRequestMatcher(DISABLE_CSRF_FOR_PATTERNS))
                .and()
                    .authorizeRequests().anyRequest().hasRole(SecurityHelper.ROLE_BASE_ACCESS);
    }
}

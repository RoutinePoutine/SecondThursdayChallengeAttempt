package com.example.exercisefourohone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public static BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private SSUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return new SSUserDetailsService(userRepository);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // include CSS FILES BELOW (WHERE IT SAYS HERE)

        http
                    .authorizeRequests()
                    .antMatchers("/", "/h2-console/**", "/register", "/css/**").permitAll() // INCLUDE CSS FILES HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                    // temporary .antmaters = just to get site running --> after below, still no form
                    .antMatchers("/mesageform").permitAll()
                    // css is blocked, how about: below was necessary
                    .antMatchers("https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css").permitAll()
                    .antMatchers("/list", "/update").permitAll()

                    // show file blocked
                    .antMatchers("/show", "/detail", "/detail/{id}").permitAll()
                // below from StackOverflow
                    .antMatchers("/detail/{id}/**").access("authorise(#id)")

                  // I have to free up Bootstrap's three files
                    .antMatchers("https://code.jquery.com/jquery-3.4.1.slim.min.js").permitAll()
                    .antMatchers("https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js").permitAll()
                    .antMatchers("https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js").permitAll()



                .antMatchers("/admin")
                    .access("hasAuthority('ADMIN')")
                    .anyRequest().authenticated()
                    .and().formLogin().loginPage("/login").permitAll()
                    .and()
                    .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login").permitAll().permitAll().and().httpBasic();

                     http.csrf().disable();
                     http.headers().frameOptions().disable();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{

        auth.userDetailsService(userDetailsServiceBean())
                .passwordEncoder(passwordEncoder());
// comment out lines below?
//        auth.inMemoryAuthentication().withUser("dave")
//                .password(passwordEncoder().encode("begreat")).authorities("ADMIN").and().withUser("user")
//                .password(passwordEncoder().encode("password"))
//                .authorities("USER");
    }
}



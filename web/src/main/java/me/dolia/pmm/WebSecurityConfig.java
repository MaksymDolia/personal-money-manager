package me.dolia.pmm;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private DataSource dataSource;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.jdbcAuthentication()
        .authoritiesByUsernameQuery("SELECT app_user.email, role.name FROM app_user "
            + "                               JOIN app_user_roles ON app_user.email = app_user_roles.users_email JOIN role "
            + "                               ON app_user_roles.roles_id = role.id WHERE app_user.email = ?")
        .usersByUsernameQuery("SELECT email, password,1 FROM app_user WHERE email = ?")
        .dataSource(dataSource)
        .passwordEncoder(passwordEncoder());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/login", "/", "/logout").permitAll()
        .and()
        .formLogin()
        .loginPage("/login")
        .failureUrl("/login")
        .defaultSuccessUrl("/app")
        .usernameParameter("email")
        .and()
        .logout()
        .logoutSuccessUrl("/?message=logout_success")
        .and()
        .authorizeRequests()
        .antMatchers("/app**").hasRole("USER")
        .antMatchers("/app/**").hasRole("USER")
        .antMatchers("/profile/**").hasRole("USER")
        .antMatchers(HttpMethod.POST, "/signin").anonymous();
  }
}

package com.eparking.eparking.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter =  new CustomAuthenticationFilter(authenticationManagerBean());
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        http.authorizeRequests().antMatchers(
                "/user/loginUser/**",
                "/user/registerUser/**",
                "/user/confirmOTP/**",
                "/payment/returnPayment/**",
                "/user/forgotPassword/**",
                "/user/newPasword/**",
                "/swagger-ui.html", "/webjars/**", "/v2/api-docs", "/swagger-resources/**"
        ).permitAll()
                .antMatchers(HttpMethod.PUT, "/user/confirmOTP/**").permitAll()
                .antMatchers(HttpMethod.POST, "/user/confirmOTP/**").permitAll();
        http.authorizeRequests().antMatchers(PUT, "/user/updateUser").hasAnyAuthority("ROLE_SUPPLIER", "ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers(POST,"/car/addCar").hasAnyAuthority("ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers(POST,"/parking/createParking").hasAnyAuthority("ROLE_SUPPLIER");
        http.authorizeRequests().antMatchers(POST,"/parking/addDatesForParking").hasAnyAuthority("ROLE_SUPPLIER");
        http.authorizeRequests().antMatchers(POST,"/parking/addSpecialDatesForParking").hasAnyAuthority("ROLE_SUPPLIER");
        http.authorizeRequests().antMatchers(POST,"/parking/createSpecialDate").hasAnyAuthority("ROLE_SUPPLIER");
        http.authorizeRequests().antMatchers(GET,"/cart/getListReservation").hasAnyAuthority("ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers(GET,"/reservation/getListOrder").hasAnyAuthority("ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers(GET,"/payment/createPayment").hasAnyAuthority("ROLE_CUSTOMER","ROLE_SUPPLIER");
        http.authorizeRequests().antMatchers(GET,"/payment/updateWallet").hasAnyAuthority("ROLE_CUSTOMER","ROLE_SUPPLIER");
        http.authorizeRequests().antMatchers(POST,"/reservation/createReservation").hasAnyAuthority("ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/user/getUserProfile").hasAnyAuthority("ROLE_SUPPLIER", "ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/user/getRoleByUserID").hasAnyAuthority("ROLE_SUPPLIER", "ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/reservation/getReservationByID").hasAnyAuthority("ROLE_SUPPLIER", "ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/reservation/updateStatus").hasAnyAuthority("ROLE_SUPPLIER", "ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/revenue/parking/today/**").hasAnyAuthority("ROLE_SUPPLIER");
        http.authorizeRequests().antMatchers(GET, "/revenue/allParking/today").hasAnyAuthority("ROLE_SUPPLIER");
        http.authorizeRequests().antMatchers(GET, "/revenue/parking/cal").hasAnyAuthority("ROLE_SUPPLIER");
        http.authorizeRequests().antMatchers(GET, "/revenue/allParking/cal").hasAnyAuthority("ROLE_SUPPLIER");
        http.authorizeRequests().antMatchers(PUT, "/parking/updatePricing").hasAnyAuthority("ROLE_SUPPLIER");
        http.authorizeRequests().antMatchers(POST, "/car/addCar").hasAnyAuthority("ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers(DELETE, "/car/removeCar").hasAnyAuthority("ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/car/showCarsInParkingByStatus").hasAnyAuthority("ROLE_SUPPLIER");
        http.authorizeRequests().antMatchers(GET, "/car/getListCarByUser").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_SUPPLIER");
        http.authorizeRequests().antMatchers(GET, "/parking/getParkingDetail/**").hasAnyAuthority("ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/parking/getListParking").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_SUPPLIER");
        http.authorizeRequests().antMatchers(GET, "/parking/searchNearbyParking").hasAnyAuthority("ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers(POST, "/reservation/createReservation").hasAnyAuthority("ROLE_CUSTOMER");
        http.authorizeRequests().antMatchers(PUT, "/parking/updateSlotOfParking").hasAnyAuthority("ROLE_SUPPLIER");
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }
}

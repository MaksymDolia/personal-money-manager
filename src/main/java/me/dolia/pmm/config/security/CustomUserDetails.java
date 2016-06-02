package me.dolia.pmm.config.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.dolia.pmm.role.Role;
import me.dolia.pmm.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static java.util.stream.Collectors.toList;


public class CustomUserDetails extends User implements UserDetails {

    public CustomUserDetails(User user) {
        setPassword(user.getPassword());
        setEmail(user.getEmail());
        setCreated(user.getCreated());
        setId(user.getId());
        setEnable(user.getEnable());
        setRoles(user.getRoles());
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream()
                .map(Role::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(toList());
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return getEmail();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return super.getEnable();
    }
}
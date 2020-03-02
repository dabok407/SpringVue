package com.mk.vue.security;

import java.util.ArrayList;
import java.util.List;

import com.mk.vue.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 인증 프로바이더
 * 로그인시 사용자가 입력한 아이디와 비밀번호를 확인하고 해당 권한을 주는 클래스
 */
@Component("authProvider")
public class AuthProvider implements AuthenticationProvider  {

    @Autowired
    UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String id = authentication.getName();
        /*String password = HashUtil.sha256(authentication.getCredentials().toString());*/
        String password = String.valueOf(authentication.getCredentials());

        /*UserDto user = userService.selectUser(new UserDto(id));*/
        UserDetails user = userService.loadUserByUsername(id);

        if (null == user){
            throw new UsernameNotFoundException(id);
        }else if(!user.getPassword().equals(password)){
            userService.updateLoginFailCount(id); /* 로그인 실패 count update */
            throw new BadCredentialsException(id);
        }else if(!user.isAccountNonLocked()){
            throw new LockedException(id);
        }else if(!user.isEnabled()){
            throw new DisabledException(id);
        }else if(!user.isAccountNonExpired()){
            throw new AccountExpiredException(id);
        }else if(!user.isCredentialsNonExpired()){
            throw new CredentialsExpiredException(id);
        }

        /* 최근로그인 update */
        userService.updateLoginDt(id);

        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(id, password, user.getAuthorities());
        token.setDetails(user);
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}

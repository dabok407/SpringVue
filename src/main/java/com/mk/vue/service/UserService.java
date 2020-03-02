package com.mk.vue.service;

import com.mk.vue.model.entity.AdminUser;
import com.mk.vue.model.network.Header;
import com.mk.vue.repository.AdminUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private AdminUserRepository adminUserRepository;

    @Transactional
    public Long joinUser(AdminUser adminUser) {
        // 비밀번호 암호화
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));

        return adminUserRepository.save(adminUser).getId();
    }

    public void updateLoginDt(String account){

        Optional<AdminUser> optional = adminUserRepository.findByAccount(account);

        optional.map(adminUser -> {
            adminUser.setLastLoginAt(LocalDateTime.now());
            return adminUser;
        })
        .map(adminUser -> adminUserRepository.save(adminUser))             // update -> newUser
        .map(Header::OK)
        .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    public void updateLoginFailCount(String account){

        Optional<AdminUser> optional = adminUserRepository.findByAccount(account);

        optional.map(adminUser -> {
            adminUser.setLoginFailCount(adminUser.getLoginFailCount()+1);
            return adminUser;
        })
                .map(adminUser -> adminUserRepository.save(adminUser))             // update -> newUser
                .map(Header::OK)
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        Optional<AdminUser> userEntityWrapper = Optional.ofNullable(adminUserRepository.findFirstByAccount(account));
        if(userEntityWrapper.isPresent()){
            AdminUser adminUser = userEntityWrapper.get();

            List<GrantedAuthority> authorities = new ArrayList<>();

            if (("ROLE_ADMIN").equals(adminUser.getRole())) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            } else {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }
            return new User(adminUser.getAccount(), adminUser.getPassword(), authorities);
        }else{
            throw new UsernameNotFoundException(account);
        }

    }
}
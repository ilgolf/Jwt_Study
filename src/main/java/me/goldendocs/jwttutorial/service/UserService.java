package me.goldendocs.jwttutorial.service;

import javassist.bytecode.DuplicateMemberException;
import lombok.RequiredArgsConstructor;
import me.goldendocs.jwttutorial.dto.UserDto;
import me.goldendocs.jwttutorial.model.Authority;
import me.goldendocs.jwttutorial.model.User;
import me.goldendocs.jwttutorial.repository.UserRepository;
import me.goldendocs.jwttutorial.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    // 회원 가입
    @Transactional
    public User signUp(UserDto userDto) throws DuplicateMemberException {
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }

        // 빌더 패턴의 장점
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .username(userDto.getUsername())
                .password(encoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        return userRepository.save(user);
    }

    // username을 기준으로 정보를 갖고오는 메서드
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username);
    }

    // SecurityContext에 저장된 username의 정보만 가져오는 메서드
    @Transactional(readOnly = true)
    public Optional<User> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
    }
}

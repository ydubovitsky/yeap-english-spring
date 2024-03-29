package ru.ydubovitsky.yeapenglish.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.ydubovitsky.yeapenglish.security.config.JwtConfig;
import ru.ydubovitsky.yeapenglish.security.model.AppUser;
import ru.ydubovitsky.yeapenglish.security.model.roles.RolesEnum;
import ru.ydubovitsky.yeapenglish.security.repository.AppUserRepository;

import java.util.Set;


@RequiredArgsConstructor
@Slf4j
@Component
public class CustomCommandLineRunner implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;

    @Override
    public void run(String... args) throws Exception {
        appUserRepository.findByUsername(jwtConfig.getInitAdminName())
                .ifPresentOrElse(
                        user -> log.info("Admin exists already"),
                        this::createAdminUserOnStartup);
    }

    private void createAdminUserOnStartup() {
        AppUser admin = AppUser.builder()
                .username(jwtConfig.getInitAdminName())
                .password(passwordEncoder.encode(jwtConfig.getInitAdminPassword()))
                .password2(passwordEncoder.encode(jwtConfig.getInitAdminPassword()))
                .roles(Set.of(RolesEnum.ADMIN, RolesEnum.OWNER))
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .isEnabled(true)
                .build();
        appUserRepository.save(admin);
        log.info("Admin user created");
    }
}


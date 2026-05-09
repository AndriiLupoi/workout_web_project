//package org.lupoi.workoutapp.infrastructure.seed;/*
//    @author Andrii
//    @project workout
//    @class UserDataSeeder
//    @version 1.0.0
//    @since 09.05.2026 - 13.49
//*/
//
//import lombok.RequiredArgsConstructor;
//import org.lupoi.workoutapp.domain.enums.Role;
//import org.lupoi.workoutapp.infrastructure.document.user.UserDocument;
//import org.lupoi.workoutapp.infrastructure.repository.MongoUserRepository;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class UserDataSeeder implements ApplicationRunner {
//
//    private final MongoUserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(ApplicationArguments args) {
//
//        // щоб не дублювало при кожному запуску
//        if (userRepository.count() >= 30) {
//            return;
//        }
//
//        List<UserDocument> users = new ArrayList<>();
//
//        for (int i = 1; i <= 30; i++) {
//
//            String email = "user" + i + "@example.com";
//
//            // якщо такий юзер вже є — пропускаємо
//            if (userRepository.existsByEmail(email)) {
//                continue;
//            }
//
//            users.add(
//                    UserDocument.builder()
//                            .email(email)
//                            .firstName("User" + i)
//                            .lastName("Test" + i)
//                            .passwordHash(passwordEncoder.encode("Password123!"))
//                            .createdAt(LocalDateTime.now())
//                            .role(i % 10 == 0 ? Role.ADMIN : Role.USER)
//                            .build()
//            );
//        }
//
//        userRepository.saveAll(users);
//
//        System.out.println("Seeded users: " + users.size());
//    }
//}

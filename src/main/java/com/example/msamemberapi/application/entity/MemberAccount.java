package com.example.msamemberapi.application.entity;

import com.example.msamemberapi.application.enums.AccountType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberAccount {

    @Id
    private String id;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    private String password;
    @Builder.Default
    private LocalDateTime lastLoggedInAt = LocalDateTime.now();

    public void changePassword(String password) {
        this.password = password;
    }

    public void updateLastLoggedInAt() {
        this.lastLoggedInAt = LocalDateTime.now();
    }

    public void updatePassword(String password) {
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
    }
}


package com.example.msamemberapi.application.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Member {

    @Id
    private String id;
    private String password;
    @ElementCollection(fetch = FetchType.LAZY) @Builder.Default
    private List<String> roles = new ArrayList<>();

    public void addRole(String role) {
        roles.add(role);
    }
}

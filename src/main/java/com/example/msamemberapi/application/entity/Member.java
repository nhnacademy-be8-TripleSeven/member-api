package com.example.msamemberapi.application.entity;

import com.example.msamemberapi.application.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String loginId;
    private String password;
    @ElementCollection(fetch = FetchType.LAZY) @Builder.Default
    private List<String> roles = new ArrayList<>();
    private String name;
    private Date birth;
    @Enumerated(EnumType.STRING)
    private Gender gender;


    public void addRole(String role) {
        roles.add(role);
    }
}

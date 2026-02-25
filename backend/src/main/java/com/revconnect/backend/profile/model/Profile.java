package com.revconnect.backend.profile.model;

import com.revconnect.backend.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 1000)
    private String bio;

    private String profilePicturePath;
    private String location;
    private String website;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
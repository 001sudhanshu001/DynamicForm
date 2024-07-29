package com.learn.entity;

import com.learn.constants.TableNames;
import com.learn.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Table(name = TableNames.USERS,
        indexes = {
            @Index(name = "user_email_index", columnList = "email")
        }
)
@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "appUser")
    private Set<FilledHtmlForm> filledHtmlFormSet;

    @OneToMany(mappedBy = "appUser")
    private List<UserSession> userSessions;

    @OneToMany(mappedBy = "appUser")
    private List<HtmlForm> htmlForms;

}

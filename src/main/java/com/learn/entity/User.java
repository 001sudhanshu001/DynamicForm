package com.learn.entity;

import com.learn.constants.TableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Table(name = TableNames.USERS,
        indexes = {
            @Index(name = "user_email_index", columnList = "email")
        }
)
@Entity
@Getter @Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @OneToMany(mappedBy = "user")
    private Set<FilledHtmlForm> filledHtmlFormSet;
}

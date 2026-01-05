package com.invest.Trading.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.invest.Trading.Domain.USER_ROLE;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fullName;
    private String email;
    private String mobile;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private boolean isVerified = false;

    private BigDecimal balance = BigDecimal.ZERO;

    private USER_ROLE role = USER_ROLE.ROLE_CUSTOMER;

}

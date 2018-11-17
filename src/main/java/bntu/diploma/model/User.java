package bntu.diploma.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Column(unique = true, nullable = false)
    private String userName;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @JsonIgnore
    @Column(unique = true, nullable = false)
    private String apiKey;

    @JsonIgnore
    @Column(unique = true, nullable = false)
    private String encryptionKey;

    /**
     * Indicates what access is allowed for the user
     * <p>
     * If accessLevel equals 1 the user has an admin level access
     * If accessLevel equals 2 the user has an user level access
     */
    @Column(nullable = false)
    private Integer accessLevel;

    public User() {
    }

    public User(String userName, String password, String apiKey, Integer accessLevel) {
        this.userName = userName;
        this.password = password;
        this.apiKey = apiKey;
        this.accessLevel = accessLevel;
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getApiKey() {
        return apiKey;
    }

    public Integer getAccessLevel() {
        return accessLevel;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }
}

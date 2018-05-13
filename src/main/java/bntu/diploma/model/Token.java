package bntu.diploma.model;

import javax.persistence.*;

@Entity
public class Token {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long tokenId;

    // many to one by user's id
    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private User user;

    // unique
    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Boolean expired;

    @Column(nullable = false)
    private String loginDateTime;

    // can be null
    private String logoutDateTime;


    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public String getLoginDateTime() {
        return loginDateTime;
    }

    public void setLoginDateTime(String loginDateTime) {
        this.loginDateTime = loginDateTime;
    }

    public String getLogoutDateTime() {
        return logoutDateTime;
    }

    public void setLogoutDateTime(String logoutDateTime) {
        this.logoutDateTime = logoutDateTime;
    }
}

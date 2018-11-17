package bntu.diploma.model;

import javax.persistence.*;

@Entity
public class Oblast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long oblastsId;

    @Column(nullable = false)
    private String name;

    public Oblast() {
    }

    public Oblast(Long id, String name) {
        this.oblastsId = id;
        this.name = name;
    }

    public Long getOblastsId() {
        return oblastsId;
    }

    public void setOblastsId(Long oblastsId) {
        this.oblastsId = oblastsId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

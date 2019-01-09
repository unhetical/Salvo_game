package com.java.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity

public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    private Long id;
    private String email;
    private String password;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayerSet = new HashSet<>();

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<Score> ScoreSet= new HashSet<>();

    public Player() {}

    public Player(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<GamePlayer> getGamePlayer() {
        return gamePlayerSet;
    }

    public void setGameplayer(Set<GamePlayer> gamePlayer) {
        this.gamePlayerSet = gamePlayer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<GamePlayer> getGamePlayerSet() {
        return gamePlayerSet;
    }

    public void setGamePlayerSet(Set<GamePlayer> gamePlayerSet) {
        this.gamePlayerSet = gamePlayerSet;
    }

    public Set<Score> getScoreSet() {
        return ScoreSet;
    }

    public void setScoreSet(Set<Score> scoreSet) {
        ScoreSet = scoreSet;
    }

    public Score getScore(Game game){
        return ScoreSet.stream().filter(sc -> sc.getGame().equals(game)).findFirst().orElse(null);
 }

}


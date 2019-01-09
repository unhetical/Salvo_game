package com.java.salvo;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    private Game game;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    private Set<Ship> ShipSet= new HashSet<>();

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    private Set<Salvo> SalvoSet= new HashSet<>();

    public GamePlayer() {}

    public GamePlayer(Player player, Game game) {
        this.player = player;
        this.game = game;
        this.date = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Set<Ship> getShipSet() {
        return ShipSet;
    }

    public void setShipSet(Set<Ship> shipSet) {
        ShipSet = shipSet;
    }

    public Set<Salvo> getSalvoSet() {
        return SalvoSet;
    }

    public void setSalvoSet(Set<Salvo> salvoSet) {
        SalvoSet = salvoSet;
    }

    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        ShipSet.add(ship);
    }
public void addSalvo(Salvo salvo){
        salvo.setGamePlayer(this);
        SalvoSet.add(salvo);
}

public Score getScore(){
        return getPlayer().getScore(this.game);
    }

}




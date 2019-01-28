package com.java.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;


@SpringBootApplication
public class SalvoApplication {

        public static void main(String[] args) {
            SpringApplication.run(SalvoApplication.class);
        }

        private String name = "";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Bean
        public CommandLineRunner initData(PlayerRepository playerRepository,
                                          GameRepository gameRepository,
                                          GamePlayerRepository gamePlayerRepository,
                                          ShipRepository shipRepository,
                                          SalvoRepository salvoRepository,
                                          ScoreRepository scoreRepository) {
            return (args) -> {

                //PLAYERS
                Player p1 = new Player ("j.bauer@ctu.gov", "24");
                playerRepository.save(p1);
                Player p2 = new Player("c.obrian@ctu.gov", "42");
                playerRepository.save(p2);
                Player p3 = new Player("kim_bauer@gmail.com", "kb");
                playerRepository.save(p3);
                Player p4 = new Player("t.almeida@ctu.gov", "mole");
                playerRepository.save(p4);

                //GAMES
                Game g1 = new Game();
                gameRepository.save(g1);
                Game g2 = new Game();
                gameRepository.save(g2);
                Game g3 = new Game();
                gameRepository.save(g3);
                Game g4 = new Game();
                gameRepository.save(g4);
                Game g5 = new Game();
                gameRepository.save(g5);
                Game g6 = new Game();
                gameRepository.save(g6);
                Game g7 = new Game();
                gameRepository.save(g7);
                Game g8 = new Game();
                gameRepository.save(g8);

                // GAMEPLAYERS
                GamePlayer gp1 = new GamePlayer(p1, g1);
                gamePlayerRepository.save(gp1);
                GamePlayer gp2 = new GamePlayer(p2, g1);
                gamePlayerRepository.save(gp2);
                GamePlayer gp3 = new GamePlayer(p1, g2);
                gamePlayerRepository.save(gp3);
                GamePlayer gp4 = new GamePlayer(p2, g2);
                gamePlayerRepository.save(gp4);
                GamePlayer gp5 = new GamePlayer(p2, g3);
                gamePlayerRepository.save(gp5);
                GamePlayer gp6 = new GamePlayer(p4, g3);
                gamePlayerRepository.save(gp6);
                GamePlayer gp7 = new GamePlayer(p2, g4);
                gamePlayerRepository.save(gp7);
                GamePlayer gp8 = new GamePlayer(p1, g4);
                gamePlayerRepository.save(gp8);
                GamePlayer gp9 = new GamePlayer(p4, g5);
                gamePlayerRepository.save(gp9);
                GamePlayer gp10 = new GamePlayer(p1, g5);
                gamePlayerRepository.save(gp10);
                GamePlayer gp11 = new GamePlayer(p3, g6);
                gamePlayerRepository.save(gp11);
                GamePlayer gp12 = new GamePlayer(p4, g7);
                gamePlayerRepository.save(gp12);
                GamePlayer gp13 = new GamePlayer(p3, g8);
                gamePlayerRepository.save(gp13);
                GamePlayer gp14 = new GamePlayer(p4, g8);
                gamePlayerRepository.save(gp14);

                //SHIPS
                Ship sh1 = new Ship("Destroyer", Arrays.asList("H2", "H3", "H4"));
                gp1.addShip(sh1);
                shipRepository.save(sh1);
                Ship sh2 = new Ship("Submarine", Arrays.asList("E1", "F1", "G1"));
                gp1.addShip(sh2);
                shipRepository.save(sh2);
                Ship sh3 = new Ship("PatrolBoat", Arrays.asList("B4", "B5"));
                gp1.addShip(sh3);
                shipRepository.save(sh3);
                Ship sh4 = new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"));
                gp2.addShip(sh4);
                shipRepository.save(sh4);
                Ship sh5 = new Ship("PatrolBoat", Arrays.asList("F1", "F2"));
                gp2.addShip(sh5);
                shipRepository.save(sh5);
                Ship sh6 = new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"));
                gp3.addShip(sh6);
                shipRepository.save(sh6);
                Ship sh7 = new Ship("PatrolBoat", Arrays.asList("C6", "C7"));
                gp3.addShip(sh7);
                shipRepository.save(sh7);
                Ship sh8 = new Ship("Submarine", Arrays.asList("A2", "A3", "A4"));
                gp4.addShip(sh8);
                shipRepository.save(sh8);
                Ship sh9 = new Ship("PatrolBoat", Arrays.asList("G6", "H6"));
                gp4.addShip(sh9);
                shipRepository.save(sh9);
                Ship sh10 = new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"));
                gp5.addShip(sh10);
                shipRepository.save(sh10);
                Ship sh11 = new Ship("PatrolBoat", Arrays.asList("C6", "C7"));
                gp5.addShip(sh11);
                shipRepository.save(sh11);
                Ship sh12 = new Ship("Submarine", Arrays.asList("A2", "A3", "A4"));
                gp6.addShip(sh12);
                shipRepository.save(sh12);
                Ship sh13 = new Ship("PatrolBoat", Arrays.asList("G6", "H6"));
                gp6.addShip(sh13);
                shipRepository.save(sh13);
                Ship sh14 = new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"));
                gp7.addShip(sh14);
                shipRepository.save(sh14);
                Ship sh15 = new Ship("PatrolBoat", Arrays.asList("C6", "C7"));
                gp7.addShip(sh15);
                shipRepository.save(sh15);
                Ship sh16 = new Ship("Submarine", Arrays.asList("A2", "A3", "A4"));
                gp8.addShip(sh16);
                shipRepository.save(sh16);
                Ship sh17 = new Ship("PatrolBoat", Arrays.asList("G6", "H6"));
                gp8.addShip(sh17);
                shipRepository.save(sh17);
                Ship sh18 = new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"));
                gp9.addShip(sh18);
                shipRepository.save(sh18);
                Ship sh19 = new Ship("PatrolBoat", Arrays.asList("C6", "C7"));
                gp8.addShip(sh19);
                shipRepository.save(sh19);
                Ship sh20 = new Ship("Submarine", Arrays.asList("A2", "A3", "A4"));
                gp8.addShip(sh20);
                shipRepository.save(sh20);
                Ship sh21 = new Ship("PatrolBoat", Arrays.asList("G6", "H6"));
                gp8.addShip(sh21);
                shipRepository.save(sh21);
                Ship sh22 = new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"));
                gp8.addShip(sh22);
                shipRepository.save(sh22);
                Ship sh23 = new Ship("PatrolBoat", Arrays.asList("C6", "C7"));
                gp9.addShip(sh23);
                shipRepository.save(sh23);
                Ship sh24 = new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"));
                gp10.addShip(sh24);
                shipRepository.save(sh24);
                Ship sh25 = new Ship("PatrolBoat", Arrays.asList("C6", "C7"));
                gp10.addShip(sh25);
                shipRepository.save(sh25);
                Ship sh26 = new Ship("Submarine", Arrays.asList("A2", "A3", "A4"));
                gp11.addShip(sh26);
                shipRepository.save(sh26);
                Ship sh27 = new Ship("PatrolBoat", Arrays.asList("G6", "H6"));
                gp11.addShip(sh27);
                shipRepository.save(sh27);

                //SALVO

                Salvo sa1 = new Salvo(1, Arrays.asList("B5", "C5", "F1"));
                gp1.addSalvo(sa1);
                salvoRepository.save(sa1);

                Salvo sa2 = new Salvo(1, Arrays.asList("B4", "B5", "B6"));
                gp2.addSalvo(sa2);
                salvoRepository.save(sa2);

                Salvo sa3 = new Salvo(2, Arrays.asList("F2", "D5"));
                gp1.addSalvo(sa3);
                salvoRepository.save(sa3);

                Salvo sa4 = new Salvo(2, Arrays.asList("E1", "H3", "A2"));
                gp2.addSalvo(sa4);
                salvoRepository.save(sa4);

                Salvo sa5 = new Salvo(1, Arrays.asList("A2", "A4", "G6"));
                gp3.addSalvo(sa5);
                salvoRepository.save(sa5);

                Salvo sa6 = new Salvo(1, Arrays.asList("B5", "D5", "C7"));
                gp4.addSalvo(sa6);
                salvoRepository.save(sa6);

                Salvo sa7 = new Salvo(2, Arrays.asList("A3", "H6"));
                gp3.addSalvo(sa7);
                salvoRepository.save(sa7);

                Salvo sa8 = new Salvo(2, Arrays.asList("C5", "C6"));
                gp4.addSalvo(sa8);
                salvoRepository.save(sa8);

                Salvo sa9 = new Salvo(1, Arrays.asList("G6", "H6", "A4"));
                gp5.addSalvo(sa9);
                salvoRepository.save(sa9);

                Salvo sa10 = new Salvo(1, Arrays.asList("H1", "H2", "H3"));
                gp6.addSalvo(sa10);
                salvoRepository.save(sa10);

                Salvo sa11 = new Salvo(2, Arrays.asList("A2", "A3", "D8"));
                gp5.addSalvo(sa11);
                salvoRepository.save(sa11);

                Salvo sa12 = new Salvo(2, Arrays.asList("E1", "F2", "G3"));
                gp6.addSalvo(sa12);
                salvoRepository.save(sa12);

                Salvo sa13 = new Salvo(1, Arrays.asList("A3", "A4", "F7"));
                gp7.addSalvo(sa13);
                salvoRepository.save(sa13);

                Salvo sa14 = new Salvo(1, Arrays.asList("B5", "C6", "H1"));
                gp8.addSalvo(sa14);
                salvoRepository.save(sa14);

                Salvo sa15 = new Salvo(2, Arrays.asList("A2", "G6", "H6"));
                gp7.addSalvo(sa15);
                salvoRepository.save(sa15);

                Salvo sa16 = new Salvo(2, Arrays.asList("C5", "C7", "D5"));
                gp8.addSalvo(sa16);
                salvoRepository.save(sa16);

                Salvo sa17 = new Salvo(1, Arrays.asList("A1", "A2", "A3"));
                gp9.addSalvo(sa17);
                salvoRepository.save(sa17);

                Salvo sa18 = new Salvo(1, Arrays.asList("B5", "B6", "C7"));
                gp10.addSalvo(sa18);
                salvoRepository.save(sa18);

                Salvo sa19 = new Salvo(2, Arrays.asList("G6", "G7", "G8"));
                gp9.addSalvo(sa19);
                salvoRepository.save(sa19);

                Salvo sa20 = new Salvo(2, Arrays.asList("C6", "D6", "E6"));
                gp10.addSalvo(sa20);
                salvoRepository.save(sa20);

                Salvo sa21 = new Salvo(1, Arrays.asList("H1", "H8"));
                gp12.addSalvo(sa21);
                salvoRepository.save(sa21);

                //SCORES

                Score sc1 = new Score(1.0, g1, p1);
                scoreRepository.save(sc1);
                Score sc2 = new Score(0.0, g1, p2);
                scoreRepository.save(sc2);
                Score sc3 = new Score(0.5, g2, p1);
                scoreRepository.save(sc3);
                Score sc4 = new Score(0.5, g2, p2);
                scoreRepository.save(sc4);
                Score sc5 = new Score(1.0, g3, p2);
                scoreRepository.save(sc5);
                Score sc6 = new Score(0.0, g3, p4);
                scoreRepository.save(sc6);
                Score sc7 = new Score(0.5, g4, p2);
                scoreRepository.save(sc7);
                Score sc8 = new Score(0.5, g4, p1);
                scoreRepository.save(sc8);
            };
        }
    }
@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inputName-> {
            Player player = playerRepository.findByEmail(inputName);
            if (player != null) {
                return new User(player.getEmail(), player.getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputName);
            }
        });
    }
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/rest/**").hasAuthority("USER")
                .antMatchers("/api/games").permitAll()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/logout").hasAuthority("USER")
                .antMatchers("/api/game").hasAuthority("USER")
                .antMatchers("/api/players").permitAll()
                .antMatchers("/web/games.html").permitAll()
                .antMatchers("/web/game.html").hasAuthority("USER")
                .and()
                .formLogin();

        http.formLogin()
                .usernameParameter("name")
                .passwordParameter("pwd")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");
        http.csrf().disable();
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE));
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}

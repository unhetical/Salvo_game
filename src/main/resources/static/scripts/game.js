var app = new Vue({
    el: "#app",
    data: {
        games: [],
        ships: [],
        barcos: [],
        tiroCurrent: [],
        tiroOppo: [],
        salvos: [],
        salvosCurrent: [],
        salvosOppo: [],
        gamePlayer: [],
        currentPlayer: [],
        currentScore: [],
        opponentScore: [],
        opponentPlayer: [],
        positionShip: [],
        positionSalvo: [],
        turn:[],
        letters: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        numeros: [null, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    },

    methods: {

        fetchInit: function () {
            var parsedUrl = new URL(window.location.href);
            fetch("/api/game_view/" + parsedUrl.searchParams.get("gp"), )
                .then(function (data) {
                    return data.json();
                }).then(function (myData) {

                    app.games = myData;
                    console.log("games", app.games);

                    app.ships = myData.Ships;
                    console.log("ships Current player", app.ships);

                    app.salvos = myData.Salvos;
                    console.log("salvos", app.salvos);

                    app.ocupado();

                    app.tiros(parsedUrl);
                    console.log("salvosCurrent", app.salvosCurrent);

                    app.gamePlayer = myData.gamePlayers;
                    console.log("gamePlayer", app.gamePlayer);

                    app.local(parsedUrl);
                    console.log("currentPlayer", app.currentPlayer);
                    console.log("opponentPlayer", app.opponentPlayer);
                })
        },

        logout: function () {
            fetch("/api/logout", {
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                method: 'POST',
            }).then(function (data) {
                console.log("bye bye");
                console.log('Request success: ', data);
                window.location.href = "games.html";
                alert("correct logout!")
            }).catch(function (error) {
                console.log('Request failure: ', error);
            });
        },

        local: function (parsedUrl) {
            for (let x = 0; x < this.gamePlayer.length; x++) {
                if (this.gamePlayer[x].id == parsedUrl.searchParams.get("gp")) {
                    this.currentPlayer = this.gamePlayer[x].players;
                    this.currentScore = this.gamePlayer[x].scores;
                } else {
                    this.opponentPlayer = this.gamePlayer[x].players;
                    this.opponentScore = this.gamePlayer[x].scores;
                }
            }
            if (this.gamePlayer.length < 2) {
                this.opponentPlayer.players.email = "(wait opponent player)";
            }
        },

        ocupado: function () {

            for (let i = 0; i < this.games.Ships.length; i++) {
                for (let j = 0; j < this.games.Ships[i].location.length; j++) {
                    this.positionShip = this.games.Ships[i].location[j];
                    this.barcos = document.getElementById(this.positionShip);
                    this.barcos.classList.remove('cells');
                    this.barcos.classList.add('barco');
                }
            }
        },

        tiros: function (parsedUrl) {
            for (let key in this.salvos) {
                for (let key1 in this.salvos[key]) {
                    this.positionSalvo = this.salvos[key][key1];


                    for (var i = 0; i < this.positionSalvo.length; i++) {

                        if (key == parsedUrl.searchParams.get("gp")) {
                            this.tiroCurrent = document.getElementById(2 + this.positionSalvo[i]);
                            this.tiroCurrent.classList.remove('cells');
                            this.tiroCurrent.classList.add('shoot');
                            this.tiroCurrent.textContent = key1;

                        };
                        if (key != parsedUrl.searchParams.get("gp")) {
                            this.tiroOppo = document.getElementById(this.positionSalvo[i]);
                            if (this.tiroOppo.classList[0] == 'barco') {
                                this.tiroOppo.classList.remove('barco');
                                this.tiroOppo.classList.add('shoot');
                                this.tiroOppo.textContent = key1;
                            }

                        };

                    }
                }
            }
        }
    },

    created: function () {
        this.fetchInit();

    },
})
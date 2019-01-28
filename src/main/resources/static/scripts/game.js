var app = new Vue({
    el: "#app",
    data: {
        games: [],
        ships: [],
        barcos: [],
        tiroCurrent: [],
        tiroOppo: [],
        salvos: [],
        positions: [],
        salvosCurrent: [],
        salvosOppo: [],
        gamePlayer: [],
        currentPlayer: [],
        currentScore: [],
        opponentScore: [],
        opponentPlayer: [],
        positionShip: [],
        positionSalvo: [],
        turn: [],
        letters: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        numeros: [null, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        parsedUrl: null,
        idShip: null,
        shipLocation: [{
                shipName: "Battleship",
                locations: []
            },
            {
                shipName: "Submarine",
                locations: []
            },
            {
                shipName: "Destroyer",
                locations: []
            },
            {
                shipName: "PatrolBoat",
                locations: []
            },
            {
                shipName: "AircraftCarrier",
                locations: []
            }
        ]
    },

    methods: {

        fetchInit: function () {
            this.parsedUrl = new URL(window.location.href);
            fetch("/api/game_view/" + this.parsedUrl.searchParams.get("gp"), )
                .then(function (response) {
                    console.log('Request success: ', response);
                    if (response.ok) {
                        return response.json();
                    }
                    alert("Unauthorized, return back CHEATER");
                }).then(function (myData) {
                    app.games = myData;
                    //console.log("games", app.games);
                    app.ships = myData.Ships;
                    console.log("ships Current player", app.ships);
                    app.salvos = myData.Salvos;
                    console.log("salvos", app.salvos);
                    app.ocupado();
                    app.tiros();
                    console.log("salvosCurrent", app.salvosCurrent);
                    app.gamePlayer = myData.gamePlayers;
                    console.log("gamePlayer", app.gamePlayer);
                    app.local();
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

        placeShips: function () {
            var shipLocation = this.shipLocation;
            console.log(shipLocation);
            fetch("/api/games/players/" + this.parsedUrl.searchParams.get("gp") + "/ships", {
                    credentials: 'include',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    },
                    method: 'POST',
                    body: JSON.stringify(shipLocation)
                }).then(function (response) {
                    console.log(response);
                    return response.json();
                    // if (response.ok) {
                    //     return response.json();
                    // } else {
                    //     alert(response.status)
                    // }
                }).then(function (data) {
                    console.log("placeships", data.Error);
                })
                .catch(function (error) {
                    console.log('Request failure: ', error);
                });
        },

        local: function () {
            for (let x = 0; x < this.gamePlayer.length; x++) {
                if (this.gamePlayer[x].id == this.parsedUrl.searchParams.get("gp")) {
                    this.currentPlayer = this.gamePlayer[x].players;
                    this.currentScore = this.gamePlayer[x].scores;
                } else {
                    this.opponentPlayer = this.gamePlayer[x].players;
                    this.opponentScore = this.gamePlayer[x].scores;
                }
            }
            if (this.gamePlayer.length < 2) {
                this.opponentPlayer.email = "(wait opponent player)";
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

        tiros: function () {
            for (let key in this.salvos) {
                for (let key1 in this.salvos[key]) {
                    this.positionSalvo = this.salvos[key][key1];
                    for (var i = 0; i < this.positionSalvo.length; i++) {
                        if (key == this.parsedUrl.searchParams.get("gp")) {
                            this.tiroCurrent = document.getElementById(this.positionSalvo[i]);
                            console.log(this.tiroCurrent);
                            console.log(2 + this.positionSalvo[i])
                            this.tiroCurrent.classList.remove('cells');
                            this.tiroCurrent.classList.add('shoot');
                            this.tiroCurrent.textContent = key1;
                        };
                        if (key != this.parsedUrl.searchParams.get("gp")) {
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
        },

        dragstart_handler: function (ev) {
            this.idShip = ev.target.id;
            setTimeout(() => ev.target.className = "invisible", 0);
        },

        dragend_handler: function (ev) {
            ev.target.className = ev.target.id;
        },

        dragover_handler: function (ev) {
            ev.preventDefault();
        },

        dragenter_handler: function (ev) {
            this.positions = [];
            var celdasP = document.getElementsByClassName("cells");
            for (let i = 0; i < celdasP.length; i++) {
                celdasP[i].classList.remove("coloredCell");
            }
            var selectedCell = document.getElementById(ev.target.id);
            var shipSize = document.getElementById(this.idShip).getAttribute("data-size");
            var shipPos = document.getElementById(this.idShip).getAttribute("data-pos");
            var num = +selectedCell.id.slice(1);
            var letra = selectedCell.id.slice(0, 1);


            if (shipPos == "horizontal") {
                for (let i = 0; i < shipSize; i++) {
                    if (num + i < 11) {
                        var idCell = letra + (num + i);
                        var pintar = document.getElementById(idCell);
                        pintar.classList.add("coloredCell");
                        this.positions.push(idCell);
                    }
                }
            } else {
                for (let i = 0; i < shipSize; i++) {
                    for (let j = 0; j < this.letters.length; j++) {
                        if (this.letters[j] == letra) {
                            var idCell = this.letter[j] + num;
                            var pintar = document.getElementById(idCell);
                            pintar.classList.add("coloredCell");
                            this.positions.push(idCell);
                        }
                    }
                }
            }
            for (let x = 0; x < this.shipLocation.length; x++) {
                if (this.shipLocation[x].shipName == this.idShip) {
                    this.shipLocation[x].locations = [];
                    this.shipLocation[x].locations = this.positions;
                }
            }
            console.log("positions", this.positions);
            console.log("shipLocation", this.shipLocation);
            //console.log("idShip", this.idShip);
        },

        drop_handler: function (ev) {
            var celdasP = document.getElementsByClassName("cells");
            for (let i = 0; i < celdasP.length; i++) {
                celdasP[i].classList.remove("coloredCell");
            }
            ev.preventDefault();
            var ship = document.getElementById(this.idShip);
            ev.target.appendChild(ship);
        },

        rotate: function (id) {
            var size = id.getAttribute("data-size");
            var dataPos = id.getAttribute("data-pos");
            if (dataPos == "horizontal") {
                id.setAttribute("data-pos", "vertical");
                id.className = id.id + "-v";
                console.log(id);
            } else {
                id.setAttribute("data-pos", "horizontal");
                id.className = id.id;
                console.log(id);
            }
        }
    },
    created: function () {
        this.fetchInit();
    },
})
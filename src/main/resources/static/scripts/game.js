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
        shipSizes: null,
        shipPosi: null,
        shipElement: null,
        placedShip: null,
        finalPos: [],
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
                    if (response.ok) {
                        location.reload();
                        return response.json();
                    } else {
                        alert(response.status)
                    }
                }).then(function (data) {
                    console.log("placeships", data);
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
                    var barco = document.getElementById(this.positionShip);
                    barco.classList.remove("cells");
                    barco.classList.add("barco");
                    console.log(this.positionShip);
                    console.log(barco);

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
            this.shipElement = ev.target;
            setTimeout(() => this.shipElement.className = "invisible", 0);
        },

        dragend_handler: function (ev) {
            if (this.shipPosi == "horizontal") {
                ev.target.className = ev.target.id;
            } else {
                ev.target.className = ev.target.id + "-v";
            }
        },

        dragover_handler: function (ev) {
            ev.preventDefault();
            this.positions.forEach(pos => {
                if (!this.exist()) {
                    document.getElementById(pos).classList.remove("coloredCell");
                    document.getElementById(pos).classList.add("coloredRedCell");
                }
            });
        },

        dragenter_handler: function (ev) {
            this.positions = [];
            var celdasP = document.getElementsByClassName("cells");
            for (let i = 0; i < celdasP.length; i++) {
                celdasP[i].classList.remove("coloredCell", "coloredRedCell");
            }
            var shipSize = document.getElementById(this.shipElement.id).getAttribute("data-size");
            this.shipSizes = shipSize;
            var shipPos = document.getElementById(this.shipElement.id).getAttribute("data-pos");
            this.shipPosi = shipPos;
            if (ev.target) {
                var selectedCell = document.getElementById(ev.target.id);
            } else {
                var selectedCell = document.getElementById(ev);
            }
            var num = +selectedCell.id.slice(1);
            var letra = selectedCell.id.slice(0, 1);

            if (this.shipPosi == "horizontal") {
                for (let i = 0; i < this.shipSizes; i++) {
                    if (num + i < 11) {
                        var idCell = letra + (num + i);
                        var pintar = document.getElementById(idCell);
                        pintar.classList.add("coloredCell");
                        this.positions.push(idCell);
                    } else {
                        return false;
                    }
                }
            } else if (this.shipPosi == "vertical") {
                var start = this.letters.indexOf(letra)
                for (let x = 0; x < this.shipSizes; x++) {
                    idCell = this.letters[start + x] + num;
                    var pintar = document.getElementById(idCell);
                    pintar.classList.add("coloredCell");
                    this.positions.push(idCell);
                }
            } else {
                this.positions = [];
                return false;
            }
            console.log("data-pos", this.shipPosi);
            console.log("shipLocation", this.shipLocation);
            console.log("position", this.positions);
        },

        drop_handler: function (ev) {
            var celdasP = document.getElementsByClassName("cells");
            for (let i = 0; i < celdasP.length; i++) {
                celdasP[i].classList.remove("coloredCell", "coloredRedCell");
            }

            if (ev && this.exist()) {
                ev.preventDefault();
                ev.target.appendChild(this.shipElement);
                for (let x = 0; x < this.shipLocation.length; x++) {
                    if (this.shipLocation[x].shipName == this.shipElement.id) {
                        this.shipLocation[x].locations = [];
                        this.shipLocation[x].locations = this.positions;
                    }
                }
            }
        },

        rotate: function (shipElement) {
            this.shipElement = shipElement;
            if (this.shipPosi == "horizontal" && this.exist()) {
                this.shipElement.setAttribute("data-pos", "vertical");
                this.shipPosi = "vertical";
                this.shipElement.className = this.shipElement.id + "-v";
            } else if (this.shipPosi == "vertical" && this.exist()) {
                this.shipElement.setAttribute("data-pos", "horizontal");
                this.shipPosi = "horizontal";
                this.shipElement.className = this.shipElement.id;
            }
            this.dragenter_handler(this.shipElement.parentNode.id);
            this.drop_handler();
        },

        exist: function () {
            for (let i = 0; i < this.shipLocation.length; i++) {
                for (let x = 0; x < this.shipLocation[i].locations.length; x++) {
                    if (this.shipLocation[i].shipName != this.shipElement.id && this.positions.includes(this.shipLocation[i].locations[x]) ||
                        this.positions.length != this.shipSizes) {
                        console.error(false);
                        return false;
                    }
                }
            }
            console.error(true);
            return true;
        }
    },
    created: function () {
        this.fetchInit();
    },
})
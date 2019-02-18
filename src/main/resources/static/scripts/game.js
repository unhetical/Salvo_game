var app = new Vue({
    el: "#app",
    data: {
        games: [],
        ships: [],
        shipLives: [],
        hits: [],
        tiro: [],
        salvos: [],
        positions: [],
        gamePlayer: [],
        currentPlayer: [],
        currentScore: [],
        opponentScore: [],
        opponentPlayer: [],
        positionShip: [],
        positionShip2: [],
        positionSalvo: [],
        letters: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        numeros: [null, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        parsedUrl: null,
        shipSizes: null,
        shipPosi: null,
        shipElement: null,
        bombs: 5,
        bombPos: [],
        shipList: [{
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
        ],
        salvosList: {
            turn: null,
            locations: []
        }
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
                    console.log("games", app.games);
                    app.ships = myData.Ships;
                    // console.log("ships Current player", app.ships);
                    app.salvos = myData.Salvos;
                    console.log("salvos", app.salvos);
                    app.shipLives = myData.ShipSize;
                    // console.log("shipSizes", app.shipLives);
                    app.hits = myData.Hits;
                    console.log("hits", app.hits);
                    app.ocupado();
                    app.gamePlayer = myData.gamePlayers;
                    // console.log("gamePlayer", app.gamePlayer);
                    app.local();
                    // console.log("currentPlayer", app.currentPlayer);
                    // console.log("opponentPlayer", app.opponentPlayer);
                    app.tirosOppo();
                    app.tirosCurrent();
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
            var shipList = this.shipList;
            fetch("/api/games/players/" + this.parsedUrl.searchParams.get("gp") + "/ships", {
                    credentials: 'include',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    },
                    method: 'POST',
                    body: JSON.stringify(shipList)
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

        placeSalvo: function () {
            var salvosList = this.salvosList;
            if (salvosList.locations.length == 5) {
                fetch("/api/games/players/" + this.parsedUrl.searchParams.get("gp") + "/salvoes", {
                        credentials: 'include',
                        headers: {
                            'Accept': 'application/json',
                            'Content-Type': 'application/json'
                        },
                        method: 'POST',
                        body: JSON.stringify(salvosList)
                    }).then(function (response) {
                        console.log(response);
                        if (response.ok) {
                            location.reload();
                            return response.json();
                        } else {
                            alert(response.status)
                        }
                    }).then(function (data) {
                        console.log("placesalvoes", data);
                    })
                    .catch(function (error) {
                        console.log('Request failure: ', error);
                    });
            }
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
                    this.positionShip = this.games.Ships[i].location[0];
                    this.positionShip2 = this.games.Ships[i].location[j];
                    var barcoReal = document.getElementById(this.positionShip);
                    var barcoPos = document.getElementById(this.positionShip2);
                    var nameBarco = this.games.Ships[i].shipName;
                    barcoReal.classList.add("barcos");
                    barcoPos.classList.add("barcos");

                    var num1 = this.positionShip.slice(1);
                    var num2 = this.positionShip2.slice(1);
                    if (num1 == num2) {
                        barcoReal.style.position = "absolute";
                        barcoReal.classList.add(nameBarco + "-v");
                    } else {
                        barcoReal.style.position = "absolute";
                        barcoReal.classList.remove(nameBarco + "-v");
                        barcoReal.classList.add(nameBarco);
                    }
                }
            }
        },

        tirosOppo: function () {
            for (let key in this.salvos) {
                for (let key1 in this.salvos[key]) {
                    this.positionSalvo = this.salvos[key][key1];

                    for (var i = 0; i < this.positionSalvo.length; i++) {
                        if (key != this.parsedUrl.searchParams.get("gp")) {
                            this.tiro = document.getElementById(this.positionSalvo[i]);

                            if (this.tiro.className.includes("barcos")) {
                                this.tiro.classList.add('shoot');
                                this.tiro.textContent = key1;
                            } else {
                                this.tiro.classList.add('water');
                                this.tiro.textContent = key1;
                            }
                        }
                        if (key == this.parsedUrl.searchParams.get("gp")) {
                            this.tiro = document.getElementById(2 + this.positionSalvo[i]);
                            if (!this.tiro.className.includes("shoot")) {
                                this.tiro.classList.add('water');
                                this.tiro.textContent = key1;
                            } else {
                                this.tiro.textContent = key1;
                            }
                        }
                    }
                }
            }
        },

        tirosCurrent: function () {
            for (let key in this.hits) {
                var celdaOppo = document.getElementById(2 + key);
                celdaOppo.classList.remove("water");
                celdaOppo.classList.add("shoot");
                for (let key2 in this.shipLives) {
                    if (this.hits[key] == key2) {
                        this.shipLives[key2] -= 1;
                    } else if (this.shipLives[key2] == 0) {
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
            console.log(this.positions);
            this.positions.forEach(pos => {
                if (!this.exist() || !this.compareSize()) {
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
        },

        drop_handler: function (ev) {
            var celdasP = document.getElementsByClassName("cells");
            for (let i = 0; i < celdasP.length; i++) {
                celdasP[i].classList.remove("coloredCell", "coloredRedCell");
            }

            if (ev && this.compareSize() && this.exist()) {
                ev.preventDefault();
                ev.target.appendChild(this.shipElement);
                for (let x = 0; x < this.shipList.length; x++) {

                    if (this.shipList[x].shipName == this.shipElement.id) {
                        this.shipList[x].locations = [];
                        this.shipList[x].locations = this.positions;
                    }
                }
            }
        },

        rotate: function (shipElement) {
            this.shipElement = shipElement;

            if (this.shipPosi == "horizontal") {
                this.shipElement.setAttribute("data-pos", "vertical");
                this.shipPosi = "vertical";
                this.shipElement.className = this.shipElement.id + "-v";
                console.log(this.shipPosi);
                this.drop_handler();

            } else if (this.shipPosi == "vertical") {
                this.shipElement.setAttribute("data-pos", "horizontal");
                this.shipPosi = "horizontal";
                this.shipElement.className = this.shipElement.id;
                console.log(this.shipPosi);
                this.drop_handler();

            } else {
                return false
            }

        },

        exist: function () {
            for (let i = 0; i < this.shipList.length; i++) {
                for (let x = 0; x < this.shipList[i].locations.length; x++) {
                    if (this.shipList[i].shipName != this.shipElement.id && this.positions.includes(this.shipList[i].locations[x])) {
                        console.error(false);
                        return false;
                    }
                }
            }
            console.error(true);
            return true;
        },

        compareSize: function () {
            var parse = parseInt(this.shipSizes);
            console.log(parse, this.positions.length);
            if (this.positions.length != parse) {
                console.log("size", false);
                return false;
            }
            console.log("size", true);
            return true;
        },

        setBomb: function () {
            var pointer = document.getElementById("pointer");
            pointer.classList.add("url");
        },

        addSalvo: function (id) {
            var bombCell = document.getElementById(id);
            if (0 < this.bombs && !bombCell.className.includes("bomb")) {
                bombCell.classList.add("bomb");
                this.bombs -= 1;
                this.bombPos.push(bombCell.id.slice(1));
            } else {
                alert("all bombs are placed or already exist on this cell");
            }
            this.salvosList.locations = this.bombPos;
            console.log("salvoList push", this.salvosList);
        }

    },
    created: function () {
        this.fetchInit();
    },
})
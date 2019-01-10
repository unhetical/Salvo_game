var app = new Vue({
    el: "#app",
    data: {
        games: [],
        totalPlayers: [],
        current: null,
        gpID: null
    },

    methods: {
        fetchInit: function () {
            fetch("/api/games", {
                headers: {}
            }).then(function (data) {
                return data.json();
            }).then(function (myData) {
                app.games = myData.games;
                console.log("games", app.games);
                app.current = myData.player;
                console.log("current", app.current);
                app.totalPlayers = app.games[0].leaderboard;
                console.log("totalPlayers", app.totalPlayers);
                app.allPlayers();
                app.scores();
            })
        },

        login: function () {
            var username = document.getElementById("username").value;
            var password = document.getElementById("password").value;
            fetch("/api/login", {
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                method: 'POST',
                body: 'name=' + username + '&pwd=' + password,
            }).then(function (response) {
                if(response.ok){
                console.log('Request success: ', response);
                location.reload();
                alert("correct login!")}
            }).then(function (data) {
                console.log('Request success: ', data);
            }).catch(function (error) {
                console.log('Request failure: ', error);
            })
        },

        logout: function () {
            fetch("/api/logout", {
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                method: 'POST',
            }).then(function (response) {
                console.log('Request success: ', response);
                location.reload();
                alert("correct logout!")
            }).then(function (data) {
                console.log('Request success: ', data);
            }).catch(function (error) {
                console.log('Request failure: ', error);
            });
        },

        signUp: function () {
            var username = document.getElementById("username").value;
            var password = document.getElementById("password").value;
            fetch("/api/players", {
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    method: 'POST',
                    body: 'email=' + username + '&password=' + password,
                }).then(function (response) {
                    console.log('Request success: ', response);
                    if (response.status == 201) {
                        return response.json();
                    }
                    throw Error (response);
                }).then(function (data) {
                    alert("player created!")
                    console.log('Request success: ', data);
                    app.login();
                })
                .catch(function (error) {
                    console.log('Request failure: ', error);
                    alert("player in use")
                })
        },

        createGame: function () {
            fetch("/api/games", {
                credentials: 'include',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                method: 'POST',
            }).then(function (response) {
                return response.json();

            }).then(function (data) {
                console.log("data", data);
                app.gpID = data.gpID
                window.location = '/web/game.html?gp=' + app.gpID;
            }).catch(function (error) {

            })
        },



        buttonsOn: function (game) {
            if (this.current != null) {
                for (var i = 0; i < game.gamePlayers.length; i++) {
                    if (game.gamePlayers[i].players.id == this.current.id) {
                        return true;
                    }
                }
            }
        },

        enter: function (game) {
            if (this.current != null) {
                for (var i = 0; i < game.gamePlayers.length; i++) {
                    if (game.gamePlayers[i].players.id == this.current.id) {
                        return location.href = "/web/game.html?gp=" + game.gamePlayers[i].players.id;
                    }
                }
            }
        },


        allPlayers: function () {
            for (var i = 0; i < app.totalPlayers.length; i++) {
                var total = 0;
                for (var x = 0; x < app.totalPlayers[i].gamePlayers.length; x++) {
                    total += app.totalPlayers[i].gamePlayers[x].scores;
                }
                app.totalPlayers[i].totalScore = total;
            }
            app.totalPlayers.sort(function (a, b) {
                if (a.totalScore > b.totalScore) {
                    return -1;
                }
                if (a.totalScore < b.totalScore) {
                    return 1;
                } else if (a.totalScore == b.totalScore) {
                    if (a.won > b.won) {
                        return -1;
                    }
                    if (a.won > b.won) {
                        return 1;
                    } else if (a.won == b.won) {
                        if (a.tied > b.tied) {
                            return -1;
                        }
                        if (a.tied < b.tied) {
                            return 1;
                        } else if (a.tied == b.tied) {
                            if (a.lost > b.lost) {
                                return -1;
                            }
                            if (a.lost < b.lost) {
                                return 1;
                            }
                        }
                    }
                }
            })
        },

        scores: function () {
            for (var i = 0; i < app.totalPlayers.length; i++) {
                var won = 0;
                var lost = 0;
                var tied = 0;
                for (var x = 0; x < app.totalPlayers[i].gamePlayers.length; x++) {
                    if (app.totalPlayers[i].gamePlayers[x].scores == 1.0) {
                        won += app.totalPlayers[i].gamePlayers[x].scores;
                    }
                    if (app.totalPlayers[i].gamePlayers[x].scores == 0.5) {
                        tied += app.totalPlayers[i].gamePlayers[x].scores + 0.5;
                    }
                    if (app.totalPlayers[i].gamePlayers[x].scores == 0) {
                        lost += app.totalPlayers[i].gamePlayers[x].scores + 1;
                    }
                }
                app.totalPlayers[i].won = won;
                app.totalPlayers[i].lost = lost;
                app.totalPlayers[i].tied = tied
            }
        }
    },

    created: function () {
        this.fetchInit();
    }
})
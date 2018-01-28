var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/messenger-demo');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/messenger', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}

function showGreeting(message) {
    $("#messages").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    connect();
});

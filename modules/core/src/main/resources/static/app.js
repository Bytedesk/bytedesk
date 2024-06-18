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
    var socket = new SockJS('/stomp');
    stompClient = Stomp.over(socket);
    stompClient.reconnect_delay = 1000;
    // client will send heartbeats every 10000ms, default 10000
    stompClient.heartbeat.outgoing = 20000;
    // client does not want to receive heartbeats from the server, default 10000
    stompClient.heartbeat.incoming = 20000;
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        // stompClient.subscribe('/topic/greetings', function (greeting) {
        //     showGreeting(JSON.parse(greeting.body).content);
        // });
        stompClient.subscribe('/topic/test.thread.topic', function (message) {
            console.log(message)
            showGreeting(message.body)
        });
        // stompClient.subscribe('/app/test.thread.topic', function (message) {
        //     console.log(JSON.parse(message.body).body)
        // });
    }, function (error) {
        console.log('连接断开【' + error + '】');
        // app.isConnected = false;
        // 为断开重连做准备
        // app.subscribedTopics = [];
        // 5秒后重新连接，实际效果：每5秒重连一次，直到连接成功
        setTimeout(function () {
            console.log("reconnecting...");
            connect();
        }, 5000);
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    // stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
    // 使用/app开头，服务器端可以使用@MessageMapping拦截并处理
    stompClient.send("/app/test.thread.topic", {}, JSON.stringify({'name': $("#name").val()}));
    // 使用/topic开头，服务端无法通过@MessageMapping拦截消息并处理，服务器会直接广播，不建议使用
    // stompClient.send("/topic/test.thread.topic", {}, JSON.stringify({ 'name': $("#name").val() }));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});

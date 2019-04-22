var SockJs = require('sockjs-client');
var stompjs = require('stompjs');

const socket = new SockJs('http://user:password@localhost:8080/stomp');
const stompClient = stompjs.over(socket);

stompClient.connect({}, function (/* frame */) {
  stompClient.subscribe('/user/queue/event', frame => {
    console.log(frame)
  });

  stompClient.subscribe('/topic/client', frame => {
    console.log(frame)
  });


  stompClient.subscribe('/topic/message', frame => {
    console.log(frame)
  });

  stompClient.subscribe('/user/queue/error', frame => {
    console.log(frame)
  });

  setTimeout(() => {
    // stompClient.send('/app/event', {}, JSON.stringify({a: 1}));
    // stompClient.send('/app/ksug.springcamp.2018', {}, {});
    // stompClient.send('/app/ksug.springcamp.hello', {}, {});
    // stompClient.send('/app/echo', {}, "hello echo!!");
      stompClient.send('/app/user', {}, {});
  }, 1000);
});

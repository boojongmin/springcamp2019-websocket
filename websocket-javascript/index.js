const WebSocketClient = require('websocket').client;
const client = new WebSocketClient();

client.on('connectFailed', function(error) {
  console.log('Connect Error: ' + error.toString());
});

client.on('connect', (connection) => {
  connection.on('message', (message) => {
    if (message.type === 'utf8') {
      console.log("Received: '" + message.utf8Data + "'");
    }
  });

  connection.sendUTF('hello world!')

  connection.on('close', (message) => {
    console.log('session is closed.')
  })
});

client.connect('http://localhost:8080/websocket')



var WebSocketClient = require('websocket').client;

function ws() {
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

    setTimeout(() => {
      connection.sendUTF('hello world!')
    }, 1000)

    connection.on('close', () => {
      console.log('session is closed.')
    })
  });

  client.connect('http://localhost:8080/websocket')
}

for(let i=0; i< 1000; i++) {
  ws();
}

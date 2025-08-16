// wsClient.js
// import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

export function createStompClient() {
  const client = new Client({
    brokerURL: "ws://localhost:8080/ws", // Native WebSocket endpoint
    reconnectDelay: 2000,
    onStompError: (frame) => {
      console.error("Broker reported error: " + frame.headers["message"]);
      console.error("Additional details: " + frame.body);
    },
  });
  client.activate();
  return client;
}

/*
const client = new Client({
    webSocketFactory: () => new SockJS("/ws"),
    reconnectDelay: 2000,
    // If the JWT is in an HttpOnly cookie, you usually don't need extra headers.
    // If you ever move it to Authorization, you can set it here via beforeConnect.
  });
*/

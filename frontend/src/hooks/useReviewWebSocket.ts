import { Client, IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { useEffect } from "react";

export function useReviewWebSocket(onMessage: (id: string) => void) {
  useEffect(() => {
    const baseUrl = (import.meta as ImportMeta).env?.VITE_API_BASE_URL || "http://localhost:8080";
    const client = new Client({
      webSocketFactory: () => new SockJS(baseUrl + "/ws/reviews"),
      reconnectDelay: 5000
    });

    client.onConnect = () => {
      client.subscribe("/topic/reviews", (message: IMessage) => onMessage(message.body));
    };

    client.activate();
    return () => {
      void client.deactivate();
    };
  }, [onMessage]);
}

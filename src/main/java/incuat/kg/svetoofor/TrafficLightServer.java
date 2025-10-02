package incuat.kg.svetoofor;


import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TrafficLightServer extends WebSocketServer {

    private final Set<WebSocket> clients = Collections.synchronizedSet(new HashSet<>());

    public TrafficLightServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        clients.add(conn);
        System.out.println("Client connected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        clients.remove(conn);
        System.out.println("Client disconnected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // Пришло сообщение (например "RED_BLINK") → рассылаем всем
        synchronized (clients) {
            for (WebSocket client : clients) {
                client.send(message);
            }
        }
        System.out.println("Broadcast: " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server started on port " + getPort());
    }

    public static void main(String[] args) {
        TrafficLightServer server = new TrafficLightServer(8887); // порт 8887
        server.start();
    }
}
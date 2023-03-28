package net.finmath.smartcontract.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class FeedWebSocketHandler extends TextWebSocketHandler {

    private Timer timer = new Timer();
    private WebSocketSession session;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.session = session;
        timer.schedule(new FeedTask(), 0, 5000);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        timer.cancel();
    }

    private class FeedTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage("New message at " + System.currentTimeMillis()));
                } else {
                    timer.cancel();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
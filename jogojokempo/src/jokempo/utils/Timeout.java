package jokempo.utils;

import java.io.*;
import java.net.*;
import java.util.*;

public class Timeout {
	private Timer timer;
    private final Socket clienteSocket;
    private final long timeoutPeriod; // Tempo de timeout
    private final Runnable onTimeout; // Ação a ser realizada quando o timeout é atingido

    public Timeout(Socket clienteSocket, long timeoutPeriod, Runnable onTimeout) {
        this.clienteSocket = clienteSocket;
        this.timeoutPeriod = timeoutPeriod;
        this.onTimeout = onTimeout;
    }

    // Inicia o timer para o timeout
    public void start() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                if (!clienteSocket.isClosed()) {
                    onTimeout.run();
                }
            }
        }, timeoutPeriod);
    }

    // Reseta o timer (cancelando o timeout em andamento)
    public void reset() {
        if (timer != null) {
            timer.cancel();
        }
    }
}

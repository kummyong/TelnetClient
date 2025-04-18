package com.kkm.telnetclient;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.*;

public class TelnetSession {

    private TelnetClient telnetClient;
    private BufferedReader reader;
    private PrintWriter writer;

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String prompt;

    public TelnetSession(String host, int port, String username, String password, String prompt) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.prompt = prompt;
    }

    public void connect() throws IOException {
        telnetClient = new TelnetClient();
        telnetClient.connect(host, port);

        reader = new BufferedReader(new InputStreamReader(telnetClient.getInputStream()));
        writer = new PrintWriter(telnetClient.getOutputStream(), true);

        waitFor("login:");
        writer.println(username);

        waitFor("Password:");
        writer.println(password);

        waitFor(prompt);  // 로그인 완료 후 프롬프트까지 대기
    }

    public String execute(String command) throws IOException {
        writer.println(command);
        return waitFor(prompt);  // 프롬프트 전까지 명령 결과 반환
    }

    public void disconnect() throws IOException {
        if (telnetClient != null && telnetClient.isConnected()) {
            telnetClient.disconnect();
        }
    }

    private String waitFor(String endPattern) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1];
        long startTime = System.currentTimeMillis();
        long timeoutMillis = 10000;  // 10초 타임아웃

        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            if (reader.ready()) {
                int read = reader.read(buffer);
                if (read == -1) break;

                sb.append(buffer[0]);
                if (sb.toString().contains(endPattern)) {
                    break;
                }
            } else {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted while waiting for pattern", e);
                }
            }
        }

        if (!sb.toString().contains(endPattern)) {
            throw new IOException("Timeout while waiting for: " + endPattern);
        }

        return sb.toString();
    }
}


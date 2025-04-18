package com.kkm.telnetclient;

import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class TelnetSession {

    private static final Logger log = LoggerFactory.getLogger(TelnetSession.class);

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
        log.info("Connecting to {}:{}", host, port);
        telnetClient = new TelnetClient();
        telnetClient.connect(host, port);
        log.info("Connected to server.");

        reader = new BufferedReader(new InputStreamReader(telnetClient.getInputStream()));
        writer = new PrintWriter(telnetClient.getOutputStream(), true);

        waitUntil("login:");
        log.debug("Sending username: {}", username);
        writer.println(username);

        waitUntil("Password:");
        log.debug("Sending password.");
        writer.println(password);

        waitUntil(prompt);
        log.info("Login successful, prompt detected: {}", prompt);
    }

    public String execute(String command) throws IOException {
        log.info("Executing command: {}", command);
        writer.println(command);
        String result = waitUntil(prompt);
        log.debug("Command result: \n{}", result);
        return result;
    }

    public void disconnect() throws IOException {
        if (telnetClient != null && telnetClient.isConnected()) {
            log.info("Disconnecting from server.");
            telnetClient.disconnect();
        }
    }

    private String waitUntil(String endPattern) throws IOException {
        StringBuilder sb = new StringBuilder();
        long timeoutMillis = 10000;
        long startTime = System.currentTimeMillis();

        int ch;
        while ((System.currentTimeMillis() - startTime) < timeoutMillis && (ch = reader.read()) != -1) {
            sb.append((char) ch);
            if (sb.toString().contains(endPattern)) {
                break;
            }
        }

        if (!sb.toString().contains(endPattern)) {
            log.warn("Timeout waiting for pattern: {}", endPattern);
            throw new IOException("Timeout while waiting for: " + endPattern);
        }

        log.trace("Matched pattern [{}] in response:\n{}", endPattern, sb);
        return sb.toString();
    }
}

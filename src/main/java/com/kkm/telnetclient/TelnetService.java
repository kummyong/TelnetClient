package com.kkm.telnetclient;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelnetService {

    private static final Logger logger = LoggerFactory.getLogger(TelnetService.class);
    private final char prompt = '$';

    public void sampleExecute(String server, String user, String password, String command) {
        TelnetClient telnet = new TelnetClient();

        try {
            telnet.connect(server, 23);

            InputStream in = telnet.getInputStream();
            PrintStream out = new PrintStream(telnet.getOutputStream(), true, StandardCharsets.UTF_8);

            login(in, out, user, password);
            String result = sendCommand(in, out, command);
            logger.info("Command result:\n{}", result);
        } catch (Exception e) {
            logger.error("Telnet 접속 또는 명령 실행 중 오류", e);
        } finally {
            try {
                telnet.disconnect();
            } catch (Exception e) {
                logger.warn("Telnet 연결 종료 실패", e);
            }
        }
    }

    private void login(InputStream in, PrintStream out, String user, String password) {
        try {
            readUntil(in, "login:");
            write(out, user);
            readUntil(in, "Password:");
            write(out, password);
            readUntil(in, prompt + " ");
        } catch (Exception e) {
            logger.error("로그인 중 오류 발생", e);
        }
    }

    private String readUntil(InputStream in, String pattern) {
        try {
            char lastChar = pattern.charAt(pattern.length() - 1);
            StringBuilder sb = new StringBuilder();
            int ch;
            while ((ch = in.read()) != -1) {
                char c = (char) ch;
                sb.append(c);
                if (c == lastChar && sb.toString().endsWith(pattern)) {
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            logger.error("readUntil 오류", e);
        }
        return null;
    }

    private void write(PrintStream out, String value) {
        try {
            out.println(value);
            out.flush();
            logger.debug("[WRITE] {}", value);
        } catch (Exception e) {
            logger.error("write 오류", e);
        }
    }

    private String sendCommand(InputStream in, PrintStream out, String command) {
        try {
            write(out, command);
            return readUntil(in, prompt + " ");
        } catch (Exception e) {
            logger.error("명령 실행 중 오류", e);
        }
        return null;
    }
}
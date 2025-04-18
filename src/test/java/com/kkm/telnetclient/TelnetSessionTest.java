package com.kkm.telnetclient;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class TelnetSessionTest {

    @Test
    public void testTelnetSession() {
        TelnetSession session = new TelnetSession(
                "192.168.35.130",
                23,
                "edip",
                "edipdev11",
                "$"
        );

        try {
            session.connect();

            String result = session.execute("date '+%Y-%m-%d %H:%M:%S.%3N'");
            System.out.println("서버 시간:");
            System.out.println(result);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                session.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
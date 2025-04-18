package com.kkm.telnetclient;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class TelnetServiceTests {

    @Test
    void testExecuteCommand() {
        // given
        String server = "192.168.35.130";
        int port = 23;
        String username = "edip";
        String password = "edipdev11";
        String command = "date +\"%Y-%m-%d %H:%M:%S.%3N\"";
        TelnetService telnetService = new TelnetService();

        // when
        telnetService.sampleExecute(server, username, password, command);

        // then
        //assertTrue(result.contains("The current time is"));
    }
}

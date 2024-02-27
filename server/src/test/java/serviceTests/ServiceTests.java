package serviceTests;
import chess.ChessGame;
import org.junit.jupiter.api.*;
import passoffTests.obfuscatedTestClasses.TestServerFacade;
import passoffTests.testClasses.TestException;
import passoffTests.testClasses.TestModels;
import server.Server;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
public class ServiceTests {
    @Test
    public void whatIsGoingOn(){
        System.out.println(Server.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }
}

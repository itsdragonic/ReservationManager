import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.io.*;
import java.net.*;

/**
 * Test class for Server
 * Tests constructors, methods, and error handling
 *
 * @author Fredrick Zawislak
 * @version November 24, 2025
 */

public class TestServer {

    private Socket dummySocket;

    @Before
    public void setUp() throws Exception {
        dummySocket = new Socket() {
            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream("".getBytes());
            }

            @Override
            public OutputStream getOutputStream() {
                return new ByteArrayOutputStream();
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        dummySocket.close();
    }

    @Test
    public void testServerConstructor() {
        new Server(dummySocket); // constructor runs, safe
    }

    @Test
    public void testEndServerRuns() {
        Server server = new Server(dummySocket);
        server.endServer(); // instance method, safe
    }

    @Test
    public void testRunRuns() {
        Server server = new Server(dummySocket);
        server.run(); // safe, dummy socket with no input
    }
}
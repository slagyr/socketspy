import junit.framework.TestCase;
import java.net.*;
import java.io.*;

import socketserver.StreamReader;

// TODO Add me to a test suite

public class SocketSpyTest extends TestCase
{
	public static MockTrafficMonitor outboundMonitor;
	public static MockTrafficMonitor incomingMonitor;

	private SocketSpy spy;
	private InputStream destinationInput;
	private OutputStream destinationOutput;
	private InputStream originInput;
	private OutputStream originOutput;
	private Thread acceptingThread;

	public void setUp() throws Exception
	{
		spy = new SocketSpy(9991, 9992);
		spy.monitorSource = new MockTrafficMonitorSource();
		spy.spy();

		acceptConnection();
		makeConnection();
	}

	public void tearDown() throws Exception
	{
		spy.stop();
    if(acceptingThread.isAlive())
      acceptingThread.join();
	}

	public void testSimpleOutput() throws Exception
	{
		originOutput.write("12345".getBytes());
		StreamReader reader = new StreamReader(destinationInput);
		String readValue = reader.read(5);

		assertEquals("12345", readValue);
		assertEquals("12345", outboundMonitor.buffer.toString());
	}

	public void testSimpleInput() throws Exception
	{
		acceptingThread.join();
		destinationOutput.write("12345".getBytes());
		StreamReader reader = new StreamReader(originInput);
		String readValue = reader.read(5);

		assertEquals("12345", readValue);
		assertEquals("12345", incomingMonitor.buffer.toString());
	}

	public void testSendingLines() throws Exception
	{
		originOutput.write("line 1\n".getBytes());
		originOutput.write("line 2\n".getBytes());
		StreamReader reader = new StreamReader(destinationInput);
		String line1 = reader.readLine();
		assertEquals("line 1", line1);

		String line2 = reader.readLine();
		assertEquals("line 2", line2);
		assertEquals("line 1\nline 2\n", outboundMonitor.buffer.toString());
	}

	public void testDestinationHost() throws Exception
	{
		assertEquals(InetAddress.getLocalHost(), spy.destinationHost);
		spy = new SocketSpy(9991, 9992, InetAddress.getByName("google.com"));
		assertEquals(InetAddress.getByName("google.com"), spy.destinationHost);
	}

	private void makeConnection() throws IOException
	{
		Socket originSocket = new Socket(Inet4Address.getLocalHost(), 9991);
		originInput = originSocket.getInputStream();
		originOutput = originSocket.getOutputStream();
	}

	private void acceptConnection()
	{
		acceptingThread = new Thread(){
			public void run()
			{
				try
				{
					ServerSocket server = new ServerSocket(9992);
					Socket destinationSocket = server.accept();
					server.close();
					destinationInput = destinationSocket.getInputStream();
					destinationOutput = destinationSocket.getOutputStream();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		};
		acceptingThread.start();
	}

	private static class MockTrafficMonitor implements TrafficMonitor
	{
		public StringBuffer buffer = new StringBuffer();

		public void add(char c)
		{
			buffer.append(c);
		}
	}

	private class MockTrafficMonitorSource implements TrafficMonitorSource
	{

		public TrafficMonitor[] newMonitors()
		{
			outboundMonitor = new MockTrafficMonitor();
			incomingMonitor = new MockTrafficMonitor();
			return new TrafficMonitor[]{outboundMonitor, incomingMonitor};
		}
	}

}

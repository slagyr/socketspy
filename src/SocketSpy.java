
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.*;

import socketserver.SocketServer;
import socketserver.SocketService;

public class SocketSpy implements SocketServer
{
	private int originPort;
	private int destinationPort;
	public TrafficMonitorSource monitorSource = new SwingTrafficMonitorSource();
	private SocketService service;
	public InetAddress destinationHost;

	public SocketSpy(int listenPort, int talkPort) throws UnknownHostException
  {
		this(listenPort, talkPort, InetAddress.getByName("localhost"));
	}

	public SocketSpy(int listenPort, int talkPort, InetAddress destinationHost)
	{
		this.originPort = listenPort;
		this.destinationPort = talkPort;
		this.destinationHost = destinationHost;
	}

	public static void main(String[] args) throws Exception
	{
		int originPort = Integer.parseInt(args[0]);
		int destinationPort = Integer.parseInt(args[1]);
		SocketSpy spy = null;
		if(args.length == 3)
		{
			String destHost = args[2];
			spy = new SocketSpy(originPort, destinationPort, InetAddress.getByName(destHost));
		}
		else
			spy = new SocketSpy(originPort, destinationPort);
		spy.spy();
	}

	public void spy() throws Exception
	{
		service = new SocketService(originPort, this, InetAddress.getByName("localhost"));
	}

	public void stop() throws Exception
	{
		if(service != null)
			service.close();
	}

	public void serve(Socket originSocket)
	{
		try
		{
			final InputStream originInput = originSocket.getInputStream();
			final OutputStream originOutput = originSocket.getOutputStream();

			Socket destinationSocket = new Socket(destinationHost, destinationPort);
			final InputStream destinationInput = destinationSocket.getInputStream();
			final OutputStream destinationOutput = destinationSocket.getOutputStream();

			TrafficMonitor[] monitors = monitorSource.newMonitors();

			new Thread(new RevlealingRunnable(monitors[0], originInput, destinationOutput)).start();
			new Thread(new RevlealingRunnable(monitors[1], destinationInput, originOutput)).start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void revealTraffic(TrafficMonitor monitor, InputStream input, OutputStream output) throws Exception
	{
		int c;
		while( (c = input.read()) != -1)
		{
			output.write(c);
			monitor.add((char)c);
		}
	}

	private class RevlealingRunnable implements Runnable
	{
		private TrafficMonitor monitor;
		private InputStream input;
		private OutputStream output;

		public RevlealingRunnable(TrafficMonitor monitor, InputStream input, OutputStream output)
		{
			this.monitor = monitor;
			this.input = input;
			this.output = output;
		}

		public void run()
		{
			try
			{
				revealTraffic(monitor, input, output);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}


public class SwingTrafficMonitorSource implements TrafficMonitorSource
{
	public TrafficMonitor[] newMonitors()
	{
		SocketSpyFrame frame = new SocketSpyFrame();
		SwingTrafficMonitor outboundMonitor = new SwingTrafficMonitor(frame.outbound);
		SwingTrafficMonitor incomingMonitor = new SwingTrafficMonitor(frame.incoming);
		return new TrafficMonitor[] {outboundMonitor, incomingMonitor};
	}


}

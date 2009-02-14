
import javax.swing.*;
import java.awt.*;

public class SocketSpyFrame
{

	public SocketSpyFrame()
	{
		JFrame frame = new JFrame();
		frame.setSize(500, 500);
    mainPanel = new JPanel();
    mainPanel.setLayout(new GridLayout(1, 2));

    outbound = new JTextArea();
    outbound.setBackground(new Color(255, 255, 200));
    outbound.setEditable(false);
    outbound.setLineWrap(true);
    JScrollPane outboundScroll = new JScrollPane(outbound);
    outboundScroll.setBorder(BorderFactory.createTitledBorder("Outbound"));
    mainPanel.add(outboundScroll);

    incoming = new JTextArea();
    incoming.setBackground(new Color(200, 255, 200));
    incoming.setEditable(false);
    incoming.setLineWrap(true);
    JScrollPane incomingScroll = new JScrollPane(incoming);
    incomingScroll.setBorder(BorderFactory.createTitledBorder("Inbound"));
    mainPanel.add(incomingScroll);

    frame.getContentPane().add(mainPanel);
		frame.setVisible(true);

 	}

	private JPanel mainPanel;
	public JTextArea outbound;
	public JTextArea incoming;

  public static void main(String[] args) throws InterruptedException
  {
    new SocketSpyFrame();
  }
}

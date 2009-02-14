
import javax.swing.*;

public class SwingTrafficMonitor implements TrafficMonitor
{
	private JTextArea textArea;

  public SwingTrafficMonitor(JTextArea textArea)
	{
		this.textArea = textArea;
	}

	public void add(char c)
	{
    if(c >= ' ' && c <= '~')
		  textArea.append(c + "");
    else
      textArea.append("0x" + Integer.toHexString(c) + " ");
	}
}

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;


public class PerformanceTest extends JFrame implements ActionListener {
  public static void main(String[] args) {
    new PerformanceTest();
  }
  public PerformanceTest() {
    JButton run = new JButton("GO!");
    run.addActionListener(this);
    this.add(run);
    this.setVisible(true);
  }
  @Override
  public void actionPerformed(ActionEvent e) {
    while(true) {
      new Thread(new ClientPair()).start();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
    }
  }
}
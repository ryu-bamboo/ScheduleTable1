package schdule;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;

public class MainSchdule extends JFrame{

  public static void main(String[] args){
    MainSchdule frame = new MainSchdule();


    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setBounds(100, 100, 600, 600);
    frame.setTitle("タイトル");
    frame.setVisible(true);
  }

  MainSchdule(){
	JTable table=new JTable(30,1);
	JTextArea area =new JTextArea(10,10);

	JPanel p = new JPanel();
	p.setLayout(new BorderLayout());

	p.add(table,BorderLayout.CENTER);
	p.add(area, BorderLayout.);

	getContentPane().add(p,BorderLayout.CENTER);
  }
}
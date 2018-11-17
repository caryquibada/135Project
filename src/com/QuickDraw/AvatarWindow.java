package com.QuickDraw;
import javax.swing.JFrame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.AWTException;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.EtchedBorder;

public class AvatarWindow extends JFrame {
	private String name;
	private int currX,currY,X,Y;
	private JPanel panel;
	private BufferedImage image;
	/**
	 * Create the panel.
	 */
	public AvatarWindow(String name,String IPAddress, int port,String word) {
		this.name=name;
		getContentPane().setBackground(Color.WHITE);
		getContentPane().setLayout(null);
		setBounds(-8,-32,512,512);
		panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setBackground(Color.WHITE);
		panel.setBounds(0, 26, 496, 411);
		getContentPane().add(panel);
		setVisible(true);
		panel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e1) {
				drawLines(e1.getX(),e1.getY());
			}
		});
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e2) {
				currX=e2.getX();
				currY=e2.getY();
			}
		});
		JButton btnSave = new JButton("Save");
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					Rectangle screenRect = new Rectangle(0,45,496,411);
					BufferedImage capture = new Robot().createScreenCapture(screenRect);
					ImageIO.write(capture, "jpg", new File("resources/Images/"+name+".jpg"));
					startClient(name,IPAddress,port,word);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (AWTException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnSave.setBounds(422, 439, 64, 23);
		getContentPane().add(btnSave);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Graphics g = panel.getGraphics();
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, panel.getWidth(), panel.getHeight());
			}
		});
		btnClear.setBounds(352, 439, 64, 23);
		getContentPane().add(btnClear);
	}
	public void startClient(String name,String IPAddress,int port, String word) {
		dispose();
		new ClientGUI(name,IPAddress,port,word);
	}
	public void drawLines(int X,int Y){
		int oldXsend=currX;
		int oldYsend=currY;
		currX=X;
		currY=Y;
		Graphics g = panel.getGraphics();
		g.setColor(Color.BLACK);
		g.drawLine(oldXsend, oldYsend, X, Y);
	}
}

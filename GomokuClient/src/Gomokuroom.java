
// JavaObjClientView.java ObjecStram ��� Client
//�������� ä�� â
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Gomokuroom extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtInput;
	private String UserName;
	private JButton btnSend;
	private static final int BUF_LEN = 128; // Windows ó�� BUF_LEN �� ����

	private Socket socket; // �������
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	private JButton btnSurrender;
	private JButton btnBack;
	private JButton btnExit;
	
	private ImageIcon board = new ImageIcon("src/newboard.png");// �ٵ���
	Image img = board.getImage();
	private ImageIcon white = new ImageIcon("src/white1.png");// ��
	Image wht = white.getImage();
	private ImageIcon black = new ImageIcon("src/black1.png");// ������
	Image blk = black.getImage();
	private ImageIcon back = new ImageIcon("src/back5.png");// ������
	Image back3 = back.getImage();
	
	private int backX=20;//�ʱ갪 20 ���콺�� ���� �ִ� �迭�� 15�̹Ƿ� 20�� ���ü��� ���� ����
	private int backY=20;
	private boolean Backcount = false;
	
	private JLabel lblUserName;
	private JTextPane textArea;
	private int gamecolor; // 1��, 2��
	private int turncount = 0;//�ϼ���
	private int[][] arr = new int[15][15];

	JPanel gamePanel;
	private JLabel lblMouseEvent;
	private Graphics gc;
	private Image panelImage = null;
	private Image playerImage1 = null;
	private Image playerImage2 = null;
	private Graphics gc2 = null;
	private JLabel lblUserName1;
	private JLabel lblUserName2;
	
	private Graphics playergc1;
	private Graphics playergc2;
	

	public Gomokuroom(String username, String ip_addr, String port_no) {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 590);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(470, 190, 310, 300);
		contentPane.add(scrollPane);

		textArea = new JTextPane();
		textArea.setEditable(true);
		textArea.setFont(new Font("����ü", Font.PLAIN, 14));
		scrollPane.setViewportView(textArea);

		txtInput = new JTextField();
		txtInput.setBounds(550, 500, 145, 40);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton("������");
		btnSend.setFont(new Font("����", Font.PLAIN, 14));
		btnSend.setBounds(705, 500, 76, 40);
		contentPane.add(btnSend);

		lblUserName = new JLabel("Name");
		lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName.setBackground(Color.WHITE);
		lblUserName.setFont(new Font("����", Font.BOLD, 14));
		lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName.setBounds(470, 500, 67, 40);
		contentPane.add(lblUserName);
		setVisible(true);

		AppendText("User " + username + " connecting " + ip_addr + " " + port_no);
		UserName = username;
		lblUserName.setText(username);

		btnSurrender = new JButton("�׺�");// �׺���ư
		//btnSurrender.addActionListener(new MyActionListener());
		btnSurrender.setBounds(690, 15, 80, 40);
		contentPane.add(btnSurrender);

		btnBack = new JButton("������");// ������ ��û
		btnBack.setBounds(690, 75, 80, 40);
		contentPane.add(btnBack);
//		btnBack.addActionListener(new MyActionListener2());
		
		btnExit = new JButton("������");// ������
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "400", "Bye");
				SendObject(msg);
				System.exit(0);
			}
		});
		btnExit.setBounds(690, 135, 80, 40);
		contentPane.add(btnExit);
		
		JPanel playercolor1 = new JPanel();
		playercolor1.setBackground(Color.WHITE);
		playercolor1.setBounds(486, 60, 67, 67);
		contentPane.add(playercolor1);
		playergc1 = playercolor1.getGraphics();
		playergc1.drawImage(blk, 0, 0, 67, 67, null);
		
		
		
		JPanel playercolor2 = new JPanel();
		playercolor2.setBackground(Color.WHITE);
		playercolor2.setBounds(581, 60, 67, 67);
		contentPane.add(playercolor2);
		playergc2 = playercolor2.getGraphics();
		playergc2.drawImage(wht, 0, 0, 67, 67, null);

		// JPanel�� ���� ������
		gamePanel = new JPanel();
		gamePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		gamePanel.setBackground(Color.WHITE);
		gamePanel.setBounds(30, 70, 420, 420);
		contentPane.add(gamePanel);
		gc = gamePanel.getGraphics();

		panelImage = createImage(gamePanel.getWidth(), gamePanel.getHeight());
		gc2 = panelImage.getGraphics();
		gc2.setColor(gamePanel.getBackground());
		gc2.drawImage(img, 0, 0, 420, 420, null);

//		lblMouseEvent = new JLabel("<dynamic>");
//		lblMouseEvent.setHorizontalAlignment(SwingConstants.CENTER);
//		lblMouseEvent.setFont(new Font("����", Font.BOLD, 14));
//		lblMouseEvent.setBorder(new LineBorder(new Color(0, 0, 0)));
//		lblMouseEvent.setBackground(Color.WHITE);
//		lblMouseEvent.setBounds(12, 541, 400, 40);
//		contentPane.add(lblMouseEvent);
		
		lblUserName1 = new JLabel("player1");
		lblUserName1.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName1.setFont(new Font("����", Font.BOLD, 14));
		lblUserName1.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName1.setBackground(Color.WHITE);
		lblUserName1.setBounds(486, 15, 67, 40);
		contentPane.add(lblUserName1);
		
		lblUserName2 = new JLabel("player2");
		lblUserName2.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName2.setFont(new Font("����", Font.BOLD, 14));
		lblUserName2.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName2.setBackground(Color.WHITE);
		lblUserName2.setBounds(581, 15, 67, 40);
		contentPane.add(lblUserName2);


		for (int i = 0; i < 15; i++)// ������ �ʱ�ȭ
		{
			for (int j = 0; j < 15; j++) {
				arr[i][j] = 0;
			}
		}

		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			ChatMsg obcm = new ChatMsg(UserName, "100", "Hello");
			SendObject(obcm);

			ListenNetwork net = new ListenNetwork();
			net.start();
			TextSendAction action = new TextSendAction();
			btnSend.addActionListener(action);
			txtInput.addActionListener(action);
			txtInput.requestFocus();
			
			MyActionListener myaction = new MyActionListener();
			btnBack.addActionListener(myaction);
			btnSurrender.addActionListener(myaction);

			
			MyMouseEvent mouse = new MyMouseEvent();
			gamePanel.addMouseMotionListener(mouse);
			gamePanel.addMouseListener(mouse);

		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AppendText("connect error");
		}

	}

	class MyActionListener implements ActionListener{ //�׺� ��ư

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
			if(e.getSource() == btnSurrender)//�׺���ư�� ������ �� 
			{
				AppendText("�׺���ư�� ������");
				ChatMsg msg = new ChatMsg(UserName, "700", "Surrender");//���⼭ ����
				SendObject(msg);
				JOptionPane.showMessageDialog(gamePanel, "�׺��߽��ϴ�");
			}	
			else if(e.getSource() == btnBack)//������ ��û
			{
				if(Backcount && backX!=20 && backY !=20)
				{
					//AppendText("������ ��ư�� ������");
					ChatMsg msg = new ChatMsg(UserName, "600", "Back");
					SendObject(msg);
				}
				else
				{
					JOptionPane.showMessageDialog(gamePanel, "������� �ѹ��� �����մϴ�");
				}
			}		
		}
		
	}
	
	// Server Message�� �����ؼ� ȭ�鿡 ǥ��
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				try {

					Object obcm = null;
					String msg = null;
					ChatMsg cm;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						msg = String.format("[%s]\n%s", cm.UserName, cm.data);
					} else
						continue;
					switch (cm.code) {
					case "200": // chat message
						if (cm.UserName.equals(UserName))
							AppendTextR(msg); // �� �޼����� ������
						else
							AppendText(msg);
						break;
					case "300": // ���� �� ���ϱ�
						gamecolor = cm.gamecolor;

						if (cm.gamecolor == 1)// �浹�̸� ��
						{
							turncount = 1;
							String msgcolor = "����� ���Դϴ�";
							AppendTextR(msgcolor);
						} else if (cm.gamecolor == 2) {
							String msgcolor = "����� ���Դϴ�";
							AppendTextR(msgcolor);
						}
						else{
							String msgcolor = "����� �������Դϴ�";
							AppendTextR(msgcolor);
						}
						break;
					case "500": // Mouse Event ����
						DoMouseEvent(cm);
						break;
					case "600": // ������ ����
						DoBackEvent(cm);
						break;
					case "700": // �׺� ����
						DoSurrenderEvent(cm);
						break;
					case "800": // �й� ����
						DoWinEvent(cm);
						break;
					case "900": // ������ ���� ����
						YesBackEvent(cm);
						break;
					case "1000": //������ ���� ����
						NoBackEvent(cm);
						break;
					}
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
//						dos.close();
//						dis.close();
						ois.close();
						oos.close();
						socket.close();

						break;
					} catch (Exception ee) {
						break;
					} // catch�� ��
				} // �ٱ� catch����

			}
		}
	}

	public void paint(Graphics g) {
		super.paint(g);
		gc.drawImage(panelImage, 0, 0, this);
		playergc1.drawImage(blk, 0, 0,67,67, this);
		playergc2.drawImage(wht, 0, 0,67,67, this);
	}

	public void DoBackEvent(ChatMsg cm)// ������ ��ư
	{
		if (cm.UserName.matches(UserName)) // ������ ������ ó���� �����Ƿ� ����
			return;
		//������� cm ���� ChatMsg msg = new ChatMsg(UserName, "600", "Back"); ������
		int result = JOptionPane.showConfirmDialog(gamePanel,"��밡 ������ ��û�� �߽��ϴ� �³��Ͻðڽ��ϱ�?",
														"Confirm",JOptionPane.YES_NO_OPTION);
		if(result == JOptionPane.CLOSED_OPTION)//�׳� �ݾ����� == �ƴϿ� ó��
		{
			//������ �ƴϿ���
			AppendText("�����⸦ �����߽��ϴ�");
			ChatMsg msg = new ChatMsg(UserName, "1000", "NO");
			SendObject(msg);
		}
		else if(result == JOptionPane.YES_OPTION) //�����⸦ ��������� 
		{
			AppendText("�����⸦ �³��߽��ϴ�");
			ChatMsg msg = new ChatMsg(UserName, "900", "YES");
			SendObject(msg);
		}
		else // �ƴϿ� ���� ���
		{
			//������ �ƴϿ���
			AppendText("�����⸦ �����߽��ϴ�");
			ChatMsg msg = new ChatMsg(UserName, "1000", "NO");
			SendObject(msg);
		}
	}
	
	public void YesBackEvent(ChatMsg cm)// ������ ����� ���� �̺�Ʈ
	{
		if (!cm.UserName.matches(UserName)) // ����� �����ϹǷ�
		{
			JOptionPane.showMessageDialog(gamePanel, "�����⸦ �¶��߽��ϴ�");
		}
		//�ϴ� ������ ��ǥ�� �����ͼ� �׺κ� �迭 �����, �׺κ� �׸� ��� ��ġ��
		//��������ǥ�� ���½�Ű�� 1ȸ�� �ǰ��ض�
		int Backrow= backX;
		int Backcol= backY;
		
		arr[Backcol][Backrow] = 0;//�Ѵ� �迭 ����
		//AppendText("�׸��׸� �� ���� ���� "+(Backrow*30 -15)+" "+(Backcol*30 -15));
		gc2.drawImage(back3, Backrow*30 -15, Backcol*30 -15 , 30, 30, null);
		gc.drawImage(panelImage, 0, 0, gamePanel);
		
		if(turncount==1)//�ϼ��� �ٲٱ�
		{
			turncount=0;
		}
		else
		{
			turncount=1;
		}
		
		backX=20;//������ ��ǥ �ʱ�ȭ
		backY=20;
		Backcount=false;//������ ��ȸ ����
	}
	
	public void NoBackEvent(ChatMsg cm)// ������ ������ ���� �̺�Ʈ
	{
		if (!cm.UserName.matches(UserName)) // ����� �����ϹǷ�
		{
			JOptionPane.showMessageDialog(gamePanel, "�����⸦ �����߽��ϴ�");
		}
		
	}
	
	public void DoWinEvent(ChatMsg cm)
	{
		if (cm.UserName.matches(UserName)) // ���� ���� �̹� Local �� �׷ȴ�.
			return;
		JOptionPane.showMessageDialog(gamePanel, "�й��߽��ϴ�");
		cleanup();
	}

	public synchronized void DoSurrenderEvent(ChatMsg cm)// �׺�ó��
	{
		//AppendText("�������Ϸ�");
		if(gamecolor==1)//�ϸ��߱�
		{
			turncount=1;
			if (!cm.UserName.matches(UserName)) // ���� ���� �̹� Local �� �׷ȴ�.
				JOptionPane.showMessageDialog(gamePanel, "���� �׺��߽��ϴ�");
		}
		else if(gamecolor==2)
		{
			turncount=0;
			if (!cm.UserName.matches(UserName)) // ���� ���� �̹� Local �� �׷ȴ�.
				JOptionPane.showMessageDialog(gamePanel, "���� �׺��߽��ϴ�");
		}
		cleanup();
	}

	public void cleanup()
	{
		if(gamecolor==1)//�ϸ��߱�
		{
			turncount=1;
		}
		if(gamecolor==2)//�ϸ��߱�
		{
			turncount=0;
		}
		//AppendText("���� �ʱ�ȭ�Ϸ�");
		for (int i = 0; i < 15; i++)// ������ �ʱ�ȭ
		{
			for (int j = 0; j < 15; j++) {
				arr[i][j] = 0;
			}
		}
		//������ ���� ����
		gc2.drawImage(img, 0, 0, 420, 420, null);
		gc.drawImage(panelImage, 0, 0, 420, 420, gamePanel);
	}
	
	// Mouse Event ���� ó��
	public void DoMouseEvent(ChatMsg cm) {

		if (cm.UserName.matches(UserName)) // ���� ���� �̹� Local �� �׷ȴ�.
			return;
		int x1 = 0;
		int x2 = 0;
		int resultX = 0;
		int y1 = 0;
		int y2 = 0;
		int resultY = 0;

		x1 = cm.mouse_e.getX() / 30;
		x2 = cm.mouse_e.getX() % 30;
		y1 = cm.mouse_e.getY() / 30;
		y2 = cm.mouse_e.getY() % 30;

		if (x2 <= 15) {
			resultX = 30 * x1 - 15;
		} else if (x2 > 15) {
			resultX = 30 * x1 + 15;
		}
		if (y2 <= 15) {
			resultY = 30 * y1 - 15;
		} else if (y2 > 15) {
			resultY = 30 * y1 + 15;
		}
		int row = (resultX + 15) / 30;
		int col = (resultY + 15) / 30;

		backX=row;//������ ��ǥ �ֱ�
		backY=col;
		Backcount= true;
		//AppendTextR("��밡 �� ������ ��ǥ �ֱ�"+ col +" "+ row);
		if (cm.gamecolor== 1)
		{
			gc2.drawImage(blk, resultX, resultY, 30, 30, null);
			gc.drawImage(panelImage, 0, 0, gamePanel);
			arr[col][row] = 1;// �� ǥ��
		} else if (cm.gamecolor == 2)
		{
			gc2.drawImage(wht, resultX, resultY, 30, 30, null);
			gc.drawImage(panelImage, 0, 0, gamePanel);
			arr[col][row] =2;// �� ǥ��
		}
		
		if(cm.gamecolor==1 || cm.gamecolor==2)
		{
			turncount = 1;// ��밡 �ΰ����Ŀ� ����
			AppendTextR("��밡 �ξ����ϴ�");
		}

	}

	public void SendMouseEvent(MouseEvent e) {
		ChatMsg cm = new ChatMsg(UserName, "500", "MOUSE");
		cm.gamecolor= gamecolor;
		cm.mouse_e = e;
		SendObject(cm);
	}

	
	// Mouse Event Handler
	class MyMouseEvent implements MouseListener, MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent e) {
			//lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// ��ǥ��°���
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			//lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// ��ǥ��°���
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// Ŭ�������� �ٵϵ��� ���ߵ�
			//lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// ��ǥ��°���
			int x1 = 0;
			int x2 = 0;
			int resultX = 0;
			int resultY = 0;
			int y1 = 0;
			int y2 = 0;
			int row = 0;
			int col = 0;

			x1 = e.getX() / 30;
			x2 = e.getX() % 30;
			y1 = e.getY() / 30;
			y2 = e.getY() % 30;

			if (x2 <= 15) {
				resultX = 30 * x1 - 15;
			} else if (x2 > 15) {
				resultX = 30 * x1 + 15;
			}
			if (y2 <= 15) {
				resultY = 30 * y1 - 15;
			} else if (y2 > 15) {
				resultY = 30 * y1 + 15;
			}

			row = (resultX + 15) / 30;
			col = (resultY + 15) / 30;

			backX=row;//������ ��ǥ �ֱ�
			backY=col;
			Backcount= true;
			//AppendTextR("���� �� ������ ��ǥ �ֱ�"+ col +" "+ row);
//			String cmsg = "�˻��մϴ� ������ǥ"+ col+row;
//			AppendTextR(cmsg);
			//AppendTextR(arr[col][row]+"  0�̸� �ڸ��� �������");
			
			//if (turncount == 1 && arr[col][row] == 0 && Searchrule(col, row, gamecolor) == 0)// �� ������ �������ִ�
			if(gamecolor ==1 && Searchrule(col,row,gamecolor) == 1)//���̸鼭 ����ΰ�??
			{
				AppendTextR("����Դϴ�");
			}
			else if (turncount == 1 && arr[col][row] == 0 )// �� ������ �������ִ�
			{

				if (gamecolor == 2)// ��
				{
					gc2.drawImage(wht, resultX, resultY, 30, 30, null);
					gc.drawImage(panelImage, 0, 0, gamePanel);
					arr[col][row] = 2;// �� ǥ��

				} else if (gamecolor == 1)// ������
				{
					gc2.drawImage(blk, resultX, resultY, 30, 30, null);
					gc.drawImage(panelImage, 0, 0, gamePanel);
					arr[col][row] = 1;// �� ǥ��
				}
				turncount = 0;
				SendMouseEvent(e);
				Winrule(col, row, gamecolor);
			}

		}
		@Override
		public void mouseEntered(MouseEvent e) {
			//lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// ��ǥ��°���

		}

		@Override
		public void mouseExited(MouseEvent e) {
			//lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// ��ǥ��°���

		}

		@Override
		public void mousePressed(MouseEvent e) {
			//lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// ��ǥ��°���
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			//lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// ��ǥ��°���
		}
	}

	// keyboard enter key ġ�� ������ ����
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button�� �����ų� �޽��� �Է��ϰ� Enter key ġ��
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = null;
				// msg = String.format("[%s] %s\n", UserName, txtInput.getText());
				msg = txtInput.getText();
				SendMessage(msg);
				txtInput.setText(""); // �޼����� ������ ���� �޼��� ����â�� ����.
				txtInput.requestFocus(); // �޼����� ������ Ŀ���� �ٽ� �ؽ�Ʈ �ʵ�� ��ġ��Ų��
				if (msg.contains("/exit")) // ���� ó��
					System.exit(0);
			}
		}
	}

	// ȭ�鿡 ���
	public synchronized void AppendText(String msg) {
		msg = msg.trim(); // �յ� blank�� \n�� �����Ѵ�.
		int len = textArea.getDocument().getLength();

		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(left, Color.BLACK);
		doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), msg + "\n", left);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// ȭ�� ������ ���
	public synchronized void AppendTextR(String msg) {
		msg = msg.trim(); // �յ� blank�� \n�� �����Ѵ�.
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet right = new SimpleAttributeSet();
		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
		StyleConstants.setForeground(right, Color.BLUE);
		doc.setParagraphAttributes(doc.getLength(), 1, right, false);
		try {
			doc.insertString(doc.getLength(), msg + "\n", right);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void AppendImage(ImageIcon ori_icon) {
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len); // place caret at the end (with no selection)
		Image ori_img = ori_icon.getImage();
		Image new_img;
		ImageIcon new_icon;
		int width, height;
		double ratio;
		width = ori_icon.getIconWidth();
		height = ori_icon.getIconHeight();
		// Image�� �ʹ� ũ�� �ִ� ���� �Ǵ� ���� 200 �������� ��ҽ�Ų��.
		if (width > 200 || height > 200) {
			if (width > height) { // ���� ����
				ratio = (double) height / width;
				width = 200;
				height = (int) (width * ratio);
			} else { // ���� ����
				ratio = (double) width / height;
				height = 200;
				width = (int) (height * ratio);
			}
			new_img = ori_img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			new_icon = new ImageIcon(new_img);
			textArea.insertIcon(new_icon);
		} else {
			textArea.insertIcon(ori_icon);
			new_img = ori_img;
		}
		len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		textArea.replaceSelection("\n");
		gc2.drawImage(ori_img, 0, 0, gamePanel.getWidth(), gamePanel.getHeight(), gamePanel);
		gc.drawImage(panelImage, 0, 0, gamePanel.getWidth(), gamePanel.getHeight(), gamePanel);
	}

	// Windows ó�� message ������ ������ �κ��� NULL �� ����� ���� �Լ�
	public byte[] MakePacket(String msg) {
		byte[] packet = new byte[BUF_LEN];
		byte[] bb = null;
		int i;
		for (i = 0; i < BUF_LEN; i++)
			packet[i] = 0;
		try {
			bb = msg.getBytes("euc-kr");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		for (i = 0; i < bb.length; i++)
			packet[i] = bb[i];
		return packet;
	}

	// Server���� network���� ����
	public void SendMessage(String msg) {
		try {
			// dos.writeUTF(msg);
//			byte[] bb;
//			bb = MakePacket(msg);
//			dos.write(bb, 0, bb.length);
			ChatMsg obcm = new ChatMsg(UserName, "200", msg);
			oos.writeObject(obcm);
		} catch (IOException e) {
			// AppendText("dos.write() error");
			AppendText("oos.writeObject() error");
			try {
//				dos.close();
//				dis.close();
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}

	public void SendObject(Object ob) { // ������ �޼����� ������ �޼ҵ�
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			// textArea.append("�޼��� �۽� ����!!\n");
			AppendText("SendObject Error");
		}
	}

	public synchronized void Winrule(int col, int row, int gamecolor)// row�� ����, col�� ���� �¸�����
	{
		int count = 0;
		int R = row;
		int C = col;
		

		// 1�� �¿�˻� ����
		while (gamecolor == arr[C][R])// ������
		{
			count++;
			R++;
			if (R > 14)
				break;
		}
		R = row;
		C = col;
		count--;//���� �ڸ��� ���⶧����
		while (gamecolor == arr[C][R])// ����
		{
			
			count++;
			R--;
			if (R < 0)
				break;
		}
		// AppendTextR(count+" �¿�count ��");
		// �¿�˻� ��
		if (count == 5) {
			// �¸�
			ChatMsg msg = new ChatMsg(UserName, "800", "Win");//���⼭ ����
			SendObject(msg);
			JOptionPane.showMessageDialog(gamePanel, "�¸��߽��ϴ�");
			cleanup();
		}

		// 2�� ���ϰ˻� ����
		count = 0;
		R = row;
		C = col;
		while (gamecolor == arr[C][R])// ����
		{
			count++;
			C--;
			if (C < 0)
				break;
		}
		R = row;
		C = col;
		count--;//�����ڸ��� ���⶧����
		while (gamecolor == arr[C][R])// �Ʒ���
		{
			count++;
			C++;
			if (C > 14)
				break;
		}
		// AppendTextR(count+" ����count ��");
		// ���ϰ˻� ��
		if (count == 5) {
			// �¸�
			ChatMsg msg = new ChatMsg(UserName, "800", "Win");//���⼭ ����
			SendObject(msg);
			JOptionPane.showMessageDialog(gamePanel, "�¸��߽��ϴ�");
			cleanup();
		}

		// 3�� ���� ������ ������ �Ʒ��� �밢��
		count = 0;
		R = row;
		C = col;
		while (gamecolor == arr[C][R])// ���� ���� ��������
		{
			count++;
			C--;
			R--;
			if (C < 0 || R < 0)
				break;
		}
		R = row;
		C = col;
		count--;//�����ڸ��� ���⶧����
		while (gamecolor == arr[C][R])// ������ �Ʒ��� ��������
		{
			count++;
			C++;
			R++;
			if (R > 14 || C > 14)
				break;
		}
		// AppendTextR(count+" 3�� �밢�� count ��");
		// ���� ������ ������ �Ʒ��� �밢���˻� ��
		if (count == 5) {
			// �¸�
			ChatMsg msg = new ChatMsg(UserName, "800", "Win");//���⼭ ����
			SendObject(msg);
			JOptionPane.showMessageDialog(gamePanel, "�¸��߽��ϴ�");
			cleanup();
		}

		// 4�� ������ ������ ���� �Ʒ��� �밢��
		count = 0;
		R = row;
		C = col;
		while (gamecolor == arr[C][R])// ������ ���� ��������
		{
			count++;
			C--;
			R++;
			if (C < 0 || R > 14)
				break;
		}
		R = row;
		C = col;
		count--;//�����ڸ��� ���⶧����
		while (gamecolor == arr[C][R])// ���� �Ʒ��� ��������
		{
			count++;
			C++;
			R--;
			if (R < 0 || C > 14)
				break;
		}
		// AppendTextR(count+" 4�� �밢�� count ��");
		// ������ ������ ���� �Ʒ��� �밢���˻� ��
		if (count == 5) {
			// �¸�
			ChatMsg msg = new ChatMsg(UserName, "800", "Win");//���⼭ ����
			SendObject(msg);
			JOptionPane.showMessageDialog(gamePanel, "�¸��߽��ϴ�");
			cleanup();
			
		}

	}

	public synchronized int Searchrule(int col, int row, int gamecolor)// 33���� �ƴ��� �˻�
	{
		int ban = 0;// 33�����ƴ��� �Ǻ� 1�̸� 33��
		int R = row;
		int C = col;
		int emp1[] = { 1, 1, 0, 0 };// �Ϲ��� 33 ���� ����1
		int emp2[] = { 1, 1, 0, 1 };// �Ϲ��� 33 ���� ����2
		int emp3[] = { 1, 1, 0, 2 };// �Ϲ��� 33 ���� ����2
		int emp4[] = { 0, 1, 1, 0 };// ��ĭ ������ִ� ����
		int emp5[] = { 1, 0, 1, 0 };// ��ĭ�� ������ִ� ����
		int temp[] = new int[4];// �� 4ĭ ����	

		boolean right = false;
		boolean left = false;
		boolean up = false;
		boolean down = false;
		boolean crossud = false;
		boolean crossrl = false;
		if(gamecolor ==2)
		{
			return ban;
		}
		
		R = row;
		C = col;
		// 1�� �����ʰ˻� ����
		for(int i=0;i<4;i++)//temp �ʱ�ȭ Ȥ�ø� 
		{
			temp[i]=0;
		}
		for (int i = 0; i < 4; i++)// ������ 4��
		{
			if (R < 14)
				R++;
			else
				break;
			
			if (arr[C][R] == 1)// ������
			{
				temp[i] = 1;
			} else if (arr[C][R] == 2)// ��
			{
				temp[i] = 2;
			} else if (arr[C][R] == 0)// �������
			{
				temp[i] = 0;
			}	
		}
		
		if (Arrays.equals(emp1, temp) || Arrays.equals(emp2, temp) || Arrays.equals(emp3, temp)
				|| Arrays.equals(emp4, temp) || Arrays.equals(emp5, temp)) {
			// ���޴µ� 1,1,0,0 �̴�
			// ���޴µ� 1,1,0,1 �̴�
			// ���޴µ� 1,1,0,2 �̴�
			// ���޴µ� 0,1,1,0 �̴�
			// ���޴µ� 0,1,0,1 �̴�
			right = true;
			//AppendTextR("������ true  "+R);
		}
		
		// ���� �˻�
		for(int i=0;i<4;i++)//temp �ʱ�ȭ Ȥ�ø� 
		{
			temp[i]=0;
		}
		R = row;
		C = col;
		for (int i = 0; i < 4; i++)// ���� 4��
		{
			if (C > 0)
				C--;
			else
				break;
			
			if (arr[C][R] == 1)// ������
			{
				temp[i] = 1;
			} else if (arr[C][R] == 2)// ��
			{
				temp[i] = 2;
			} else if (arr[C][R] == 0)// �������
			{
				temp[i] = 0;
			}	
		}
		if (Arrays.equals(emp1, temp) || Arrays.equals(emp2, temp) || Arrays.equals(emp3, temp)
				|| Arrays.equals(emp4, temp) || Arrays.equals(emp5, temp)) {
			// ���޴µ� 1,1,0,0 �̴�
			// ���޴µ� 1,1,0,1 �̴�
			// ���޴µ� 1,1,0,2 �̴�
			// ���޴µ� 0,1,1,0 �̴�
			// ���޴µ� 0,1,0,1 �̴�
			up = true;
			//AppendTextR("���� true");
		}
		
		// �Ʒ��� �˻�
		for(int i=0;i<4;i++)//temp �ʱ�ȭ Ȥ�ø� 
		{
			temp[i]=0;
		}
		R = row;
		C = col;
		for (int i = 0; i < 4; i++)// �Ʒ��� 4��
		{
			if (C < 14)
				C++;
			else
				break;
			
			if (arr[C][R] == 1)// ������
			{
				temp[i] = 1;
			} else if (arr[C][R] == 2)// ��
			{
				temp[i] = 2;
			} else if (arr[C][R] == 0)// �������
			{
				temp[i] = 0;
			}	

		}
		if (Arrays.equals(emp1, temp) || Arrays.equals(emp2, temp) || Arrays.equals(emp3, temp)
				|| Arrays.equals(emp4, temp) || Arrays.equals(emp5, temp)) {
			// 1,1,0,0 �̴�
			//  1,1,0,1 �̴�
			//  1,1,0,2 �̴�
			// 0,1,1,0 �̴�
			//  0,1,0,1 �̴�
			down = true;
			//AppendTextR("�Ʒ� true");
		}
		// ���� �˻�
		R = row;
		C = col;
		for(int i=0;i<4;i++)//temp �ʱ�ȭ Ȥ�ø� 
		{
			temp[i]=0;
		}
		for (int i = 0; i < 4; i++)// �������� 4��
		{
			if (R > 0)
				R--;
			else
				break;
			
			if (arr[C][R] == 1)// ������
			{
				temp[i] = 1;
			} else if (arr[C][R] == 2)// ��
			{
				temp[i] = 2;
			} else if (arr[C][R] == 0)// �������
			{
				temp[i] = 0;
			}
		}
		if (Arrays.equals(emp1, temp) || Arrays.equals(emp2, temp) || Arrays.equals(emp3, temp)
				|| Arrays.equals(emp4, temp) || Arrays.equals(emp5, temp)) {
			// ���޴µ� 1,1,0,0 �̴�
			// ���޴µ� 1,1,0,1 �̴�
			// ���޴µ� 1,1,0,2 �̴�
			// ���޴µ� 0,1,1,0 �̴�
			// ���޴µ� 0,1,0,1 �̴�
			left = true;
			//AppendTextR("���� true");
		}
		
		//����
		R = row;
		C = col;
		int emp6[]= {1,0,0};
		int emp7[]= {1,0,1};
		int emp8[]= {1,0,2};
		int emp9[]= {0,1,0};
		
		int line1[]= {0,0,0};
		int line2[]= {0,0,0};
		
		for (int i = 0; i < 3; i++)// ���� ����
		{
			if(C<0 || C>14)
			{
				break;
			}
			else if(C-i-1<0 || C+i+1>14)
			{
				break;
			}
			
			if (arr[C-i-1][R] == 1)// ������
			{
				line1[i] = 1;
			} else if (arr[C-i-1][R] == 2)// ��
			{
				line1[i] = 2;
			} else if (arr[C-i-1][R] == 0)// �������
			{
				line1[i] = 0;
			}
			
			if (arr[C+i+1][R] == 1)// ������
			{
				line2[i] = 1;
			} else if (arr[C+i+1][R] == 2)// ��
			{
				line2[i] = 2;
			} else if (arr[C+i+1][R] == 0)// �������
			{
				line2[i] = 0;
			}
		}
		if ((Arrays.equals(emp6, line1) || Arrays.equals(emp7, line1) || Arrays.equals(emp8, line1)	|| Arrays.equals(emp9, line1) )
				&& ( Arrays.equals(emp6, line2) || Arrays.equals(emp7, line2) || Arrays.equals(emp8, line2)	|| Arrays.equals(emp9, line2) ))
		{
			crossud = true;
			//AppendTextR("ũ�ν����Ʒ� true");
		}
		
		for(int i=0;i<3;i++)// line1,2 �ʱ�ȭ Ȥ�ø� 
		{
			line1[i]=0;
			line2[i]=0;
		}
		for (int i = 0; i < 3; i++)//�¿� ũ�ν�
		{
			if(R<0 || R>14)
			{
				break;
			}
			else if(R-i-1<0 || R+i+1>14)
			{
				break;
			}
			
			if (arr[C][R-i-1] == 1)// ������
			{
				line1[i] = 1;
			} else if (arr[C][R-i-1] == 2)// ��
			{
				line1[i] = 2;
			} else if (arr[C][R-i-1] == 0)// �������
			{
				line1[i] = 0;
			}
			
			if (arr[C][R+i+1] == 1)// ������
			{
				line2[i] = 1;
			} else if (arr[C][R+i+1] == 2)// ��
			{
				line2[i] = 2;
			} else if (arr[C][R+i+1] == 0)// �������
			{
				line2[i] = 0;
			}
		}
		if ((Arrays.equals(emp6, line1) || Arrays.equals(emp7, line1) || Arrays.equals(emp8, line1)	|| Arrays.equals(emp9, line1) )
				&& ( Arrays.equals(emp6, line2) || Arrays.equals(emp7, line2) || Arrays.equals(emp8, line2)	|| Arrays.equals(emp9, line2) ))
		{
			crossrl = true;
			//AppendTextR("ũ�ν������ʿ��� true");
		}
		// �밢�� �� ����
		boolean rightup = false;
		boolean rightdown = false;
		boolean leftup = false;
		boolean leftdown = false;
		boolean crossruld = false;
		boolean crosslurd = false;

		R = row;
		C = col;
		// 1�� ���������밢�� �˻� ����
		for(int i=0;i<4;i++)//temp �ʱ�ȭ Ȥ�ø� 
		{
			temp[i]=0;
		}
		for (int i = 0; i < 4; i++)// ���������밢 4��
		{
			if (R < 14 && C >0)
			{
				R++;
				C--;
			}
			else
				break;
			
			if (arr[C][R] == 1)// ������
			{
				temp[i] = 1;
			} else if (arr[C][R] == 2)// ��
			{
				temp[i] = 2;
			} else if (arr[C][R] == 0)// �������
			{
				temp[i] = 0;
			}
		}
		if (Arrays.equals(emp1, temp) || Arrays.equals(emp2, temp) || Arrays.equals(emp3, temp)
				|| Arrays.equals(emp4, temp) || Arrays.equals(emp5, temp)) {
			rightup = true;
			//AppendTextR("�������� true");
		}
		
		R = row;
		C = col;
		// 2�� �������밢�� �˻� ����
		for(int i=0;i<4;i++)//temp �ʱ�ȭ Ȥ�ø� 
		{
			temp[i]=0;
		}
		for (int i = 0; i < 4; i++)// ���������밢 4��
		{
			if (R >0 && C >0)
			{
				R--;
				C--;
			}
			else
				break;
			
			if (arr[C][R] == 1)// ������
			{
				temp[i] = 1;
			} else if (arr[C][R] == 2)// ��
			{
				temp[i] = 2;
			} else if (arr[C][R] == 0)// �������
			{
				temp[i] = 0;
			}
		}
		if (Arrays.equals(emp1, temp) || Arrays.equals(emp2, temp) || Arrays.equals(emp3, temp)
				|| Arrays.equals(emp4, temp) || Arrays.equals(emp5, temp)) {
			leftup = true;
			//AppendTextR("������ true");
		}
		
		R = row;
		C = col;
		// 3�� ���ʾƷ��밢�� �˻� ����
		for(int i=0;i<4;i++)//temp �ʱ�ȭ Ȥ�ø� 
		{
			temp[i]=0;
		}
		for (int i = 0; i < 4; i++)// ���������밢 4��
		{
			if (R >0 && C<14)
			{
				R--;
				C++;
			}
			else
				break;
			
			if (arr[C][R] == 1)// ������
			{
				temp[i] = 1;
			} else if (arr[C][R] == 2)// ��
			{
				temp[i] = 2;
			} else if (arr[C][R] == 0)// �������
			{
				temp[i] = 0;
			}

		}
		if (Arrays.equals(emp1, temp) || Arrays.equals(emp2, temp) || Arrays.equals(emp3, temp)
					|| Arrays.equals(emp4, temp) || Arrays.equals(emp5, temp)) {
				leftdown = true;
				//AppendTextR("���ʾƷ� true");
		}
		R = row;
		C = col;
		// 4�� �����ʾƷ��밢�� �˻� ����
		for(int i=0;i<4;i++)//temp �ʱ�ȭ Ȥ�ø� 
		{
			temp[i]=0;
		}
		for (int i = 0; i < 4; i++)// ���������밢 4��
		{
			if (R<14 && C<14)
			{
				R++;
				C++;
			}
			else
				break;
			
			if (arr[C][R] == 1)// ������
			{
				temp[i] = 1;
			} else if (arr[C][R] == 2)// ��
			{
				temp[i] = 2;
			} else if (arr[C][R] == 0)// �������
			{
				temp[i] = 0;
			}
		}
		if (Arrays.equals(emp1, temp) || Arrays.equals(emp2, temp) || Arrays.equals(emp3, temp)
					|| Arrays.equals(emp4, temp) || Arrays.equals(emp5, temp)) {
				rightdown = true;
				//AppendTextR("�����ʾƷ� true");
		}

		
		R = row;
		C = col;
		for(int i=0;i<3;i++)// line1,2 �ʱ�ȭ Ȥ�ø� 
		{
			line1[i]=0;
			line2[i]=0;
		}
		for (int i = 0; i < 3; i++)// ������������ ���ʾƷ� ����
		{
			if((C<0 || C>14) || (R<0 || R>14))
			{
				break;
			}
			else if((C-i-1<0 || C+i+1>14) || (R-i-1<0 || R+i+1>14))
			{
				break;
			}
			
			if (arr[C-i-1][R+i+1] == 1)// ������
			{
				line1[i] = 1;
			} else if (arr[C-i-1][R+i+1] == 2)// ��
			{
				line1[i] = 2;
			} else if (arr[C-i-1][R+i+1] == 0)// �������
			{
				line1[i] = 0;
			}
			
			if (arr[C+i+1][R-i-1] == 1)// ������
			{
				line2[i] = 1;
			} else if (arr[C+i+1][R-i-1] == 2)// ��
			{
				line2[i] = 2;
			} else if (arr[C+i+1][R-i-1] == 0)// �������
			{
				line2[i] = 0;
			}
		}
		if ((Arrays.equals(emp6, line1) || Arrays.equals(emp7, line1) || Arrays.equals(emp8, line1)	|| Arrays.equals(emp9, line1) )
					&& ( Arrays.equals(emp6, line2) || Arrays.equals(emp7, line2) || Arrays.equals(emp8, line2)	|| Arrays.equals(emp9, line2) ))
			{
				crossruld = true;
				//AppendTextR("������������ ���ʾƷ��밢�� true");
			}
		for(int i=0;i<3;i++)// line1,2 �ʱ�ȭ Ȥ�ø� 
		{
			line1[i]=0;
			line2[i]=0;
		}
		for (int i = 0; i < 3; i++)// ���������� �����ʾƷ��� ũ�ν�
		{
			if((R<0 || R>14) || (C<0 || C>14))
			{
				break;
			}
			else if((R-i-1<0 || R+i+1>14) || (C-i-1<0 || C+i+1>14))
			{
				break;
			}
			
			if (arr[C-i-1][R-i-1] == 1)// ������
			{
				line1[i] = 1;
			} else if (arr[C-i-1][R-i-1] == 2)// ��
			{
				line1[i] = 2;
			} else if (arr[C-i-1][R-i-1] == 0)// �������
			{
				line1[i] = 0;
			}
			
			if (arr[C+i+1][R+i+1] == 1)// ������
			{
				line2[i] = 1;
			} else if (arr[C+i+1][R+i+1] == 2)// ��
			{
				line2[i] = 2;
			} else if (arr[C+i+1][R+i+1] == 0)// �������
			{
				line2[i] = 0;
			}
		}
		if ((Arrays.equals(emp6, line1) || Arrays.equals(emp7, line1) || Arrays.equals(emp8, line1)	|| Arrays.equals(emp9, line1) )
					&& ( Arrays.equals(emp6, line2) || Arrays.equals(emp7, line2) || Arrays.equals(emp8, line2)	|| Arrays.equals(emp9, line2) ))
			{
				crosslurd = true;
				//AppendTextR("���������� �����ʾƷ��밢�� true");
			}
		// 33�� �����ϴ� �����ϰ��
		if ((left && up) || (right && up) || (left && down) || (right && down)
				|| (crossud && left) || (crossud && right) || (crossrl && up)
				|| (crossrl && down) || (crossud && crossrl) || (crosslurd && crossruld) 
				|| (crosslurd && leftdown) || (crosslurd && rightup) || (crossruld && leftup) || (crossruld && rightdown)
				|| (rightup && rightdown) || (rightdown && leftdown) || (leftdown && leftup) || (leftup && rightup) 
				|| (left && leftup) || (left && leftdown) || (left && rightdown) || (left && rightup)
				|| (left && crosslurd) || (left && crossruld) || (right && rightdown) || (right && rightup)
				|| (right && crosslurd) || (left && crossruld) || (right && leftdown) || (right && leftup)
				|| (up && leftup) || (up && leftdown) || (up && rightdown) || (up && rightup)
				|| (up && crosslurd) || (up && crossruld) || (down && rightdown) || (down && rightup)
				|| (down && crosslurd) || (down && crossruld) || (down && leftdown) || (down && leftup)
				|| (crossrl && leftup) || (crossrl && leftdown) || (crossrl && rightdown) || (crossrl && rightup)
				|| (crossrl && crosslurd) || (crossrl && crossruld) || (crossud && rightdown) || (crossud && rightup)
				|| (crossud && crosslurd) || (crossud && crossruld) || (crossud && leftdown) || (crossud && leftup)
				)
		{
			ban = 1;
		}

		return ban;
	}
	
	
}
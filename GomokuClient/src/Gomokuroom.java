
// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창
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
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의

	private Socket socket; // 연결소켓
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	private JButton btnSurrender;
	private JButton btnBack;
	private JButton btnExit;
	
	private ImageIcon board = new ImageIcon("src/newboard.png");// 바둑판
	Image img = board.getImage();
	private ImageIcon white = new ImageIcon("src/white1.png");// 흰돌
	Image wht = white.getImage();
	private ImageIcon black = new ImageIcon("src/black1.png");// 검은돌
	Image blk = black.getImage();
	private ImageIcon back = new ImageIcon("src/back5.png");// 검은돌
	Image back3 = back.getImage();
	
	private int backX=20;//초깃값 20 마우스로 받을 최대 배열은 15이므로 20은 나올수가 없는 숫자
	private int backY=20;
	private boolean Backcount = false;
	
	private JLabel lblUserName;
	private JTextPane textArea;
	private int gamecolor; // 1흑, 2백
	private int turncount = 0;//턴순서
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
		textArea.setFont(new Font("굴림체", Font.PLAIN, 14));
		scrollPane.setViewportView(textArea);

		txtInput = new JTextField();
		txtInput.setBounds(550, 500, 145, 40);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton("보내기");
		btnSend.setFont(new Font("굴림", Font.PLAIN, 14));
		btnSend.setBounds(705, 500, 76, 40);
		contentPane.add(btnSend);

		lblUserName = new JLabel("Name");
		lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName.setBackground(Color.WHITE);
		lblUserName.setFont(new Font("굴림", Font.BOLD, 14));
		lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName.setBounds(470, 500, 67, 40);
		contentPane.add(lblUserName);
		setVisible(true);

		AppendText("User " + username + " connecting " + ip_addr + " " + port_no);
		UserName = username;
		lblUserName.setText(username);

		btnSurrender = new JButton("항복");// 항복버튼
		//btnSurrender.addActionListener(new MyActionListener());
		btnSurrender.setBounds(690, 15, 80, 40);
		contentPane.add(btnSurrender);

		btnBack = new JButton("무르기");// 무르기 요청
		btnBack.setBounds(690, 75, 80, 40);
		contentPane.add(btnBack);
//		btnBack.addActionListener(new MyActionListener2());
		
		btnExit = new JButton("나가기");// 나가기
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

		// JPanel로 만든 보드판
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
//		lblMouseEvent.setFont(new Font("굴림", Font.BOLD, 14));
//		lblMouseEvent.setBorder(new LineBorder(new Color(0, 0, 0)));
//		lblMouseEvent.setBackground(Color.WHITE);
//		lblMouseEvent.setBounds(12, 541, 400, 40);
//		contentPane.add(lblMouseEvent);
		
		lblUserName1 = new JLabel("player1");
		lblUserName1.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName1.setFont(new Font("굴림", Font.BOLD, 14));
		lblUserName1.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName1.setBackground(Color.WHITE);
		lblUserName1.setBounds(486, 15, 67, 40);
		contentPane.add(lblUserName1);
		
		lblUserName2 = new JLabel("player2");
		lblUserName2.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName2.setFont(new Font("굴림", Font.BOLD, 14));
		lblUserName2.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName2.setBackground(Color.WHITE);
		lblUserName2.setBounds(581, 15, 67, 40);
		contentPane.add(lblUserName2);


		for (int i = 0; i < 15; i++)// 보드판 초기화
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

	class MyActionListener implements ActionListener{ //항복 버튼

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
			if(e.getSource() == btnSurrender)//항복버튼이 눌렸을 때 
			{
				AppendText("항복버튼이 눌려짐");
				ChatMsg msg = new ChatMsg(UserName, "700", "Surrender");//여기서 막힘
				SendObject(msg);
				JOptionPane.showMessageDialog(gamePanel, "항복했습니다");
			}	
			else if(e.getSource() == btnBack)//무르기 요청
			{
				if(Backcount && backX!=20 && backY !=20)
				{
					//AppendText("무르기 버튼이 눌려짐");
					ChatMsg msg = new ChatMsg(UserName, "600", "Back");
					SendObject(msg);
				}
				else
				{
					JOptionPane.showMessageDialog(gamePanel, "무르기는 한번만 가능합니다");
				}
			}		
		}
		
	}
	
	// Server Message를 수신해서 화면에 표시
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
							AppendTextR(msg); // 내 메세지는 우측에
						else
							AppendText(msg);
						break;
					case "300": // 게임 색 정하기
						gamecolor = cm.gamecolor;

						if (cm.gamecolor == 1)// 흑돌이면 선
						{
							turncount = 1;
							String msgcolor = "당신은 흑입니다";
							AppendTextR(msgcolor);
						} else if (cm.gamecolor == 2) {
							String msgcolor = "당신은 백입니다";
							AppendTextR(msgcolor);
						}
						else{
							String msgcolor = "당신은 관전중입니다";
							AppendTextR(msgcolor);
						}
						break;
					case "500": // Mouse Event 수신
						DoMouseEvent(cm);
						break;
					case "600": // 무르기 수신
						DoBackEvent(cm);
						break;
					case "700": // 항복 수신
						DoSurrenderEvent(cm);
						break;
					case "800": // 패배 수신
						DoWinEvent(cm);
						break;
					case "900": // 무르기 승인 수신
						YesBackEvent(cm);
						break;
					case "1000": //무르기 거절 수신
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
					} // catch문 끝
				} // 바깥 catch문끝

			}
		}
	}

	public void paint(Graphics g) {
		super.paint(g);
		gc.drawImage(panelImage, 0, 0, this);
		playergc1.drawImage(blk, 0, 0,67,67, this);
		playergc2.drawImage(wht, 0, 0,67,67, this);
	}

	public void DoBackEvent(ChatMsg cm)// 무르기 버튼
	{
		if (cm.UserName.matches(UserName)) // 본인은 무르기 처리를 했으므로 리턴
			return;
		//여기까지 cm 만들어서 ChatMsg msg = new ChatMsg(UserName, "600", "Back"); 보내옴
		int result = JOptionPane.showConfirmDialog(gamePanel,"상대가 무르기 신청을 했습니다 승낙하시겠습니까?",
														"Confirm",JOptionPane.YES_NO_OPTION);
		if(result == JOptionPane.CLOSED_OPTION)//그냥 닫았을때 == 아니요 처리
		{
			//무르기 아니요함
			AppendText("무르기를 거절했습니다");
			ChatMsg msg = new ChatMsg(UserName, "1000", "NO");
			SendObject(msg);
		}
		else if(result == JOptionPane.YES_OPTION) //무르기를 허락했을때 
		{
			AppendText("무르기를 승낙했습니다");
			ChatMsg msg = new ChatMsg(UserName, "900", "YES");
			SendObject(msg);
		}
		else // 아니요 했을 경우
		{
			//무르기 아니요함
			AppendText("무르기를 거절했습니다");
			ChatMsg msg = new ChatMsg(UserName, "1000", "NO");
			SendObject(msg);
		}
	}
	
	public void YesBackEvent(ChatMsg cm)// 무르기 허락에 대한 이벤트
	{
		if (!cm.UserName.matches(UserName)) // 대답을 들어야하므로
		{
			JOptionPane.showMessageDialog(gamePanel, "무르기를 승락했습니다");
		}
		//일단 무르기 좌표를 가져와서 그부분 배열 지우고, 그부분 그림 비게 고치고
		//무르기좌표를 리셋시키고 1회만 되게해라
		int Backrow= backX;
		int Backcol= backY;
		
		arr[Backcol][Backrow] = 0;//둘다 배열 비우기
		//AppendText("그림그릴 곳 가로 세로 "+(Backrow*30 -15)+" "+(Backcol*30 -15));
		gc2.drawImage(back3, Backrow*30 -15, Backcol*30 -15 , 30, 30, null);
		gc.drawImage(panelImage, 0, 0, gamePanel);
		
		if(turncount==1)//턴순서 바꾸기
		{
			turncount=0;
		}
		else
		{
			turncount=1;
		}
		
		backX=20;//무르기 좌표 초기화
		backY=20;
		Backcount=false;//무르기 기회 소진
	}
	
	public void NoBackEvent(ChatMsg cm)// 무르기 거절에 대한 이벤트
	{
		if (!cm.UserName.matches(UserName)) // 대답을 들어야하므로
		{
			JOptionPane.showMessageDialog(gamePanel, "무르기를 거절했습니다");
		}
		
	}
	
	public void DoWinEvent(ChatMsg cm)
	{
		if (cm.UserName.matches(UserName)) // 본인 것은 이미 Local 로 그렸다.
			return;
		JOptionPane.showMessageDialog(gamePanel, "패배했습니다");
		cleanup();
	}

	public synchronized void DoSurrenderEvent(ChatMsg cm)// 항복처리
	{
		//AppendText("턴조정완료");
		if(gamecolor==1)//턴맞추기
		{
			turncount=1;
			if (!cm.UserName.matches(UserName)) // 본인 것은 이미 Local 로 그렸다.
				JOptionPane.showMessageDialog(gamePanel, "백이 항복했습니다");
		}
		else if(gamecolor==2)
		{
			turncount=0;
			if (!cm.UserName.matches(UserName)) // 본인 것은 이미 Local 로 그렸다.
				JOptionPane.showMessageDialog(gamePanel, "흑이 항복했습니다");
		}
		cleanup();
	}

	public void cleanup()
	{
		if(gamecolor==1)//턴맞추기
		{
			turncount=1;
		}
		if(gamecolor==2)//턴맞추기
		{
			turncount=0;
		}
		//AppendText("보드 초기화완료");
		for (int i = 0; i < 15; i++)// 보드판 초기화
		{
			for (int j = 0; j < 15; j++) {
				arr[i][j] = 0;
			}
		}
		//보드판 정리 리셋
		gc2.drawImage(img, 0, 0, 420, 420, null);
		gc.drawImage(panelImage, 0, 0, 420, 420, gamePanel);
	}
	
	// Mouse Event 수신 처리
	public void DoMouseEvent(ChatMsg cm) {

		if (cm.UserName.matches(UserName)) // 본인 것은 이미 Local 로 그렸다.
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

		backX=row;//무르기 좌표 넣기
		backY=col;
		Backcount= true;
		//AppendTextR("상대가 둔 무르기 좌표 넣기"+ col +" "+ row);
		if (cm.gamecolor== 1)
		{
			gc2.drawImage(blk, resultX, resultY, 30, 30, null);
			gc.drawImage(panelImage, 0, 0, gamePanel);
			arr[col][row] = 1;// 돌 표시
		} else if (cm.gamecolor == 2)
		{
			gc2.drawImage(wht, resultX, resultY, 30, 30, null);
			gc.drawImage(panelImage, 0, 0, gamePanel);
			arr[col][row] =2;// 돌 표시
		}
		
		if(cm.gamecolor==1 || cm.gamecolor==2)
		{
			turncount = 1;// 상대가 두고난후에 내턴
			AppendTextR("상대가 두었습니다");
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
			//lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// 좌표출력가능
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			//lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// 좌표출력가능
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// 클릭했을때 바둑돌이 들어가야됨
			//lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// 좌표출력가능
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

			backX=row;//무르기 좌표 넣기
			backY=col;
			Backcount= true;
			//AppendTextR("내가 둔 무르기 좌표 넣기"+ col +" "+ row);
//			String cmsg = "검사합니다 찍은좌표"+ col+row;
//			AppendTextR(cmsg);
			//AppendTextR(arr[col][row]+"  0이면 자리가 비어있음");
			
			//if (turncount == 1 && arr[col][row] == 0 && Searchrule(col, row, gamecolor) == 0)// 턴 순서를 가지고있다
			if(gamecolor ==1 && Searchrule(col,row,gamecolor) == 1)//흑이면서 삼삼인가??
			{
				AppendTextR("삼삼입니다");
			}
			else if (turncount == 1 && arr[col][row] == 0 )// 턴 순서를 가지고있다
			{

				if (gamecolor == 2)// 흰돌
				{
					gc2.drawImage(wht, resultX, resultY, 30, 30, null);
					gc.drawImage(panelImage, 0, 0, gamePanel);
					arr[col][row] = 2;// 돌 표시

				} else if (gamecolor == 1)// 검은돌
				{
					gc2.drawImage(blk, resultX, resultY, 30, 30, null);
					gc.drawImage(panelImage, 0, 0, gamePanel);
					arr[col][row] = 1;// 돌 표시
				}
				turncount = 0;
				SendMouseEvent(e);
				Winrule(col, row, gamecolor);
			}

		}
		@Override
		public void mouseEntered(MouseEvent e) {
			//lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// 좌표출력가능

		}

		@Override
		public void mouseExited(MouseEvent e) {
			//lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// 좌표출력가능

		}

		@Override
		public void mousePressed(MouseEvent e) {
			//lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// 좌표출력가능
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			//lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// 좌표출력가능
		}
	}

	// keyboard enter key 치면 서버로 전송
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button을 누르거나 메시지 입력하고 Enter key 치면
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = null;
				// msg = String.format("[%s] %s\n", UserName, txtInput.getText());
				msg = txtInput.getText();
				SendMessage(msg);
				txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				if (msg.contains("/exit")) // 종료 처리
					System.exit(0);
			}
		}
	}

	// 화면에 출력
	public synchronized void AppendText(String msg) {
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
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

	// 화면 우측에 출력
	public synchronized void AppendTextR(String msg) {
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
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
		// Image가 너무 크면 최대 가로 또는 세로 200 기준으로 축소시킨다.
		if (width > 200 || height > 200) {
			if (width > height) { // 가로 사진
				ratio = (double) height / width;
				width = 200;
				height = (int) (width * ratio);
			} else { // 세로 사진
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

	// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
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

	// Server에게 network으로 전송
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

	public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			// textArea.append("메세지 송신 에러!!\n");
			AppendText("SendObject Error");
		}
	}

	public synchronized void Winrule(int col, int row, int gamecolor)// row가 가로, col이 세로 승리조건
	{
		int count = 0;
		int R = row;
		int C = col;
		

		// 1번 좌우검사 시작
		while (gamecolor == arr[C][R])// 오른쪽
		{
			count++;
			R++;
			if (R > 14)
				break;
		}
		R = row;
		C = col;
		count--;//같은 자리를 세기때문에
		while (gamecolor == arr[C][R])// 왼쪽
		{
			
			count++;
			R--;
			if (R < 0)
				break;
		}
		// AppendTextR(count+" 좌우count 값");
		// 좌우검사 끝
		if (count == 5) {
			// 승리
			ChatMsg msg = new ChatMsg(UserName, "800", "Win");//여기서 막힘
			SendObject(msg);
			JOptionPane.showMessageDialog(gamePanel, "승리했습니다");
			cleanup();
		}

		// 2번 상하검사 시작
		count = 0;
		R = row;
		C = col;
		while (gamecolor == arr[C][R])// 위쪽
		{
			count++;
			C--;
			if (C < 0)
				break;
		}
		R = row;
		C = col;
		count--;//같은자리르 세기때문에
		while (gamecolor == arr[C][R])// 아래쪽
		{
			count++;
			C++;
			if (C > 14)
				break;
		}
		// AppendTextR(count+" 상하count 값");
		// 상하검사 끝
		if (count == 5) {
			// 승리
			ChatMsg msg = new ChatMsg(UserName, "800", "Win");//여기서 막힘
			SendObject(msg);
			JOptionPane.showMessageDialog(gamePanel, "승리했습니다");
			cleanup();
		}

		// 3번 왼쪽 위에서 오른쪽 아래로 대각선
		count = 0;
		R = row;
		C = col;
		while (gamecolor == arr[C][R])// 왼쪽 위로 지그제그
		{
			count++;
			C--;
			R--;
			if (C < 0 || R < 0)
				break;
		}
		R = row;
		C = col;
		count--;//같은자리를 세기때문에
		while (gamecolor == arr[C][R])// 오른쪽 아래로 지그제그
		{
			count++;
			C++;
			R++;
			if (R > 14 || C > 14)
				break;
		}
		// AppendTextR(count+" 3번 대각선 count 값");
		// 왼쪽 위에서 오른쪽 아래로 대각선검사 끝
		if (count == 5) {
			// 승리
			ChatMsg msg = new ChatMsg(UserName, "800", "Win");//여기서 막힘
			SendObject(msg);
			JOptionPane.showMessageDialog(gamePanel, "승리했습니다");
			cleanup();
		}

		// 4번 오른쪽 위에서 왼쪽 아래로 대각선
		count = 0;
		R = row;
		C = col;
		while (gamecolor == arr[C][R])// 오른쪽 위로 지그제그
		{
			count++;
			C--;
			R++;
			if (C < 0 || R > 14)
				break;
		}
		R = row;
		C = col;
		count--;//같은자리를 세기때문에
		while (gamecolor == arr[C][R])// 왼쪽 아래로 지그제그
		{
			count++;
			C++;
			R--;
			if (R < 0 || C > 14)
				break;
		}
		// AppendTextR(count+" 4번 대각선 count 값");
		// 오른쪽 위에서 왼쪽 아래로 대각선검사 끝
		if (count == 5) {
			// 승리
			ChatMsg msg = new ChatMsg(UserName, "800", "Win");//여기서 막힘
			SendObject(msg);
			JOptionPane.showMessageDialog(gamePanel, "승리했습니다");
			cleanup();
			
		}

	}

	public synchronized int Searchrule(int col, int row, int gamecolor)// 33인지 아닌지 검사
	{
		int ban = 0;// 33인지아닌지 판별 1이면 33임
		int R = row;
		int C = col;
		int emp1[] = { 1, 1, 0, 0 };// 일반적 33 구조 예시1
		int emp2[] = { 1, 1, 0, 1 };// 일반적 33 구조 예시2
		int emp3[] = { 1, 1, 0, 2 };// 일반적 33 구조 예시2
		int emp4[] = { 0, 1, 1, 0 };// 한칸 띄워져있는 구조
		int emp5[] = { 1, 0, 1, 0 };// 한칸씩 띄워져있는 구조
		int temp[] = new int[4];// 총 4칸 감시	

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
		// 1번 오른쪽검사 시작
		for(int i=0;i<4;i++)//temp 초기화 혹시모를 
		{
			temp[i]=0;
		}
		for (int i = 0; i < 4; i++)// 오른쪽 4번
		{
			if (R < 14)
				R++;
			else
				break;
			
			if (arr[C][R] == 1)// 검은돌
			{
				temp[i] = 1;
			} else if (arr[C][R] == 2)// 흰돌
			{
				temp[i] = 2;
			} else if (arr[C][R] == 0)// 비어있음
			{
				temp[i] = 0;
			}	
		}
		
		if (Arrays.equals(emp1, temp) || Arrays.equals(emp2, temp) || Arrays.equals(emp3, temp)
				|| Arrays.equals(emp4, temp) || Arrays.equals(emp5, temp)) {
			// 비교햇는데 1,1,0,0 이다
			// 비교햇는데 1,1,0,1 이다
			// 비교햇는데 1,1,0,2 이다
			// 비교햇는데 0,1,1,0 이다
			// 비교햇는데 0,1,0,1 이다
			right = true;
			//AppendTextR("오른쪽 true  "+R);
		}
		
		// 위쪽 검사
		for(int i=0;i<4;i++)//temp 초기화 혹시모를 
		{
			temp[i]=0;
		}
		R = row;
		C = col;
		for (int i = 0; i < 4; i++)// 위로 4번
		{
			if (C > 0)
				C--;
			else
				break;
			
			if (arr[C][R] == 1)// 검은돌
			{
				temp[i] = 1;
			} else if (arr[C][R] == 2)// 흰돌
			{
				temp[i] = 2;
			} else if (arr[C][R] == 0)// 비어있음
			{
				temp[i] = 0;
			}	
		}
		if (Arrays.equals(emp1, temp) || Arrays.equals(emp2, temp) || Arrays.equals(emp3, temp)
				|| Arrays.equals(emp4, temp) || Arrays.equals(emp5, temp)) {
			// 비교햇는데 1,1,0,0 이다
			// 비교햇는데 1,1,0,1 이다
			// 비교햇는데 1,1,0,2 이다
			// 비교햇는데 0,1,1,0 이다
			// 비교햇는데 0,1,0,1 이다
			up = true;
			//AppendTextR("위쪽 true");
		}
		
		// 아래쪽 검사
		for(int i=0;i<4;i++)//temp 초기화 혹시모를 
		{
			temp[i]=0;
		}
		R = row;
		C = col;
		for (int i = 0; i < 4; i++)// 아래로 4번
		{
			if (C < 14)
				C++;
			else
				break;
			
			if (arr[C][R] == 1)// 검은돌
			{
				temp[i] = 1;
			} else if (arr[C][R] == 2)// 흰돌
			{
				temp[i] = 2;
			} else if (arr[C][R] == 0)// 비어있음
			{
				temp[i] = 0;
			}	

		}
		if (Arrays.equals(emp1, temp) || Arrays.equals(emp2, temp) || Arrays.equals(emp3, temp)
				|| Arrays.equals(emp4, temp) || Arrays.equals(emp5, temp)) {
			// 1,1,0,0 이다
			//  1,1,0,1 이다
			//  1,1,0,2 이다
			// 0,1,1,0 이다
			//  0,1,0,1 이다
			down = true;
			//AppendTextR("아래 true");
		}
		// 왼쪽 검사
		R = row;
		C = col;
		for(int i=0;i<4;i++)//temp 초기화 혹시모를 
		{
			temp[i]=0;
		}
		for (int i = 0; i < 4; i++)// 왼쪽으로 4번
		{
			if (R > 0)
				R--;
			else
				break;
			
			if (arr[C][R] == 1)// 검은돌
			{
				temp[i] = 1;
			} else if (arr[C][R] == 2)// 흰돌
			{
				temp[i] = 2;
			} else if (arr[C][R] == 0)// 비어있음
			{
				temp[i] = 0;
			}
		}
		if (Arrays.equals(emp1, temp) || Arrays.equals(emp2, temp) || Arrays.equals(emp3, temp)
				|| Arrays.equals(emp4, temp) || Arrays.equals(emp5, temp)) {
			// 비교햇는데 1,1,0,0 이다
			// 비교햇는데 1,1,0,1 이다
			// 비교햇는데 1,1,0,2 이다
			// 비교햇는데 0,1,1,0 이다
			// 비교햇는데 0,1,0,1 이다
			left = true;
			//AppendTextR("왼쪽 true");
		}
		
		//십자
		R = row;
		C = col;
		int emp6[]= {1,0,0};
		int emp7[]= {1,0,1};
		int emp8[]= {1,0,2};
		int emp9[]= {0,1,0};
		
		int line1[]= {0,0,0};
		int line2[]= {0,0,0};
		
		for (int i = 0; i < 3; i++)// 상하 먼저
		{
			if(C<0 || C>14)
			{
				break;
			}
			else if(C-i-1<0 || C+i+1>14)
			{
				break;
			}
			
			if (arr[C-i-1][R] == 1)// 검은돌
			{
				line1[i] = 1;
			} else if (arr[C-i-1][R] == 2)// 흰돌
			{
				line1[i] = 2;
			} else if (arr[C-i-1][R] == 0)// 비어있음
			{
				line1[i] = 0;
			}
			
			if (arr[C+i+1][R] == 1)// 검은돌
			{
				line2[i] = 1;
			} else if (arr[C+i+1][R] == 2)// 흰돌
			{
				line2[i] = 2;
			} else if (arr[C+i+1][R] == 0)// 비어있음
			{
				line2[i] = 0;
			}
		}
		if ((Arrays.equals(emp6, line1) || Arrays.equals(emp7, line1) || Arrays.equals(emp8, line1)	|| Arrays.equals(emp9, line1) )
				&& ( Arrays.equals(emp6, line2) || Arrays.equals(emp7, line2) || Arrays.equals(emp8, line2)	|| Arrays.equals(emp9, line2) ))
		{
			crossud = true;
			//AppendTextR("크로스위아래 true");
		}
		
		for(int i=0;i<3;i++)// line1,2 초기화 혹시모를 
		{
			line1[i]=0;
			line2[i]=0;
		}
		for (int i = 0; i < 3; i++)//좌우 크로스
		{
			if(R<0 || R>14)
			{
				break;
			}
			else if(R-i-1<0 || R+i+1>14)
			{
				break;
			}
			
			if (arr[C][R-i-1] == 1)// 검은돌
			{
				line1[i] = 1;
			} else if (arr[C][R-i-1] == 2)// 흰돌
			{
				line1[i] = 2;
			} else if (arr[C][R-i-1] == 0)// 비어있음
			{
				line1[i] = 0;
			}
			
			if (arr[C][R+i+1] == 1)// 검은돌
			{
				line2[i] = 1;
			} else if (arr[C][R+i+1] == 2)// 흰돌
			{
				line2[i] = 2;
			} else if (arr[C][R+i+1] == 0)// 비어있음
			{
				line2[i] = 0;
			}
		}
		if ((Arrays.equals(emp6, line1) || Arrays.equals(emp7, line1) || Arrays.equals(emp8, line1)	|| Arrays.equals(emp9, line1) )
				&& ( Arrays.equals(emp6, line2) || Arrays.equals(emp7, line2) || Arrays.equals(emp8, line2)	|| Arrays.equals(emp9, line2) ))
		{
			crossrl = true;
			//AppendTextR("크로스오른쪽왼쪽 true");
		}
		// 대각선 비교 시작
		boolean rightup = false;
		boolean rightdown = false;
		boolean leftup = false;
		boolean leftdown = false;
		boolean crossruld = false;
		boolean crosslurd = false;

		R = row;
		C = col;
		// 1번 오른쪽위대각선 검사 시작
		for(int i=0;i<4;i++)//temp 초기화 혹시모를 
		{
			temp[i]=0;
		}
		for (int i = 0; i < 4; i++)// 오른쪽위대각 4번
		{
			if (R < 14 && C >0)
			{
				R++;
				C--;
			}
			else
				break;
			
			if (arr[C][R] == 1)// 검은돌
			{
				temp[i] = 1;
			} else if (arr[C][R] == 2)// 흰돌
			{
				temp[i] = 2;
			} else if (arr[C][R] == 0)// 비어있음
			{
				temp[i] = 0;
			}
		}
		if (Arrays.equals(emp1, temp) || Arrays.equals(emp2, temp) || Arrays.equals(emp3, temp)
				|| Arrays.equals(emp4, temp) || Arrays.equals(emp5, temp)) {
			rightup = true;
			//AppendTextR("오른쪽위 true");
		}
		
		R = row;
		C = col;
		// 2번 왼쪽위대각선 검사 시작
		for(int i=0;i<4;i++)//temp 초기화 혹시모를 
		{
			temp[i]=0;
		}
		for (int i = 0; i < 4; i++)// 오른쪽위대각 4번
		{
			if (R >0 && C >0)
			{
				R--;
				C--;
			}
			else
				break;
			
			if (arr[C][R] == 1)// 검은돌
			{
				temp[i] = 1;
			} else if (arr[C][R] == 2)// 흰돌
			{
				temp[i] = 2;
			} else if (arr[C][R] == 0)// 비어있음
			{
				temp[i] = 0;
			}
		}
		if (Arrays.equals(emp1, temp) || Arrays.equals(emp2, temp) || Arrays.equals(emp3, temp)
				|| Arrays.equals(emp4, temp) || Arrays.equals(emp5, temp)) {
			leftup = true;
			//AppendTextR("왼쪽위 true");
		}
		
		R = row;
		C = col;
		// 3번 왼쪽아래대각선 검사 시작
		for(int i=0;i<4;i++)//temp 초기화 혹시모를 
		{
			temp[i]=0;
		}
		for (int i = 0; i < 4; i++)// 오른쪽위대각 4번
		{
			if (R >0 && C<14)
			{
				R--;
				C++;
			}
			else
				break;
			
			if (arr[C][R] == 1)// 검은돌
			{
				temp[i] = 1;
			} else if (arr[C][R] == 2)// 흰돌
			{
				temp[i] = 2;
			} else if (arr[C][R] == 0)// 비어있음
			{
				temp[i] = 0;
			}

		}
		if (Arrays.equals(emp1, temp) || Arrays.equals(emp2, temp) || Arrays.equals(emp3, temp)
					|| Arrays.equals(emp4, temp) || Arrays.equals(emp5, temp)) {
				leftdown = true;
				//AppendTextR("왼쪽아래 true");
		}
		R = row;
		C = col;
		// 4번 오른쪽아래대각선 검사 시작
		for(int i=0;i<4;i++)//temp 초기화 혹시모를 
		{
			temp[i]=0;
		}
		for (int i = 0; i < 4; i++)// 오른쪽위대각 4번
		{
			if (R<14 && C<14)
			{
				R++;
				C++;
			}
			else
				break;
			
			if (arr[C][R] == 1)// 검은돌
			{
				temp[i] = 1;
			} else if (arr[C][R] == 2)// 흰돌
			{
				temp[i] = 2;
			} else if (arr[C][R] == 0)// 비어있음
			{
				temp[i] = 0;
			}
		}
		if (Arrays.equals(emp1, temp) || Arrays.equals(emp2, temp) || Arrays.equals(emp3, temp)
					|| Arrays.equals(emp4, temp) || Arrays.equals(emp5, temp)) {
				rightdown = true;
				//AppendTextR("오른쪽아래 true");
		}

		
		R = row;
		C = col;
		for(int i=0;i<3;i++)// line1,2 초기화 혹시모를 
		{
			line1[i]=0;
			line2[i]=0;
		}
		for (int i = 0; i < 3; i++)// 오른쪽위에서 왼쪽아래 먼저
		{
			if((C<0 || C>14) || (R<0 || R>14))
			{
				break;
			}
			else if((C-i-1<0 || C+i+1>14) || (R-i-1<0 || R+i+1>14))
			{
				break;
			}
			
			if (arr[C-i-1][R+i+1] == 1)// 검은돌
			{
				line1[i] = 1;
			} else if (arr[C-i-1][R+i+1] == 2)// 흰돌
			{
				line1[i] = 2;
			} else if (arr[C-i-1][R+i+1] == 0)// 비어있음
			{
				line1[i] = 0;
			}
			
			if (arr[C+i+1][R-i-1] == 1)// 검은돌
			{
				line2[i] = 1;
			} else if (arr[C+i+1][R-i-1] == 2)// 흰돌
			{
				line2[i] = 2;
			} else if (arr[C+i+1][R-i-1] == 0)// 비어있음
			{
				line2[i] = 0;
			}
		}
		if ((Arrays.equals(emp6, line1) || Arrays.equals(emp7, line1) || Arrays.equals(emp8, line1)	|| Arrays.equals(emp9, line1) )
					&& ( Arrays.equals(emp6, line2) || Arrays.equals(emp7, line2) || Arrays.equals(emp8, line2)	|| Arrays.equals(emp9, line2) ))
			{
				crossruld = true;
				//AppendTextR("오른쪽위에서 왼쪽아래대각선 true");
			}
		for(int i=0;i<3;i++)// line1,2 초기화 혹시모를 
		{
			line1[i]=0;
			line2[i]=0;
		}
		for (int i = 0; i < 3; i++)// 왼쪽위에서 오른쪽아래로 크로스
		{
			if((R<0 || R>14) || (C<0 || C>14))
			{
				break;
			}
			else if((R-i-1<0 || R+i+1>14) || (C-i-1<0 || C+i+1>14))
			{
				break;
			}
			
			if (arr[C-i-1][R-i-1] == 1)// 검은돌
			{
				line1[i] = 1;
			} else if (arr[C-i-1][R-i-1] == 2)// 흰돌
			{
				line1[i] = 2;
			} else if (arr[C-i-1][R-i-1] == 0)// 비어있음
			{
				line1[i] = 0;
			}
			
			if (arr[C+i+1][R+i+1] == 1)// 검은돌
			{
				line2[i] = 1;
			} else if (arr[C+i+1][R+i+1] == 2)// 흰돌
			{
				line2[i] = 2;
			} else if (arr[C+i+1][R+i+1] == 0)// 비어있음
			{
				line2[i] = 0;
			}
		}
		if ((Arrays.equals(emp6, line1) || Arrays.equals(emp7, line1) || Arrays.equals(emp8, line1)	|| Arrays.equals(emp9, line1) )
					&& ( Arrays.equals(emp6, line2) || Arrays.equals(emp7, line2) || Arrays.equals(emp8, line2)	|| Arrays.equals(emp9, line2) ))
			{
				crosslurd = true;
				//AppendTextR("왼쪽위에서 오른쪽아래대각선 true");
			}
		// 33에 만족하는 조건일경우
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.DNet.DNetAPI;
import com.syc.Function;

public class MainWindow {

	private JFrame frame;
	private JTextField ledCycle;
	private JTextField ledTimes;
	private JTextField bzrCycle;
	private JTextField sysSerNum;
	private JTextField bzrTimes;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	private final ButtonGroup buttonGroup_2 = new ButtonGroup();
	private final ButtonGroup buttonGroup_3 = new ButtonGroup();
	private JTextField mfwriteData;
	private final ButtonGroup buttonGroup_10 = new ButtonGroup();
	private final ButtonGroup buttonGroup_11 = new ButtonGroup();
	Socket m_conSocket;
	DataOutputStream m_out;
	DataInputStream m_in;
	int m_iConnect = 0;

	Thread m_ThreadRec;
	private JTextField name;
	private JTextField ipAdd;
	private JTextField workport;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	private byte[] getByteArray(String str) {
		str = str.replaceAll("[^0-9A-Fa-f]", "");
		byte[] ans = new byte[str.length() / 2];
		for (int i = 0; i < str.length(); i += 2) {
			ans[i / 2] = (byte) Integer.parseInt(str.substring(i, i + 2), 16);
		}
		return ans;
	}

	private void showData(JTextArea dataArea, byte[] data, String str, int pos,
			int len) {
		String dStr = "";
		for (int i = 0; i < len; i++) {
			dStr += String.format("%02x ", data[i + pos]);
		}
		dataArea.append(str + dStr.toUpperCase() + '\n');
	}

	private void showData(JTextArea dataArea, byte[] data, String str) {
		String dStr = "";
		for (byte b : data) {
			dStr += String.format("%02x ", b);
		}
		dataArea.append(str + dStr.toUpperCase() + '\n');
	}

	void output(JTextArea dataArea, String data) {
		dataArea.setText(dataArea.getText() + data);
	}

	private void showStatue(JTextArea dataArea, int Code) {
		String msg = null;
		switch (Code) {
		case 0x00:
			msg = "Command succeed.....";
			break;
		case 0x01:
			msg = "Command failed.....";
			break;
		case 0x02:
			msg = "Checksum error.....";
			break;
		case 0x03:
			msg = "Not selected COM port.....";
			break;
		case 0x04:
			msg = "Reply time out.....";
			break;
		case 0x05:
			msg = "Check sequence error.....";
			break;
		case 0x07:
			msg = "Check sum error.....";
			break;
		case 0x0A:
			msg = "The parameter value out of range.....";
			break;
		case 0x80:
			msg = "Command OK.....";
			break;
		case 0x81:
			msg = "Command FAILURE.....";
			break;
		case 0x82:
			msg = "Reader reply time out error.....";
			break;
		case 0x83:
			msg = "The card does not exist.....";
			break;
		case 0x84:
			msg = "The data is error.....";
			break;
		case 0x85:
			msg = "Reader received unknown command.....";
			break;
		case 0x87:
			msg = "Error.....";
			break;
		case 0x89:
			msg = "The parameter of the command or the format of the command error.....";
			break;
		case 0x8A:
			msg = "Some error appear in the card InitVal process.....";
			break;
		case 0x8B:
			msg = "Get the wrong snr during anticollison loop.....";
			break;
		case 0x8C:
			msg = "The authentication failure.....";
			break;
		case 0x8F:
			msg = "Reader received unknown command.....";
			break;
		case 0x90:
			msg = "The card do not support this command.....";
			break;
		case 0x91:
			msg = "The foarmat of the command error.....";
			break;
		case 0x92:
			msg = "Do not support option mode.....";
			break;
		case 0x93:
			msg = "The block do not exist.....";
			break;
		case 0x94:
			msg = "The object have been locked.....";
			break;
		case 0x95:
			msg = "The lock operation do not success.....";
			break;
		case 0x96:
			msg = "The operation do not success.....";
			break;
		}
		msg += '\n';
		dataArea.append(msg);
	}


	private void showDNetStatue(JTextArea dataArea, int Code) {
		String msg = null;
		switch (Code) {
		case 0:
			msg = "Common Error";
			break;
		case 1:
			msg = "Success";
			break;
		case 100:
			msg = "Failed to get device information";
			break;
		case 102:
			msg = "Wrong Password";
			break;
		case 103:
			msg = "Connect failed";
			break;
		case 104:
			msg = "No Response";
			break;
		case 105:
			msg = "No Login";
			break;
		case 106:
			msg = "Invalid Command";
			break;
		case 107:
			msg = "Invalid Parameter Format";
			break;
		case 108:
			msg = "Invalid Parameter Length";
			break;
		default:
			msg = "Invalid Error Code";
			break;
		}
		msg += '\n';
		dataArea.append(msg);
	}

	public void TCPClose() {
		try {
			m_conSocket.close();
			m_out.close();
			m_in.close();
		} catch (IOException ioexception) {
		}
	}

	public int TCPInit(String strip, int port) {
		try {
			m_conSocket = new Socket(strip, port);
			m_conSocket.setSoTimeout(2000);
			m_out = new DataOutputStream(m_conSocket.getOutputStream());
			m_in = new DataInputStream(m_conSocket.getInputStream());
		} catch (UnknownHostException unknownhostexception) {
			System.err.println("Don't know about host: taranis.");
			return 0;
		} catch (IOException ioexception) {
			System.err
					.println("Couldn't get I/O for the connection to: taranis.");
			return 0;
		}
		return 1;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		/**
		 * The MainWindow UI.
		 */
		frame = new JFrame();
		frame.setBounds(100, 100, 906, 602);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(0, 2, 0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane);

		JPanel panel = new JPanel();
		tabbedPane.addTab("System Settings", null, panel, null);
		panel.setLayout(null);

		final JButton btnOpen = new JButton("Connect");
		btnOpen.setEnabled(false);

		btnOpen.setBounds(231, 131, 95, 25);
		panel.add(btnOpen);

		final JButton btnClose = new JButton("Disconnect");

		btnClose.setEnabled(false);
		btnClose.setBounds(338, 131, 95, 25);
		panel.add(btnClose);
		
		JLabel label_5 = new JLabel("Set SerNum:");
		label_5.setBounds(10, 422, 95, 15);
		panel.add(label_5);

		sysSerNum = new JTextField();
		sysSerNum.setText("AA BB AA BB AA BB AA BB");
		sysSerNum.setBounds(98, 420, 170, 19);
		panel.add(sysSerNum);
		sysSerNum.setColumns(10);

		JButton btnSetSernum = new JButton("Set SerNum");

		btnSetSernum.setBounds(302, 417, 131, 25);
		panel.add(btnSetSernum);

		JLabel lblReadSernum = new JLabel("Read SerNum:");
		lblReadSernum.setBounds(10, 452, 113, 15);
		panel.add(lblReadSernum);

		JButton btnReadSernum = new JButton("Read SerNum");

		btnReadSernum.setBounds(302, 447, 131, 25);
		panel.add(btnReadSernum);

		JLabel lblLed = new JLabel("Led:");
		lblLed.setBounds(10, 368, 70, 15);
		panel.add(lblLed);

		ledCycle = new JTextField();
		ledCycle.setText("18");
		ledCycle.setBounds(94, 368, 26, 19);
		panel.add(ledCycle);
		ledCycle.setColumns(10);

		JLabel lblNewLabel = new JLabel("Cycle");
		lblNewLabel.setBounds(126, 368, 46, 15);
		panel.add(lblNewLabel);

		ledTimes = new JTextField();
		ledTimes.setText("09");
		ledTimes.setColumns(10);
		ledTimes.setBounds(186, 366, 26, 19);
		panel.add(ledTimes);

		JLabel lblTimes = new JLabel("Times");
		lblTimes.setBounds(222, 368, 46, 15);
		panel.add(lblTimes);

		JButton btnSetLed = new JButton("Set LED");

		btnSetLed.setBounds(302, 363, 131, 25);
		panel.add(btnSetLed);

		JLabel lblBuzzer = new JLabel("Buzzer:");
		lblBuzzer.setBounds(10, 395, 70, 15);
		panel.add(lblBuzzer);

		bzrCycle = new JTextField();
		bzrCycle.setText("18");
		bzrCycle.setColumns(10);
		bzrCycle.setBounds(94, 393, 26, 19);
		panel.add(bzrCycle);

		JLabel lblCycle = new JLabel("Cycle");
		lblCycle.setBounds(126, 395, 46, 15);
		panel.add(lblCycle);

		bzrTimes = new JTextField();
		bzrTimes.setText("09");
		bzrTimes.setColumns(10);
		bzrTimes.setBounds(186, 393, 26, 19);
		panel.add(bzrTimes);

		JLabel lblTimes_1 = new JLabel("Times");
		lblTimes_1.setBounds(222, 395, 46, 15);
		panel.add(lblTimes_1);

		JButton btnSetBuzzer = new JButton("Set Buzzer");

		btnSetBuzzer.setBounds(302, 390, 131, 25);
		panel.add(btnSetBuzzer);

		JButton scanButton = new JButton("Scan");

		scanButton.setBounds(20, 131, 70, 25);
		panel.add(scanButton);
		
		JLabel lblName = new JLabel("Name\uFF1A");
		lblName.setBounds(101, 183, 70, 15);
		panel.add(lblName);
		
		name = new JTextField();
		name.setColumns(10);
		name.setBounds(164, 181, 148, 19);
		panel.add(name);
		
		JLabel lblIp = new JLabel("IP Adress\uFF1A");
		lblIp.setBounds(101, 210, 70, 15);
		panel.add(lblIp);
		
		ipAdd = new JTextField();
		ipAdd.setColumns(10);
		ipAdd.setBounds(164, 208, 148, 19);
		panel.add(ipAdd);
		
		JLabel lblPort = new JLabel("Port\uFF1A");
		lblPort.setBounds(101, 237, 70, 15);
		panel.add(lblPort);
		
		workport = new JTextField();
		workport.setColumns(10);
		workport.setBounds(164, 235, 148, 19);
		panel.add(workport);
		
		final JButton setInfo = new JButton("Set Information");
		
		setInfo.setEnabled(false);
		setInfo.setBounds(164, 262, 126, 25);
		panel.add(setInfo);
		
		final JButton getInfo = new JButton("Get Information");
		
		getInfo.setEnabled(false);
		getInfo.setBounds(98, 132, 123, 25);
		panel.add(getInfo);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(10, 10, 423, 117);
		panel.add(scrollPane_2);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"IP", "Version", "MAC", "Type", "Method", "TCP Port"
			}
		) {
			boolean[] columnEditables = new boolean[] {
				false, false, false, false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		scrollPane_2.setViewportView(table);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("14443A-MF", null, panel_2, null);
		panel_2.setLayout(null);

		JLabel lblTypeA = new JLabel("Type A");
		lblTypeA.setBounds(12, 12, 57, 15);
		panel_2.add(lblTypeA);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_3.setBounds(12, 30, 95, 25);
		panel_2.add(panel_3);
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.X_AXIS));

		final JRadioButton readIdle = new JRadioButton("Idle");
		buttonGroup.add(readIdle);
		panel_3.add(readIdle);
		readIdle.setSelected(true);

		JRadioButton rdbtnAll = new JRadioButton("All");
		buttonGroup.add(rdbtnAll);
		panel_3.add(rdbtnAll);

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_4.setBounds(108, 30, 130, 25);
		panel_2.add(panel_4);
		panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.X_AXIS));

		final JRadioButton readKeyA = new JRadioButton("Key A");
		buttonGroup_1.add(readKeyA);
		panel_4.add(readKeyA);
		readKeyA.setSelected(true);

		JRadioButton radioButton_1 = new JRadioButton("Key B");
		buttonGroup_1.add(radioButton_1);
		panel_4.add(radioButton_1);

		final JComboBox mfreadAdd = new JComboBox();
		mfreadAdd.setEditable(true);
		mfreadAdd.setModel(new DefaultComboBoxModel(new String[] { "00", "01",
				"02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B",
				"0C", "0D", "0E", "0F", "10", "11", "12", "13", "14", "15",
				"16", "17", "18", "19", "1A", "1B", "1C", "1D", "1E", "1F",
				"20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
				"2A", "2B", "2C", "2D", "2E", "2F", "30", "31", "32", "33",
				"34", "35", "36", "37", "38", "39", "3A", "3B", "3C", "3D",
				"3E", "3F" }));
		mfreadAdd.setSelectedIndex(16);
		mfreadAdd.setBounds(12, 59, 46, 25);
		panel_2.add(mfreadAdd);

		JLabel lblStartAddress = new JLabel("Start Address");
		lblStartAddress.setBounds(61, 64, 106, 15);
		panel_2.add(lblStartAddress);

		final JComboBox mfreadNum = new JComboBox();
		mfreadNum.setEditable(true);
		mfreadNum.setModel(new DefaultComboBoxModel(new String[] { "01", "02",
				"03", "04" }));
		mfreadNum.setSelectedIndex(0);
		mfreadNum.setBounds(179, 59, 46, 25);
		panel_2.add(mfreadNum);

		JLabel label_8 = new JLabel("Number");
		label_8.setBounds(228, 64, 62, 15);
		panel_2.add(label_8);

		final JComboBox mfreadKey = new JComboBox();
		mfreadKey.setEditable(true);
		mfreadKey
				.setModel(new DefaultComboBoxModel(new String[] {
						"FF FF FF FF FF FF", "A0 A1 A2 A3 A4 A5",
						"B0 B1 B2 B3 B4 B5" }));
		mfreadKey.setBounds(12, 88, 150, 24);
		panel_2.add(mfreadKey);

		JLabel lblKey = new JLabel("Key");
		lblKey.setBounds(166, 91, 31, 15);
		panel_2.add(lblKey);

		JButton mfRead = new JButton("Read Card");

		mfRead.setBounds(318, 30, 117, 25);
		panel_2.add(mfRead);

		JSeparator separator = new JSeparator();
		separator.setBounds(12, 120, 423, 2);
		panel_2.add(separator);

		JLabel label_9 = new JLabel("Key");
		label_9.setBounds(166, 185, 31, 15);
		panel_2.add(label_9);

		final JComboBox mfwriteKey = new JComboBox();
		mfwriteKey.setEditable(true);
		mfwriteKey
				.setModel(new DefaultComboBoxModel(new String[] {
						"FF FF FF FF FF FF", "A0 A1 A2 A3 A4 A5",
						"B0 B1 B2 B3 B4 B5" }));
		mfwriteKey.setBounds(12, 182, 150, 24);
		panel_2.add(mfwriteKey);

		final JComboBox mfwriteAdd = new JComboBox();
		mfwriteAdd.setModel(new DefaultComboBoxModel(new String[] { "00", "01",
				"02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B",
				"0C", "0D", "0E", "0F", "10", "11", "12", "13", "14", "15",
				"16", "17", "18", "19", "1A", "1B", "1C", "1D", "1E", "1F",
				"20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
				"2A", "2B", "2C", "2D", "2E", "2F", "30", "31", "32", "33",
				"34", "35", "36", "37", "38", "39", "3A", "3B", "3C", "3D",
				"3E", "3F" }));
		mfwriteAdd.setSelectedIndex(16);
		mfwriteAdd.setEditable(true);
		mfwriteAdd.setBounds(12, 153, 46, 25);
		panel_2.add(mfwriteAdd);

		JLabel label_10 = new JLabel("Start Address");
		label_10.setBounds(61, 158, 106, 15);
		panel_2.add(label_10);

		JLabel label_11 = new JLabel("Number");
		label_11.setBounds(228, 158, 62, 15);
		panel_2.add(label_11);

		final JComboBox mfwriteNum = new JComboBox();
		mfwriteNum.setModel(new DefaultComboBoxModel(new String[] { "01", "02",
				"03", "04" }));
		mfwriteNum.setSelectedIndex(0);
		mfwriteNum.setEditable(true);
		mfwriteNum.setBounds(179, 153, 46, 25);
		panel_2.add(mfwriteNum);

		JButton mfWrite = new JButton("Write Card");

		mfWrite.setBounds(318, 124, 117, 25);
		panel_2.add(mfWrite);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(12, 235, 423, 2);
		panel_2.add(separator_1);

		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_5.setBounds(12, 124, 95, 25);
		panel_2.add(panel_5);
		panel_5.setLayout(new BoxLayout(panel_5, BoxLayout.X_AXIS));

		final JRadioButton writeIdle = new JRadioButton("Idle");
		buttonGroup_2.add(writeIdle);
		panel_5.add(writeIdle);
		writeIdle.setSelected(true);

		JRadioButton radioButton_2 = new JRadioButton("All");
		buttonGroup_2.add(radioButton_2);
		panel_5.add(radioButton_2);

		JPanel panel_6 = new JPanel();
		panel_6.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_6.setBounds(108, 124, 130, 25);
		panel_2.add(panel_6);
		panel_6.setLayout(new BoxLayout(panel_6, BoxLayout.X_AXIS));

		final JRadioButton writeKeyA = new JRadioButton("Key A");
		panel_6.add(writeKeyA);
		buttonGroup_3.add(writeKeyA);
		writeKeyA.setSelected(true);

		JRadioButton radioButton_5 = new JRadioButton("Key B");
		panel_6.add(radioButton_5);
		buttonGroup_3.add(radioButton_5);

		mfwriteData = new JTextField();
		mfwriteData.setText("FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF");
		mfwriteData.setBounds(12, 209, 213, 19);
		panel_2.add(mfwriteData);
		mfwriteData.setColumns(10);

		JLabel lblDataToWrite = new JLabel("Data to write");
		lblDataToWrite.setBounds(227, 211, 95, 15);
		panel_2.add(lblDataToWrite);

		JPanel panel_13 = new JPanel();
		panel_13.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_13.setBounds(12, 257, 95, 25);
		panel_2.add(panel_13);
		panel_13.setLayout(new BoxLayout(panel_13, BoxLayout.X_AXIS));

		final JRadioButton snrIdle = new JRadioButton("Idle");
		buttonGroup_11.add(snrIdle);
		panel_13.add(snrIdle);
		snrIdle.setSelected(true);

		JRadioButton radioButton_19 = new JRadioButton("All");
		buttonGroup_11.add(radioButton_19);
		panel_13.add(radioButton_19);

		JPanel panel_14 = new JPanel();
		panel_14.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_14.setBounds(108, 257, 130, 25);
		panel_2.add(panel_14);
		panel_14.setLayout(new BoxLayout(panel_14, BoxLayout.X_AXIS));

		final JRadioButton snrHalt = new JRadioButton("halt");
		buttonGroup_10.add(snrHalt);
		panel_14.add(snrHalt);
		snrHalt.setSelected(true);

		JRadioButton radioButton_21 = new JRadioButton("no halt");
		buttonGroup_10.add(radioButton_21);
		panel_14.add(radioButton_21);

		JButton button_2 = new JButton("GetCardNumber");

		button_2.setBounds(274, 257, 161, 25);
		panel_2.add(button_2);

		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		JLabel lblData = new JLabel("Data:");
		panel_1.add(lblData, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane, BorderLayout.CENTER);

		final JTextArea dataArea = new JTextArea();
		scrollPane.setViewportView(dataArea);
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub
				getInfo.setEnabled(true);
				btnOpen.setEnabled(true);
				setInfo.setEnabled(true);
			}
			
		});
		
		getInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int row = table.getSelectedRow();
				byte[] szmac = table.getValueAt(row, 2).toString().getBytes();
				byte[] szip = table.getValueAt(row, 0).toString().getBytes();
				byte pdevtype = Byte.parseByte(table.getValueAt(row, 3).toString());
				int result = DNetAPI.DN_GetDevInfoUDPbyMACAndIP(szmac, szip, pdevtype);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				dataArea.append("获取信息\n");
				showDNetStatue(dataArea, result);

				if (result != 1)
					return;

				byte[] nameByte = new byte[20];
				result = DNetAPI.DN_GetDevConfigUDP("NAME\0".getBytes(), nameByte);
				dataArea.append("Get Name\n");
				showDNetStatue(dataArea, result);
				name.setText(new String(nameByte).trim());
				
				byte[] IPByte = new byte[20];
				result = DNetAPI.DN_GetDevConfigUDP("IP\0".getBytes(), IPByte);
				dataArea.append("Get IP Adress\n");
				showDNetStatue(dataArea, result);
				ipAdd.setText(new String(IPByte).trim());
				
				byte[] PortByte = new byte[20];
				result = DNetAPI.DN_GetDevConfigUDP("C1_PORT\0".getBytes(), PortByte);
				dataArea.append("Get Port\n");
				showDNetStatue(dataArea, result);
				workport.setText(new String(PortByte).trim());
			}
		});
		
		scanButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = DNetAPI.DN_SearchAll();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				dataArea.append("Scanning\n");
				showDNetStatue(dataArea, result);

				if (result != 1)
					return;
				byte[] szip = new byte[20];
				byte[] szver = new byte[10];
				byte[] szmac = new byte[20];
				byte[] pdevtype = new byte[1];
				byte[] pipmode = new byte[1];
				int[] ptcpport = new int[1];
				result = DNetAPI.DN_GetSearchDev(szip, szver, szmac, pdevtype,
						pipmode, ptcpport);
				dataArea.append("Get Information\n");
				showDNetStatue(dataArea, result);
				if (result != 1)
					return;
				Vector ver = new Vector();
				ver.add(new String(szip).trim()+"\0");
				ver.add(new String(szver).trim()+"\0");
				ver.add(new String(szmac).trim()+"\0");
				ver.add(pdevtype[0]);
				ver.add(pipmode[0]);
				ver.add(ptcpport[0]);
				((DefaultTableModel)table.getModel()).setRowCount(0);
				((DefaultTableModel)table.getModel()).addRow(ver);
				getInfo.setEnabled(false);
				setInfo.setEnabled(false);
				btnOpen.setEnabled(false);
			}
		});

		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				byte[] szmac = table.getValueAt(row, 2).toString().getBytes();
				byte[] szip = table.getValueAt(row, 0).toString().getBytes();
				byte pdevtype = Byte.parseByte(table.getValueAt(row, 3).toString());
				int result = DNetAPI.DN_GetDevInfoUDPbyMACAndIP(szmac, szip, pdevtype);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				dataArea.append("Get Information\n");
				showDNetStatue(dataArea, result);

				if (result != 1)
					return;
				
				byte[] PortByte = new byte[20];
				result = DNetAPI.DN_GetDevConfigUDP("C1_PORT\0".getBytes(), PortByte);
				dataArea.append("Get Port\n");
				showDNetStatue(dataArea, result);
				
				String ip = new String(szip);
				int port = Integer.parseInt(new String(PortByte).trim());
				result = Function.Connect(ip, port);
				if (result == 1) {
					JOptionPane.showMessageDialog(frame, "Failed to connect!", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					btnClose.setEnabled(true);
					btnOpen.setEnabled(false);
				}

			}
		});
		setInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String nameStr = name.getText().trim()+"\0";
				String IPStr = ipAdd.getText().trim()+"\0";
				String PortStr = workport.getText().trim()+"\0";
				if(nameStr!="")
				{
					dataArea.append("Set Name\n");
					int result = DNetAPI.DN_SetModifyConfigUDP("NAME\0".getBytes(),nameStr.getBytes());
					showDNetStatue(dataArea,result);
				}
				if(IPStr!="")
				{
					dataArea.append("Set IP\n");
					int result = DNetAPI.DN_SetModifyConfigUDP("IP\0".getBytes(),IPStr.getBytes());
					showDNetStatue(dataArea,result);
				}
				if(PortStr!="")
				{
					dataArea.append("Set Port\n");
					int result = DNetAPI.DN_SetModifyConfigUDP("C1_PORT\0".getBytes(),PortStr.getBytes());
					showDNetStatue(dataArea,result);
				}
				int row = table.getSelectedRow();
				byte[] szmac = table.getValueAt(row, 2).toString().getBytes();
				byte[] szip = table.getValueAt(row, 0).toString().getBytes();
				byte devtype = Byte.parseByte(table.getValueAt(row, 3).toString());
				int result = DNetAPI.DN_ModifyDevUDPbyMACAndIP(szmac, szip, "88888\0".getBytes(), devtype);
				dataArea.append("Submit\n");
				showDNetStatue(dataArea,result);
			}
		});
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = Function.DisConnect();
				if (result == 0) {
					btnClose.setEnabled(false);
					btnOpen.setEnabled(true);
				} else {
					JOptionPane.showMessageDialog(frame, "Failed to disconnect！",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		btnSetLed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				byte freq = (byte) Integer.parseInt(ledCycle.getText()
						.toString(), 16);
				byte duration = (byte) Integer.parseInt(ledTimes.getText()
						.toString(), 16);
				byte[] buffer = new byte[1];
				int result = Function.API_ControlLED(freq, duration, buffer);
				showStatue(dataArea, result);
				if(result == 0)
					return;
				showStatue(dataArea, buffer[0]);
				dataArea.append("\n");
			}
		});

		btnSetBuzzer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				byte freq = (byte) Integer.parseInt(bzrCycle.getText()
						.toString(), 16);
				byte duration = (byte) Integer.parseInt(bzrTimes.getText()
						.toString(), 16);
				byte[] buffer = new byte[1];
				int result = Function.API_ControlBuzzer(freq, duration, buffer);
				showStatue(dataArea, result);
				if(result == 0)
					return;
				showStatue(dataArea, buffer[0]);
				dataArea.append("\n");
			}
		});

		mfRead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				byte mode1 = (byte) (readKeyA.isSelected() ? 0 : 1);
				byte mode2 = (byte) (readIdle.isSelected() ? 0 : 1);
				byte mode = (byte) ((mode1 << 1) | mode2); // reading model
				byte blk_add = (byte) Integer.parseInt(mfreadAdd
						.getSelectedItem().toString(), 16); // block address
				byte num_blk = (byte) Integer.parseInt(mfreadNum
						.getSelectedItem().toString(), 16); // block number
				byte[] snr = getByteArray(mfreadKey.getSelectedItem()
						.toString()); // key
				byte[] buffer = new byte[16 * num_blk]; // data read

				int result = Function.API_PCDRead(mode, blk_add, num_blk, snr,
						buffer);

				showStatue(dataArea, result);
				if (result == 0) {
					showData(dataArea, snr, "Card Number:\n", 0, 4);
					showData(dataArea, buffer, "Card Data:\n", 0,
							16 * num_blk);
				} else
					showStatue(dataArea, snr[0]);
				dataArea.append("\n");
			}
		});
		mfWrite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				byte mode1 = (byte) (writeKeyA.isSelected() ? 0 : 1);
				byte mode2 = (byte) (writeIdle.isSelected() ? 0 : 1);
				byte mode = (byte) ((mode1 << 1) | mode2); // model
				byte blk_add = (byte) Integer.parseInt(mfwriteAdd
						.getSelectedItem().toString(), 16); // block address
				byte num_blk = (byte) Integer.parseInt(mfwriteNum
						.getSelectedItem().toString(), 16); // block number
				byte[] snr = getByteArray(mfwriteKey.getSelectedItem()
						.toString()); // data write
				byte[] buffer = getByteArray(mfwriteData.getText().toString());
				int result = Function.API_PCDWrite(mode, blk_add, num_blk, snr,
						buffer);

				showStatue(dataArea, result);
				if (result == 0) {
					showData(dataArea, snr, "Card Number:\n", 0, 4);
				} else
					showStatue(dataArea, snr[0]);
				dataArea.append("\n");
			}
		});
		
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				byte mode;
				if (!snrIdle.isSelected())
					mode = 0x52;
				else
					mode = 0x26;
				byte halt = (byte) (snrHalt.isSelected() ? 1 : 0);
				byte[] snr = new byte[1];
				byte[] value = new byte[5]; // card number
				int result = Function.GET_SNR(mode,halt, snr, value);
				showStatue(dataArea, result);
				if (result == 0) {
					if (snr[0] == 0x00)
						dataArea.append("Only one card.....\n");
					else
						dataArea.append("More than one card......\n");
					showData(dataArea, value, "Card Number:\n", 0, 4);
				} else
					showStatue(dataArea, snr[0]);
				dataArea.append("\n");
			}
		});

		btnSetSernum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				byte[] buffer = new byte[1];
				buffer[0] = 0;
				byte[] newValue = getByteArray(sysSerNum.getText().toString());
				int result = Function.API_SetSerNum(newValue, buffer);
				String a = String.format("%02x", buffer[0]);
				showStatue(dataArea, result);
				showStatue(dataArea, buffer[0]);
				dataArea.append("\n");
			}
		});

		btnReadSernum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				byte[] buffer = new byte[9];
				int result = Function.API_GetSerNum(buffer);
				showStatue(dataArea, result);
				showData(dataArea, buffer, "SerNum: \n", 1, 8);
				dataArea.append("\n");
			}
		});

	}
}

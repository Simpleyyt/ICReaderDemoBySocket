package com.syc;

import java.io.*;
import java.net.*;

public class Function {
	private static Socket socket;
	private static DataOutputStream os;
	private static DataInputStream is;
	private static byte STX = 0x02;
	private static byte ETX = 0x03;
	private static byte[] Buffer = new byte[263];
	private static int p = 0;

	private static void clearBuffer() {
		p = 1;
		Buffer[0] = STX;
	}

	private static int checkData(byte[] data, int h, int t) {
		int check;
		int i;
		check = data[h];
		for (i = h + 1; i <= t; i++) {
			check = check ^ data[i];
		}
		return check;
	}

	private static void writeBuffer(byte data) {
		Buffer[p] = data;
		p++;
	}

	private static void writeBuffers(byte[] data, int length) {
		int i;
		for (i = 0; i < length; i++)
			writeBuffer(data[i]);
	}

	private static void copyData(byte[] s, int spos, byte[] d, int dpos, int len) {
		int i;
		for (i = 0; i < len; i++) {
			d[i + dpos] = s[i + spos];
		}
	}

	private static int sendCommand(byte command, byte[] sDATA, int sDLen,
			byte[] rDATA, byte[] Statue) {
		int result;
		clearBuffer();
		writeBuffer((byte) 0x00);
		writeBuffer((byte) (sDLen + 1));
		writeBuffer(command);
		writeBuffers(sDATA, sDLen);

		result = sendData();

		if (result != 0)
			return result;
		copyData(Buffer, 4, rDATA, 0, Buffer[2] - 1);
		Statue[0] = Buffer[3];
		return 0;
	}

	private static int sendData() {
		int length;
		int BCC;
		int i;
		BCC = checkData(Buffer, 1, p - 1);
		writeBuffer((byte) BCC);
		writeBuffer(ETX);

		System.out.printf("Send data:");
		for (i = 0; i < p; i++)
			System.out.printf("%02x ", Buffer[i]);
		System.out.printf("\n");

		try {
			os.write(Buffer, 0, p);
			os.flush();
			Thread.sleep(500);
			length = is.read(Buffer);
		} catch (Exception e) {
			return 0x82;
		}

		System.out.printf("length read: %02x\n", length);

		p = length;

		System.out.printf("Recieve data:");
		for (i = 0; i < length; i++)
			System.out.printf("%02x ", Buffer[i]);
		System.out.printf("\n");

		if (p < 6 || Buffer[0] != STX || Buffer[p - 1] != ETX
				|| Buffer[2] + 5 != p)
			return 0x05;
		if (checkData(Buffer, 1, p - 3) != Buffer[p - 2])
			return 0x02;
		return 0;
	}

	public static int Connect(String ip, int port) {
		try {
			socket = new Socket(ip, port);
			os = new DataOutputStream(socket.getOutputStream());
			is = new DataInputStream(socket.getInputStream());
		} catch (Exception e) {
			return 1;
		}

		return 0;
	}

	public static int DisConnect() {
		try {
			os.close();
			is.close();
			socket.close();
		} catch (Exception e) {
			return 1;
		}
		return 0;
	}

	public static int API_ControlLED(byte freq, byte duration, byte[] buffer) {
		byte[] Statue = new byte[1];
		byte[] DATA = new byte[2];
		DATA[0] = freq;
		DATA[1] = duration;
		int result = sendCommand((byte) 0x88, DATA, 2, buffer, Statue);
		if (result != 0)
			return result;
		return Statue[0];
	}

	public static int API_ControlBuzzer(byte freq, byte duration, byte[] buffer) {
		byte Statue[] = new byte[1];
		byte[] DATA = new byte[2];
		DATA[0] = freq;
		DATA[1] = duration;
		int result = sendCommand((byte) 0x89, DATA, 2, buffer, Statue);
		if (result != 0)
			return result;
		return Statue[0];
	}

	public static int API_PCDRead(byte mode, byte blk_add, byte num_blk,
			byte[] snr, byte[] buffer) {
		byte[] Statue = new byte[1];
		int num = (num_blk * 16 + 4) > 9 ? (num_blk * 16 + 4) : 9;
		byte[] DATA = new byte[num];
		DATA[0] = mode;
		DATA[1] = num_blk;
		DATA[2] = blk_add;
		copyData(snr, 0, DATA, 3, 6);
		int result = sendCommand((byte) 0x20, DATA, 9, DATA, Statue);
		if (result != 0)
			return result;
		copyData(DATA, 0, snr, 0, 4);
		copyData(DATA, 4, buffer, 0, num_blk * 16);
		return Statue[0];
	}

	public static int API_PCDWrite(byte mode, byte blk_add, byte num_blk,
			byte[] snr, byte[] buffer) {
		byte[] Statue = new byte[1];
		int num = num_blk * 16 + 9;
		byte[] DATA = new byte[num];
		DATA[0] = mode;
		DATA[1] = num_blk;
		DATA[2] = blk_add;
		copyData(snr, 0, DATA, 3, 6);
		copyData(buffer, 0, DATA, 9, num_blk * 16);
		int result = sendCommand((byte) 0x21, DATA, num, DATA, Statue);
		if (result != 0)
			return result;
		copyData(DATA, 0, snr, 0, 4);
		return Statue[0];
	}
	
	public static int GET_SNR( byte mode, byte API_halt, byte[]snr, byte[]value)
	{
		byte[] Statue = new byte[1];
		byte[] DATA = new byte[5];
		DATA[0] = mode;
		DATA[1] = API_halt;
		int result = sendCommand((byte) 0x25,DATA,2,DATA,Statue);
		if (result != 0)
			return result;
		snr[0] = DATA[0];
		copyData(DATA,1,value,0,4);
		return Statue[0];   
	}
	
	public static int  API_SetSerNum( byte[] newValue, byte[] buffer)
	{
		byte[] Statue = new byte[1];
		int result = sendCommand((byte)0x82,newValue,8,buffer,Statue);
		if (result != 0)
			return result;
		return Statue[0];
	}

	public static int  API_GetSerNum( byte[] buffer)
	{
		byte[] Statue = new byte[1];
		int result = sendCommand((byte) 0x83,null,0,buffer,Statue);
		if (result != 0)
			return result;
		return Statue[0];
	}
}

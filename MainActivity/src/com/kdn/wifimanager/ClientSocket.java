package com.kdn.wifimanager;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;
public class ClientSocket {
	private String ip;
	private int port;
	private Socket socket = null;
	DataOutputStream out = null;
	DataInputStream getMessageStream = null;
	public ClientSocket(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	public void createConnection() {
		Log.d("main", "in create Connection entering ");
		try {
			socket = new Socket(ip, port);
			Log.d("main", "in create Connection");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} finally {
			Log.d("main","none");
		}
	}
	public void sendMessage(String sendMessage) {
		try {
			out = new DataOutputStream(socket.getOutputStream());
			if (sendMessage.equals("Windows")) {
				out.writeByte(0x1);
				out.flush();
				return;
			}
			if (sendMessage.equals("Unix")) {
				out.writeByte(0x2);
				out.flush();
				return;
			}
			if (sendMessage.equals("Linux")) {
				out.writeByte(0x3);
				out.flush();
			} else {
				out.writeUTF(sendMessage);
				out.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	public DataInputStream getMessageStream() {
		try {
			System.out.println(socket);
			System.out.println(socket.getInputStream());
			
			getMessageStream = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));
			// return getMessageStream;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (getMessageStream != null) {
				try {
					getMessageStream.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return getMessageStream;
	}
	public void shutDownConnection() {
		try {
			if (out != null) {
				out.close();
			}
			if (getMessageStream != null) {
				getMessageStream.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String [] args){
		//ClientSocket cs = new ClientSocket("175.186.52.1", 8821);
		//cs.getMessageStream();
		System.out.println("??");
	}
}

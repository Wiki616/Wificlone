import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
public class TransferFileServer {
	private static final int HOST_PORT = 8877;
	static ArrayList filelist = new ArrayList(); 
	private void start() {
		Socket s = null;
		try {
			ServerSocket ss = new ServerSocket(HOST_PORT);
			int i;
			for (i = 0;i < filelist.size();i ++) {
				String filePath = filelist.get(i).toString();
				File file = new File(filePath);
				System.out.println("file len:" + (int) file.length());
				s = ss.accept();
				log("create Socket");
				DataInputStream dis = new DataInputStream(
						new BufferedInputStream(s.getInputStream()));
				dis.readByte();
				DataInputStream fis = new DataInputStream(
						new BufferedInputStream(new FileInputStream(filePath)));
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				dos.writeUTF(file.getName());
				dos.flush();
				dos.writeLong((long) file.length());
				dos.flush();
				int bufferSize = 8192;
				byte[] buf = new byte[bufferSize];
				while (true) {
					int read = 0;
					if (fis != null) {
						read = fis.read(buf);
					}
					if (read == -1) {
						break;
					}
					dos.write(buf,0,read);
				}
				dos.flush();
				fis.close();
				s.close();
				log("success!");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void log(String msg) {
		System.out.println(msg);
	}
	
	public static void main(String args[]) {
		byte[] buff=new byte[]{};
		try {
			FileOutputStream out=new FileOutputStream("D:\\Wificlone\\0.txt");
			refreshFileList("D:\\Wificlone");
			String txt = String.valueOf(filelist.size());
			for (int i = 0;i < filelist.size();i ++)
				txt += "\r\n" + filelist.get(i).toString();
			buff = txt.getBytes();  
			out.write(buff,0,buff.length);
			new TransferFileServer().start();
		} catch (FileNotFoundException e) {
			// TODO
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 
			e.printStackTrace();
		}
		
	}
	private static void refreshFileList(String strPath) {
		// TODO 
		File dir = new File(strPath); 
        File[] files = dir.listFiles(); 
        
        if (files == null) 
            return; 
        for (int i = 0; i < files.length; i++) { 
            if (files[i].isDirectory()) { 
                refreshFileList(files[i].getAbsolutePath()); 
            } else { 
                String strFileName = files[i].getAbsolutePath().toLowerCase();
                //System.out.println("---"+strFileName);
                filelist.add(files[i].getAbsolutePath());
                System.out.println(files[i].getAbsolutePath());
            } 
        } 
	}
}

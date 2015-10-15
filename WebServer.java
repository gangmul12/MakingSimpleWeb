import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

class WebServer {
	public static void main(String argv[]) throws Exception {
		File test = new File("log.out");
		if(test.exists())
			test.delete();
		
		
		
		String requestMessageLine;
		String fileName;
		ServerSocket ListenSocket = new ServerSocket(11466);
		int rcvdMessage = 1;
		while(true){
			try{
			Socket connectionSocket = ListenSocket.accept();
			Date rcvTime = new Date();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			requestMessageLine = inFromClient.readLine();

			StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine);
			if(tokenizedLine.nextToken().equals("GET")) {
				
				fileName = tokenizedLine.nextToken();
				if(fileName.startsWith("/")==true) 
					fileName = fileName.substring(1);
				if(!fileName.equals("log.out")){
					outToClient.writeBytes("HTTP/1.0 404 Not Found\r\n");
					outToClient.writeBytes("Content-Length: 18\r\n\r\n");
					outToClient.writeBytes("HTTP 404 Not Found");
				}
				else{File file = new File("log.out");
					if(!file.exists())
						file.createNewFile();
					String port = Integer.toString(((InetSocketAddress)connectionSocket.getRemoteSocketAddress()).getPort());
					String ip = connectionSocket.getRemoteSocketAddress().toString();

					FileWriter filewriter = new FileWriter(file,true);
					BufferedWriter bufferedwriter = new BufferedWriter(filewriter);
					PrintWriter printwriter = new PrintWriter(bufferedwriter);
					printwriter.println("#"+(rcvdMessage++)+" IP "+ip.substring(1, ip.lastIndexOf(':'))+", Port "+port +", "+rcvTime.toString());
					printwriter.close();
					bufferedwriter.close();
					filewriter.close();  
					int numOfBytes = (int) file.length();
					FileInputStream inFile = new FileInputStream (fileName);
					byte[] fileInBytes = new byte[numOfBytes];
					inFile.read(fileInBytes);
					outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
					if(fileName.endsWith(".jpg"))
						outToClient.writeBytes("Content-Type: imagge/jpeg\r\n");
					if(fileName.endsWith(".gif"))
						outToClient.writeBytes("Content-Type: image/gif\r\n");
					outToClient.writeBytes("Content-Length: "+numOfBytes+"\r\n");
					outToClient.writeBytes("\r\n");
					outToClient.write(fileInBytes, 0, numOfBytes);
				}	
				
			}
			else System.out.println("Bad Request Message");
			
			}
			catch(Exception e){
				
			}
			
		}
		
	
		}
	}

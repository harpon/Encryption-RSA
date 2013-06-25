import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.math.*;

public class Server implements Runnable
{
	Socket csocket;
	static int NumberOfClients = 0;
	static int cNum = 0;
	
	Encryption en = new Encryption();
		
	Server (Socket csocket)
	{
		this.csocket = csocket;
	}
	
	public static void main(String args[]) throws Exception
	{
		int port;
		
		//if no argument , use port 10007
		if(args.length == 0){
			port = 10007;
		}
		else{
			String portStr = args[0];
			
			try{
				port = Integer.parseInt(portStr);
			}
			catch(NumberFormatException nfe){
                
				System.out.println("Whoops, Invalid port number. Will default to 9999");
                port = 10007;
                
			}
		}
		
		//Set server socket to listening mode
		ServerSocket ssock = new ServerSocket(port);
		System.out.println("Listening");
			
		while(true){
			Socket sock = ssock.accept();
			NumberOfClients++;	
			if(NumberOfClients <= 2){
				
				cNum++;
				System.out.println("Client "+cNum+" is Connected");
				System.out.println("There is/are "+NumberOfClients+" client/s connected");
			new Thread(new Server(sock)).start();
			
			}
			else{
				System.out.println("No More Clients can be connected to the Server");
			}
		}
		
	}
	
	public void run()
	{
		int num = cNum;
		
		try
		{
			BigInteger privateKey = en.getPrivateKey();
			BigInteger publicKey = en.getPublicKey();			
			BigInteger mod = en.getMod();

			PrintStream pstream = new PrintStream(csocket.getOutputStream());
			
            		BufferedReader in = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
			
            		String inputLine;
			
			pstream.println(publicKey);
			pstream.println(mod);
			System.out.println(privateKey);
           		while((inputLine = in.readLine()) != null)
			{
				//System.out.println("From Client "+num+" disconnected from the server");
				int loop = Integer.parseInt(inputLine);
				String cipherText ="";
				String realText ="";
				for(int i = 0 ; i<loop ; i++)
				{
					BigInteger cipher = new BigInteger(in.readLine());
					cipherText += cipher.toString()+" ";
					realText += en.decrypt(cipher ,en.getPrivateKey(),en.getMod());
				}

				System.out.println("Encrypted Message:"+cipherText);
				System.out.println("Message: "+realText.toLowerCase());

				if(inputLine.equals("done"))
				{
					break;
				}
			}
			
			pstream.close();
			csocket.close();
	
			System.out.println("There is/are "+NumberOfClients+" client/sconnected now");
			NumberOfClients--;
			System.out.println("There is/are "+NumberOfClients+" client/s connected now");
		}
		catch ( IOException e)
		{
			System.out.println(e);
		}
	}
}


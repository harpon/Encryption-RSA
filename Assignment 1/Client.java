import java.io.*;
import java.net.*;
import java.math.*;
import java.lang.*;

public class Client
{
	public static void main(String[] args)
	{
		//create a socket and find it to the host/port server is listening on
		String host;
		int port;
		Encryption en = new Encryption();	
		if(args.length == 0)
		{
			host = "localhost";
			port = 10007;
		}
		else
		{
			host = args[0];
			String portStr = args[1];

			try
			{
				port = Integer.parseInt(portStr);
			}
			catch ( NumberFormatException nfe)
			{
				System.out.println("Whoops, Invalid port number. Will default to 9999");
				port = 10007;
			}
		}
		
		try
		{
			System.out.println("Client will attempt connecting to server at host=" + host + " port=" + port + ".");
			Socket skt = new Socket ( host, port);
			
			//ok , got a connection. let's use java.io.* niceties to read and write from the conection
			BufferedReader myInput = new BufferedReader(new InputStreamReader(skt.getInputStream()));
			
			BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
			
			PrintStream myOutput = new PrintStream(skt.getOutputStream());
			
			boolean done = false;
			

			String servPubKeyS = myInput.readLine();
			String modS = myInput.readLine();
			
			BigInteger servPubKey = new BigInteger(servPubKeyS);
			BigInteger mod = new BigInteger(modS); 
			System.out.println("Server public Key:"+servPubKey+","+mod);
			
			while(!done)
			{
				//prompt and read from console
				System.out.print("Enter a message , or enter \"done\" to quit:");
				
				String buf = consoleInput.readLine();
				
				if(buf != null)
				{
					if(buf.equalsIgnoreCase("done"))
					{
						done = true;
					}
					else
					{
						//write something to the server
						myOutput.println(buf.length());
						for(int i = 0 ; i < buf.length() ; i++)
						{
							char tempChar = buf.charAt(i);
							String temp = Character.toString(tempChar).toUpperCase();
							myOutput.println(en.encrypt(temp,servPubKey,mod));
                        			}
						/*
						//see if the server echoes itback
						buf = myInput.readLine();
						if(buf != null)
						{
							System.out.print("Client Received ["+buf+"] from the server!\n");
						}
						*/
					}
				}
				else
				{
					done = true;
				}
			}
			skt.close();
			System.out.println("Client is exiting");
		}
		catch( IOException ex)
		{
			ex.printStackTrace();
			System.out.println("Whoops, something bad happened! I'm Outta here.");
		}
	} 
}

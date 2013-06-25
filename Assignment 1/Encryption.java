import java.math.*;
import java.lang.*;
import java.util.*;
import java.security.*;
import java.util.Random;

public class Encryption
{
	private BigInteger publicKey;
	private BigInteger privateKey;
	private BigInteger mod;
	private BigInteger squared;

	// for chinese remainder
	BigInteger p1;
	BigInteger q1;
	
	public Encryption()
	{
		//Generate two distict prime number
		BigInteger p = generatePrime();
		BigInteger q = generatePrime();
		this.p1 = p;
		this.q1 = q;
		//Calculate 'N';
			
		BigInteger n = p.multiply(q);	
		
		//Calculate the totient;
		BigInteger totient = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
		
		BigInteger e = generatePrime();
		
		while( e.compareTo(totient) != -1 )
		{
			e = generatePrime();
		}

		this.publicKey = e;

		this.privateKey = e.modInverse(totient);
		
		this.mod = n;
	}

	private BigInteger generatePrime()
	{
		SecureRandom random = new SecureRandom();
		
		BigInteger ranPrime = BigInteger.probablePrime(16,random);
		
		return ranPrime;
	}

	private BigInteger stringToNumber(String msg)
	{
		String number = "";
		for(int i = 0 ; i < msg.length() ; i++)
		{
			char c = msg.charAt(i);
			int j = (int) c;
			number += j;
		}
		
		BigInteger num = new BigInteger(number);
		
		return num;
	}
	
	private String numberToString(BigInteger b)
	{
		String number = b.toString();
		String word = "";
		

		for(int i = 0 ; i< number.length() ; i+= 2)
		{
			//System.out.println("i = " + i);
			char c1 = number.charAt(i);
			char c2 = number.charAt(i +1);
			
			String letter = "";
			letter += c1;
			letter += c2;

			int ascii = Integer.parseInt(letter);
			char c = (char) ascii;
			word += c;
		}
		return word;
	}
	
	public BigInteger encrypt(String msg, BigInteger pubKey, BigInteger n)
	{
		BigInteger number = stringToNumber(msg);
		BigInteger c = number.pow(pubKey.intValue()).mod(n);
		return c;
	}

	public String decrypt(BigInteger cipher, BigInteger privKey, BigInteger n)
	{
		BigInteger dp = privKey.mod(this.p1.subtract(BigInteger.ONE));
		BigInteger dq = privKey.mod(this.q1.subtract(BigInteger.ONE));
		BigInteger qinv  = this.q1.modInverse(this.p1);

		BigInteger m1 = (cipher.pow(dp.intValue())).mod(this.p1);
		BigInteger m2 = (cipher.pow(dq.intValue())).mod(this.q1);

		BigInteger h;

		if(m1.compareTo(m2) == 0 || m1.compareTo(m2) ==1)
		{
			h = ( qinv.multiply(m1.subtract(m2))).mod(this.p1);
		}
		else
		{
			h = (qinv.multiply(m1.add(this.p1).subtract(m2))).mod(this.q1);
		}
	
		BigInteger m = m2.add(h.multiply(this.q1));


		//BigInteger uncode = (cipher.pow(privKey.intValue())).mod(n);
		String done = numberToString(m);
		
		//System.out.println("uncode:"+ m +" done:"+done);
		return done;
	}
		
	public BigInteger getMod()
	{
		return this.mod;
	}
	
	public BigInteger getPublicKey()
	{
		return this.publicKey;
	}
		
	public BigInteger getPrivateKey()
	{
		return this.privateKey;
	}
}

// Collin Atkins, Brandon Watson, Kyle Koch, Juanita Dickhaus
// Net Sec, Prof. Franco
// Cool Cats - Monitor Project

import java.util.*;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.math.*;

public class MessageParser {
    //Monitor Handling Declarations
    int COMMAND_LIMIT = 25;
    public int CType;
    public static String HOSTNAME;
    PrintWriter out = null; 
    BufferedReader in = null; 
    String mesg,sentmessage;
    String filename;
    StringTokenizer t;
    // IDENT, PASS, and COOKIE should all be overwritten at the beginning
    String IDENT = "ident";
    String PASSWORD = "password";
    String COOKIE = "cookie";
    int HOST_PORT;

    // Keys used for DiffieHellmanExchange and Karn
    BigInteger our_pub;
    BigInteger mon_pub;
    BigInteger shared_sec;

    public MessageParser() {
	filename = "password.dat";
	GetIdentification();
    }

    public MessageParser(String ident, String password) {
	// Sets up file with ident as filename to store pass and cookie
	filename = ident+".dat";
	PASSWORD = password;
	IDENT = ident;
	GetIdentification();
    }

    // Gets the monitor's message that was sent to us
    // cryptSwitch is used to tell if encryption is currently being used
    // Rewritten to work more consistently with my login
    public String GetMonitorMessage(boolean cryptSwitch) {
	String sMesg="", temp="";
	try {
	    if(cryptSwitch) {
		Karn karn = new Karn(shared_sec);
		sMesg = karn.decrypt(in.readLine());
		//System.out.println(sMesg);
		if(sMesg.trim().equals("WAITING:")) {
		    sMesg = karn.decrypt(in.readLine());
		    return sMesg;
		}
		else {
		    if(sMesg.substring(0,6).equals("RESULT")) {
			return sMesg;
		    }
		    temp = karn.decrypt(in.readLine());
		    if(!(temp.trim().equals("WAITING:"))) {
			sMesg = sMesg.concat(" ");
			sMesg = sMesg.concat(temp);
		    }
		}
	    }
	    else {
		sMesg = in.readLine();
		
		if(sMesg.trim().equals("WAITING:")) {
		    sMesg = in.readLine();
		}
		else {
		    temp = in.readLine();
		    if(!(temp.trim().equals("WAITING:"))) {
			sMesg = sMesg.concat(" ");
			sMesg = sMesg.concat(temp);
		    }
		}
	    }
	} catch (IOException e) {
	    System.out.println("MessageParser [getMonitorMessage]: error "+
			       "in GetMonitorMessage:\n\t"+e+this);
	    sMesg="";
	} catch (NullPointerException n) {
	    System.out.println("MessageParser [getMonitorMessage]: error "+
			       "as a null pointer exception\n\t");
	    sMesg="";
	} catch (NumberFormatException o) {
	    System.out.println("MessageParser [getMonitorMessage]: number "+
			       "format error:\n\t"+o+this);
	    sMesg="";
	} catch (NoSuchElementException ne) {
	    System.out.println("MessageParser [getMonitorMessage]: no such "+
			       "element exception occurred:\n\t"+this);
	} catch (ArrayIndexOutOfBoundsException ae) {
	    System.out.println("MessageParser [getMonitorMessage]: AIOB "+
			       "EXCEPTION!\n\t"+this);
	    sMesg="";
	}
	return sMesg;
    }

    // Gets the command the monitor is asking for
    public String GetNextCommand (String mesg) {
	try {
	    String sDefault = "REQUIRE";
	    t = new StringTokenizer(mesg," :\n");
	    //Search for the REQUIRE Command
	    String temp = t.nextToken();
	    // Trims off what is after REQUIRE
	    while (!(temp.trim().equals(sDefault.trim()))) temp = t.nextToken();
	    temp = t.nextToken();
	    return temp;
	} catch (NoSuchElementException e) {
	    System.out.println("MessageParser [getNextCommand]: No such element");
	    return null;
	}
    }

    // Trims off the cookie/key/ppc/etc and returns it
    // Give the text before it 'cmdTrim' and the mesg it is in
    public String GetTheArg (String cmdTrim, String mesg) {
	try {
	    t = new StringTokenizer(mesg," :\n");
	    //Search for the cmdTrim Command
	    String temp = t.nextToken();
	    while (!(temp.trim().equals(cmdTrim.trim()))) temp = t.nextToken();
	    // Trims off what is after cmdTrim and after that
	    temp = t.nextToken();
	    return temp;
	} catch (NoSuchElementException e) {  return null;  }
    }
    
    public boolean Login() { 
	boolean success = false;
	try {
	    // Use Diffie Hellman to make a public key
	    DiffieHellmanExchange dhe = new DiffieHellmanExchange();
	    our_pub = dhe.getDHParmMakePublicKey("DHKey");
	    String our_pub_s = our_pub.toString(32).trim();

	    String msg = GetMonitorMessage(false);
	    // CType = 0 is Client, 1 is Server
	    if(CType == 0) {
		System.out.println("Client [IDENT]:\n\t"+msg);
		String cmd = GetNextCommand(msg);
		// Sends our public key to monitor
		Execute(cmd, our_pub_s);
		
		// Gets the monitors public key sent to us
		msg = GetMonitorMessage(false);
		System.out.println("Monitor:\n\t" + msg);
		String mon_pub_s = GetTheArg("IDENT", msg);
		mon_pub = new BigInteger(mon_pub_s, 32);

		// Build a Karn encryptor from the shared secret
		shared_sec = dhe.getSecret(mon_pub);
		Karn karn = new Karn(shared_sec);

		msg = GetMonitorMessage(true);
		System.out.println("\nClient [PASS/ALIVE]:\n\t" + msg);
		cmd = GetNextCommand(msg);

		if( cmd.equals("PASSWORD") ) {
		    Execute(cmd);
		    msg = GetMonitorMessage(true);
		    System.out.println("Monitor:\n\t" + msg);
		    // Updates the password and cookie variable and file
		    UpdatePass(PASSWORD);
		    UpdateCookie("PASSWORD", msg);
		}
		else if( cmd.equals("ALIVE") ) {
		    Execute(cmd);
		}
		else {
		    System.out.println("Client [PASS/ALIVE]: login fail.");
		    System.exit(1);
		}
	
		if( cmd.equals("PASSWORD") ) {
		    msg = GetMonitorMessage(true);
		    System.out.println("\nClient [HOST_PORT]:\n\t" + msg);
		    cmd = GetNextCommand(msg);
		    if( cmd.equals("HOST_PORT") ) {
			Execute(cmd);
		    }
		    else {
			System.out.println("Client [HOST_PORT]: login fail");
			success = false;
			System.exit(1);
		    }
		    System.out.println("Monitor:\n\t" + karn.decrypt(in.readLine()));
		}
		else if( cmd.equals("ALIVE") ) {
		    msg = GetMonitorMessage(true);
		    System.out.println("Monitor\n\t" + msg);
		}
		else { System.out.println("Monitor:\n\t" + karn.decrypt(in.readLine())); }
	    }
	    
	    if(CType == 1) {
		// Reads the extra checksum message
		msg = msg.concat(" ");
		msg = msg.concat(in.readLine());
		System.out.println("Server [IDENT]:\n\t"+msg);

		System.out.println("temp 2 " + PASSWORD);
		// Verifies the authenticity of the monitor
		String ppc = GetTheArg("PARTICIPANT_PASSWORD_CHECKSUM", msg);
		boolean result = verifyChecksum("password.dat", ppc);
		if(!result) {
		    System.out.println("DANGER! Checksum did not match! Exiting.");
		    System.exit(1);
		    success = false;
		}
		else { System.out.println("Checksum passed!\n"); }
		
		String cmd = GetNextCommand(msg);
		Execute(cmd, our_pub_s);

		// Gets the monitors public key sent to us
		msg = GetMonitorMessage(false);
		System.out.println("Monitor:\n\t" + msg);
		String mon_pub_s = GetTheArg("IDENT", msg);
		mon_pub = new BigInteger(mon_pub_s, 32);

		// Build a Karn encryptor from the shared secret
		shared_sec = dhe.getSecret(mon_pub);
		Karn karn = new Karn(shared_sec);

		msg = GetMonitorMessage(true);
		System.out.println("\nServer [ALIVE]:\n\t" + msg);
		cmd = GetNextCommand(msg);
		if( cmd.equals("ALIVE") ) {
		    Execute(cmd);
		    msg = GetMonitorMessage(true);
		    System.out.println("Monitor:\n\t" + msg);
		}
		else {
		    System.out.println("Server [PASS/ALIVE]: login fail.");
		    success = false;
		    System.exit(1);
		}

		msg = GetMonitorMessage(true);
		System.out.println("\nServer [QUIT]:\n\t" + msg);
		cmd = GetNextCommand(msg);
		if( cmd.equals("QUIT") ) {
		    Execute(cmd);
		}
		else {
		    System.out.println("Server [QUIT]: login fail");
		    success = false;
		    System.exit(1);
		}
		
		System.out.println("Monitor:\n\t" + karn.decrypt(in.readLine()));
	    }

	    success = true;
	} catch (Exception e) {
	    System.out.println("MessageParser [Login]: error\n\t" + e + this);
	    success = false;
	}
	
	System.out.println("\nSuccess Value Login = " + success);
	return success;
    }
    
    //Handle Directives and Execute appropriate commands with one argument
    public boolean Execute (String sentmessage, String arg) {
	boolean success = false;
	try {
	    if (sentmessage.trim().equals("PARTICIPANT_HOST_PORT")) {
		sentmessage = sentmessage.concat(" ");
		sentmessage = sentmessage.concat(arg);
		SendIt(sentmessage);
		success = true;
	    }
	    // Sending an ident and a pub key to enable encryption
	    if (sentmessage.trim().equals("IDENT")) {
		sentmessage = sentmessage.concat(" ");
		sentmessage = sentmessage.concat(IDENT);
		sentmessage = sentmessage.concat(" ");
		sentmessage = sentmessage.concat(arg);
		SendItSpecial(sentmessage);
		success = true;
	    }
	} catch (IOException e) {
	    System.out.println("IOError:\n\t" + e);
	    success = false;
	} catch (NullPointerException n) {
	    System.out.println("Null Error has occured");
	    success = false;
	}

	return success;
    }
    
    //Handle Directives and Execute appropriate commands
    public boolean Execute (String sentmessage) {
	boolean success = false; 
	try {
	    if (sentmessage.trim().equals("IDENT")) {
		sentmessage = sentmessage.concat(" ");
		sentmessage = sentmessage.concat(IDENT);
		SendIt (sentmessage);
		success = true;
	    } else if (sentmessage.trim().equals("PASSWORD")) {
		sentmessage = sentmessage.concat(" ");
		sentmessage = sentmessage.concat(PASSWORD);
		SendIt (sentmessage);
		success = true;  
	    } else if (sentmessage.trim().equals("HOST_PORT")) {
		sentmessage = sentmessage.concat(" ");
		sentmessage = sentmessage.concat(HOSTNAME);
		sentmessage = sentmessage.concat(" ");
		sentmessage = sentmessage.concat(String.valueOf(HOST_PORT));
		SendIt (sentmessage);
		success = true;                                  
	    } else if (sentmessage.trim().equals("ALIVE")) {
		sentmessage = sentmessage.concat(" ");
		sentmessage = sentmessage.concat(COOKIE);
		SendIt (sentmessage);
		success = true;
	    } else if (sentmessage.trim().equals("QUIT")) {
		SendIt(sentmessage);
		success = true;
	    } else if (sentmessage.trim().equals("SIGN_OFF")) {
		SendIt(sentmessage);
		success = true;
	    } else if (sentmessage.trim().equals("GET_GAME_IDENTS")) {
		SendIt(sentmessage);
		success = true;
	    } else if (sentmessage.trim().equals("PARTICIPANT_STATUS")) {
		SendIt(sentmessage);
		success = true;
	    } else if (sentmessage.trim().equals("RANDOM_PARTICIPANT_HOST_PORT")){
		SendIt(sentmessage);
		success = true;
	    }         
	} catch (IOException e) {
	    System.out.println("Message Parser [Execute]: IOError:\n\t"+e);
	    success = false;
	} catch (NullPointerException n) {
	    System.out.println("Message Parser [Execute]: Null Error has occured");
	    success = false;
	} catch (Exception e){
	    System.out.println("Message Parser [Execute]: Exception BIGINT caught");
	    success = false;
	}
      
	return success;
    }    

    // Just used for initial unencrypted sent message
    public void SendItSpecial (String message) throws IOException {
	try {        
	    System.out.println("Sent UnEnc:\n\t"+message);
	    out.println(message);
	    if (out.checkError() == true) throw (new IOException());
	    out.flush();
	    if(out.checkError() == true) throw (new IOException());
	} catch (IOException e) {} //Bubble the Exception upwards
    }

    // Sends given message to monitor
    public void SendIt (String message) throws IOException {
	try {        
	    System.out.println("Send UnEnc:\n\t"+message);
	    Karn karn = new Karn(shared_sec);
	    message = karn.encrypt(message);
	    System.out.println("Sent Enc:\n\t"+message);
	    out.println(message);
	    if (out.checkError() == true) throw (new IOException());
	    out.flush();
	    if(out.checkError() == true) throw (new IOException());
	} catch (IOException e) {}
    }

    // Updates password to newpassword
    public boolean ChangePassword() {
	boolean success = false;
	String newPass;
	BufferedReader in = null;
	try {
	    SecureRandom random = new SecureRandom();
	    newPass = new BigInteger(130, random).toString(32);
	    System.out.println("Genned Pass:\n\t" + newPass);
	    
	    String msg = "CHANGE_PASSWORD ";
	    msg = msg.concat(PASSWORD);
	    msg = msg.concat(" ");
	    msg = msg.concat(newPass);
	    SendIt(msg);

	    System.out.println("Old Password: " + PASSWORD + "\n"
			       + "New Password: " + newPass + "\n");
	    PASSWORD = newPass;
	    
	    success = true;
	} catch (IOException e) {
	    System.out.println("IOError:\n\t"+e);
	    success = false;
	} catch (NullPointerException n) {
	    System.out.println("Null Error has occured");
	    success = false;
	}

	return success;
    }
    
    // Gets Password and Cookie from 'iden.dat' file,
    // Where iden is the current ident given when ran
    public boolean GetIdentification() {
	boolean success = false;
	BufferedReader in = null;
	try { 
	    in = new BufferedReader(new FileReader(filename));
	    String line = in.readLine();
	    PASSWORD = in.readLine();
	    line = in.readLine();
	    COOKIE = in.readLine();
	    in.close();
	} catch (IOException e) { return false; }
	catch (NumberFormatException n) { return false; }
	return success;  
    }

    // Writes passwd to ident file
    public boolean UpdatePass(String Passwd) {
	int DataRet = WritePersonalData(Passwd, COOKIE);
	boolean DataCheck;
	if (DataRet == 0) { DataCheck = false; } else { DataCheck = true; }
	System.out.println("Password write success: " + DataCheck);
	return DataCheck;
    }
    // Writes cookie to ident file, given a msg with a cookie
    // Cmd is either CHANGE_PASSWORD or PASSWORD
    public boolean UpdateCookie(String cmd, String msg) {
	String Cooki = GetTheArg(cmd, msg);
	int DataRet = WritePersonalData(PASSWORD, Cooki);
	boolean DataCheck;
	if (DataRet == 0) { DataCheck = false; } else { DataCheck = true; }
	System.out.println("Cookie write success: " + DataCheck);
	return DataCheck;
    }

    // Write Personal data such as Password and Cookie
    public int WritePersonalData(String Passwd, String Cooki) {
	// Simple flag for marking write of both pass and cookie
	int flag = 0;
	PrintWriter pout = null;
	try {
	    pout = new PrintWriter(new FileWriter(filename));
	    if ((Passwd != null) && !(Passwd.equals(""))) {
		// Updates PASSWORD to new one
		PASSWORD = Passwd;
		pout.println("PASSWORD");
		pout.println(Passwd);
		flag++;
	    }   
	    if ((Cooki != null) && !(Cooki.equals(""))) {
		// Updates COOKIE to new one
		COOKIE = Cooki;
		pout.println("COOKIE");
		pout.flush();
		pout.println(Cooki);
		pout.flush();
		flag++;
	    }  
	    pout.close();
	} catch (IOException e) {
	    System.out.println("[WritePersonalData]: IOException\n\t"+e); 
	    pout.close();
	    return 0;
	} catch (NumberFormatException n) {
	    System.out.println("[WritePersonalData]: NumberFormatException\n\t"+n); 
	    pout.close();
	    return 0;
	}
	return flag;    
    }
    
    // Verifies if the monitor is authentic
    // Checks the monitor's given checksum vs the sha-1 digest of our pass
    // If they are equal it passes
    public boolean verifyChecksum(String file, String testChecksum) throws NoSuchAlgorithmException, IOException
    {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");

	String pass = PASSWORD.toUpperCase();
	sha1.update(pass.getBytes());
	
	BigInteger big = new BigInteger(1, sha1.digest());
         
        String passHash = big.toString(16);
	System.out.println("Pass hash: " + passHash);
         
        return passHash.equals(testChecksum);
    }

    public boolean IsTradePossible(String TradeMesg) {
	return false;
    }

    public int GetResource(String choice) throws IOException {
	return 0;
    }

    public void HandleWarResponse(String cmd) throws IOException{
    }

    public void DoTrade(String cmd)  throws IOException{
    }

    public void DoWar(String cmd)  throws IOException{
    }
    
}

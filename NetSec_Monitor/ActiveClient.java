// Collin Atkins, Brandon Watson, Kyle Koch, Juanita Dickhaus
// Net Sec, Prof. Franco
// Cool Cats - Monitor Project

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.awt.*;

public class ActiveClient extends MessageParser implements Runnable {

    public static String MonitorName;
    Thread runner;
    Socket toMonitor = null;
    public static int MONITOR_PORT;
    public static int LOCAL_PORT;
    public int SleepMode;
    //Interval after which a new Active Client is started 
    int DELAY = 90000;
    long prevTime,present;

    public ActiveClient() {
	super("[no-name]", "[no-password]");
	MonitorName="";
	toMonitor = null;
	MONITOR_PORT=0;
	LOCAL_PORT=0;
    }
             
    public ActiveClient(String mname, int p, int lp, int sm, 
			String name, String password) {
	super(name, password);
	try {
	    SleepMode = sm;
	    MonitorName = mname; 
	    MONITOR_PORT = p; 
	    LOCAL_PORT = lp;
	} catch (NullPointerException n) {
	    System.out.println("Active Client [Constructor]: TIMEOUT Error: "+n);
	}
    }

    public void start() {
	if (runner == null) {
	    runner = new Thread(this);
	    runner.start();
	}
    }  

    public void run() {
	while(Thread.currentThread() == runner) {
	    try {                         
		System.out.print("\nActive Client: trying monitor: "+MonitorName+
				 " port: "+MONITOR_PORT+"...");
		toMonitor = new Socket(MonitorName, MONITOR_PORT);
		System.out.println("completed.");
	    
		out = new PrintWriter(toMonitor.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(toMonitor.getInputStream()));
		
		HOSTNAME = toMonitor.getLocalAddress().getHostName();

		// Indicates that this is the client
		CType = 0;
		HOST_PORT = LOCAL_PORT;

		System.out.println("\nStarting login from Client...\n");
		if(Login()) {  
		    System.out.println("\nClient log in successful!\n");
		} else {
		    System.out.println("Client failed log in. Exiting.");
		    System.exit(1);
		}

		String temp = PASSWORD;
		
		// After login is successful it first changes the password
		ChangePassword();
		String msg = GetMonitorMessage(true);
		System.out.println("[CHANGE PASS]: Monitor:\n\t" + msg);
		
		// Covers special case of host port timing out
		if(msg.substring(0,7).equals("COMMENT")) {
		    PASSWORD = temp;
		    msg = GetMonitorMessage(true);
		    System.out.println("[HOST_PORT FIX] Monitor:\n\t" + msg);
		    msg = GetMonitorMessage(true);
		    System.out.println("[HOST_PORT FIX] Monitor:\n\t" + msg);
		    Execute("HOST_PORT");
		    msg = GetMonitorMessage(true);
		    System.out.println("[HOST_PORT FIX] Monitor:\n\t" + msg);
		    
		    // Redo change password
		    ChangePassword();
		    msg = GetMonitorMessage(true);
		    System.out.println("[PASSWORD REDO] Monitor:\n\t" + msg);
		}
		// Writes pass and cookie to ident file
		UpdatePass(PASSWORD);
		UpdateCookie("CHANGE_PASSWORD", msg);

		//Wait for user to input command
		String command  = "";
		Scanner scanner = new Scanner(System.in);
		while(!command.equals("Quit")){
		    System.out.println("Enter next Command");
		    command = scanner.nextLine();
		    SendIt(command);
		    String monmsg = GetMonitorMessage(true);
		    while(monmsg.isEmpty()){
			//Wait for response
		    }
		    System.out.println(monmsg);
		}

		scanner.close();
		System.out.println("I Quit");
		
		toMonitor.close(); 
		out.close(); 
		in.close();
		try { runner.sleep(DELAY); } catch (Exception e) {}
                            
	    } catch (UnknownHostException e) {
	    } catch (IOException e) {
		try { 
		    toMonitor.close();
		} catch (IOException ioe) {
		} catch (NullPointerException n) { 
		    try {
			toMonitor.close();
		    } catch (IOException ioe) {}
		}
	    }
	}
    }
}

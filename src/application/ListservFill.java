package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

import application.ListservFillWorker.Action;

/**
 * This class is meant to allow the technology officer to sign up
 * or remove a large number of people to/from the afrotc listserv.
 * 
 * @author <pre>Travis Carson, C/Capt, AFROTC</pre>
 * <pre>Technology Officer, Detachment 330</pre>
 * <pre>February 2017</pre>
 * 
 */

// TODO: if you feel adept at concurrency, consider using an ExecutorService with
// this program instead of threads. This should significantly speed up the program.
public class ListservFill{
	private static final int NUMTHREADS = Runtime.getRuntime().availableProcessors();
	private static ListservFillWorker[] workers;
	
	// Website URL
	private final static String Det330ListservURL = 
			"https://listserv.umd.edu/cgi-bin/wa?SUBED1=det330cadetwing&A=1";
	
	
	/**
	 * Adds the given emails in the file
	 * to the Listserv mailing list.
	 * 
	 * @param file
	 */
	protected static void joinAll(String text){
		workers = new ListservFillWorker[NUMTHREADS];	
		LinkedList<String> emails = getEmails(text);
		for (int i=0; i<workers.length; i++) {
			workers[i] = new ListservFillWorker(emails,  Action.JOIN);
			workers[i].start();
		}
		
		// Close the threads.
		for (int i=0; i<workers.length; i++)
			try {
				workers[i].join();
			} catch (InterruptedException e) {
				print("Consider sending all of the requests again.\nError: " + e);
				return;
			}
		
		print("Completed!");
	}
	
	
	/**
	 * Removes the given emails in the file
	 * from the Listserv mailing list.
	 * 
	 * @param file
	 */
	protected static void leaveAll(String text){		
		workers = new ListservFillWorker[NUMTHREADS];	
		LinkedList<String> emails = getEmails(text);
		for (int i=0; i<workers.length; i++) {
			workers[i] = new ListservFillWorker(emails,  Action.LEAVE);
			workers[i].start();
		}
		
		// Close the threads.
		for (int i=0; i<workers.length; i++)
			try {
				workers[i].join();
			} catch (InterruptedException e) {
				print("Consider sending all of the requests again.\nError: " + e);
				return;
			}
		
		print("Completed!");
	}
	
	
	/**
	 *  Adds the given email to the Listserv mailing list.
	 *  
	 *  @param email
	 *  
	 *  @return Success = true, Failure = false
	 */
	protected static boolean join(String email){
		return joinOrLeave(email, Action.JOIN);
	}
	
	
	/**
	 *  Removes the given email from the Listserv mailing list.
	 *  
	 *  @param email
	 *  
	 *  @return Success = true, Failure = false
	 */
	protected static boolean leave(String email){
		return joinOrLeave(email, Action.LEAVE);
	}
	
	
	/**
	 *  DO NOT USE THIS - THIS IS FOR join() AND leave() METHODS.
	 *  
	 *  <pre>
	 *  Adds the given email or takes the given email off of the Listserv mailing list.
	 *  </pre
	 *  
	 *  @param email
	 *  @param action (Join or Leave)
	 *  
	 *  @return Success = true, Failure = false
	 */
	private static boolean joinOrLeave(String email, Action action){
		// Set the required parameters that were retrieved from the web page.
		String param1 = "SUBED2=DET330CADETWING";
		String param2 = "A=1";
		String choice = null;
		
		// Determines whether the user wants to delete or add the email to the listserv.
		switch(action){
			case JOIN: choice = "b=Join"; break;
			case LEAVE: choice = "a=Leave"; break;
		}
		
		try {
			// Open URL connection
			URL url = new URL(Det330ListservURL);
			URLConnection connect = url.openConnection();
			connect.setDoOutput(true);		
			
			// Add the correct web page parameters to a string send it in
			OutputStream output = connect.getOutputStream();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(output));
			writer.print(param1 + "&" + param2 + "&s=" + email + "&" + choice + "+DET330CADETWING");
			writer.flush();
			
			// Sends data
			InputStream input = connect.getInputStream();
		} catch (MalformedURLException e) {
			print("There was an error with the URL.  Make sure the Listserv URL hasn't changed.  "
					+ "If it has, you will need to change it in the code.");
			return false;
		} catch (IOException e) {
			print("There was an error with opening the connection.  Check your internet connection.");
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * Parses the given text and extracts emails that are separated by anything but
	 * alphanumeric characters, adds the emails to a LinkedList, 
	 * then returns the List. 
	 * 
	 * @param text
	 * @return LinkedList of emails
	 */
	private static LinkedList<String> getEmails(String text) {
		LinkedList<String> emails = new LinkedList<String>();
		String[] emailList;   
        
        // Regular Expression containing anything but A-Z, a-z, 0-9, @, or .
        String cvsSplitBy = "[^A-Za-z0-9@.]";
            	
        // use comma, new line, and space as separator
        emailList = text.split(cvsSplitBy);
        
        //
        for (String email : emailList){
        	emails.add(email);
        }
             
        return emails;
	}
	
	
	/**
	 * Prints the line with a new line space.
	 * i.e. System.out.println()
	 * 
	 * @param line 
	 */
	private static void print(String line){
		ListservGUI.sendMessage(line);
	}
}

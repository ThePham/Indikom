package slackClient;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

import gui.ConnectionWindow;
import gui.GuiLauncher;
import model.FileEvent;
import model.Resource;
import net.jeremybrooks.knicker.AccountApi;
import net.jeremybrooks.knicker.KnickerException;
import net.jeremybrooks.knicker.dto.TokenStatus;


public class App 
{
	static String botAccessToken;
	static String rabbitLocation;
	
	static String GHToken;
	static String GHUsername;
	
	static boolean useWordnik = false;
	
    public static void main( String[] args )
    {
        
    	//ziskanie bot access tokenu zo suboru connection.txt v priecinku /users/user/.indikom
    	File file = new File(getUserDataDirectory() + "connection.txt");
		if (file.exists() == false){
			ConnectionWindow conWin = new ConnectionWindow();
			conWin.setVisible(true);
		}
		else {
			FileReader connectionFileReader;
	    	
			try {
				connectionFileReader = new FileReader(file);
				BufferedReader br = new BufferedReader(connectionFileReader);
				
				botAccessToken = br.readLine();
				rabbitLocation = br.readLine();
				GHUsername = br.readLine();
				GHToken = br.readLine();
				
				//Wordnik 
		    	//System.setProperty("WORDNIK_API_KEY" , "95ce83573b741a57740080102280a90a94fc04e08f9f8abb4");
				String wordnikApi = br.readLine();
				if (wordnikApi.length() > 2) {
					System.setProperty("WORDNIK_API_KEY" , wordnikApi);
					useWordnik = true;
				}
				else 
					useWordnik = false;
		    	
		    	/*
		    	if (System.getProperty("WORDNIK_API_KEY").isEmpty()) {
		    		System.setProperty("WORDNIK_API_KEY" , "95ce83573b741a57740080102280a90a94fc04e08f9f8abb4");
		    	}
		    	*/

			} catch (FileNotFoundException e1) {
				System.out.println("The file with bot access token not found, exiting.");
				System.exit(1);
			} catch (IOException e) {
				System.out.println("Error while reading bot access token from file connection.txt, exiting.");
				System.exit(1);
			}
	    	
	    	//ActivityTracker.textToSpeech("This is a test message.");
	    	//ActivityTracker.textToSpeech("This is so sad, alexa play despacito.");	
			
			Timer time = new Timer(); 
			ActivityTrackingTask trackingTask = new ActivityTrackingTask(); 
			time.schedule(trackingTask, 0, 600*1000); 
			
	    	if (useWordnik) {
		    	// check the status of the API key
		    	TokenStatus status = null;
				try {
					status = AccountApi.apiTokenStatus();
				} catch (KnickerException e) {
					e.printStackTrace();
				}
		    	if (status.isValid()) {
		    	    System.out.println("API key is valid.");
		    	} else {
		    	    System.out.println("API key is invalid!");
		    	    useWordnik = false;
		    	}
	    	}
	    	else 
	    		System.out.println("Not using Wordnik thesaurus");
	    	
	        EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						Connection connection = new Connection();
						GuiLauncher window = new GuiLauncher(connection, botAccessToken);						
						connection.addObserver(window);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
	        
	        MessageProcessing mpr = new MessageProcessing();
	        
		}
    	
    	
    }
    
    public static String getUserDataDirectory() {
	    return System.getProperty("user.home") + File.separator + ".indikom" + File.separator;
	}
    
    public static String getRabbitLocation() {
	    return rabbitLocation;
	}
    
    public static String getGHUsername() {
	    return GHUsername;
	}
    
    public static String getGHToken() {
	    return GHToken;
	}
    
    public static boolean getUseWordnik() {
	    return useWordnik;
	}
}

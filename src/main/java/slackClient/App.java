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
	
    public static void main( String[] args )
    {
        
    	//Get bot access token from /users/user/.indikom
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
			} catch (FileNotFoundException e1) {
				System.out.println("The file with bot access token not found, exiting.");
				System.exit(1);
			} catch (IOException e) {
				System.out.println("Error while reading bot access token from file connection.txt, exiting.");
				System.exit(1);
			}

			/* Activity Tracking WIP
			Timer time = new Timer(); 
			ActivityTrackingTask trackingTask = new ActivityTrackingTask(); 
			time.schedule(trackingTask, 0, 1000000000); 
			*/
			
			/*
			//Wordnik 
	    	System.setProperty("WORDNIK_API_KEY" , "API_KEY");
	    	
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
	    	}
	    	*/
			
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
}

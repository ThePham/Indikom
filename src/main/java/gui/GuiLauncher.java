package gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JMenu;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

import model.FileEvent;
import slackClient.ActivityTracker;
import slackClient.Connection;
import slackClient.MSTranslator;
import slackClient.MessageObject;
import slackClient.MessageProcessing;
import slackClient.Translator;

import javax.swing.JTextArea;

import slackClient.Connection;
import javax.swing.JMenuItem;

public class GuiLauncher implements Observer{

	private JFrame frmIndikom;
	private JComboBox comboBox;
	private JTextArea textArea;
	Connection con;
	private int receivedMessages;	
	
	/**
	 * Create the application.
	 */
	public GuiLauncher(Connection connection, String botAccessToken) {
		initialize(botAccessToken);
		con = connection;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(String botAccessToken) {
		frmIndikom = new JFrame();
		frmIndikom.setTitle("IndiKom");
		frmIndikom.setBounds(100, 100, 450, 300);
		frmIndikom.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		receivedMessages = 0;
		
		final SlackSession session = SlackSessionFactory.createWebSocketSlackSession(botAccessToken);
        try {
			session.connect();
		} catch (IOException e) {
			System.out.println("Unable to establish connection, exiting.");
			ConnectionWindow conWin = new ConnectionWindow();
			conWin.setVisible(true);
		}
		
		JButton btnOpenCommunication = new JButton("Open communication");
		
		final JComboBox comboBox = new JComboBox();
		
		Collection<SlackChannel> channels = session.getChannels();
		Iterator itr = channels.iterator();
		Vector<String> channelNames = null;
		while (itr.hasNext()){
			SlackChannel channel = (SlackChannel) itr.next();	
			if (channel.getName() != null){
				comboBox.addItem((String) channel.getName());
			}
		}
			
		JScrollPane scrollPane = new JScrollPane();
		
		
		GroupLayout groupLayout = new GroupLayout(frmIndikom.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(comboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnOpenCommunication, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap(303, Short.MAX_VALUE))
				.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnOpenCommunication))
		);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		frmIndikom.getContentPane().setLayout(groupLayout);
		
		btnOpenCommunication.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
							
				String channelName = (String) comboBox.getSelectedItem();
				SlackChannel channel = session.findChannelByName(channelName);

				/*
				List<SlackMessagePosted> messages = con.fetchMessagesFromChannelHistory(session, channel, 20);
				
				StringBuilder messagesStr = new StringBuilder();
				for (int i = messages.size()-1 ; i >= 0 ; i--){
					messages.get(i).getMessageContent();
					messages.get(i).getSender();
					
					String[] timeInMilisArray = splitStringByDot(messages.get(i).getTimestamp());
					String timeInMilis = timeInMilisArray[0];
					System.out.println("cas prijatia spravy: " + millisToDate(timeInMilis));
					
					messagesStr.append(messages.get(i).getSender().getUserName() + " [" + millisToDate3(timeInMilis) + "]" + ":\n");
					messagesStr.append("> " + messages.get(i).getMessageContent() + "\n");
					
					textArea.setText(messagesStr.toString());
				}
				*/
				con.slackMessagePostedEventContent(session, channelName);
			}
			
		});
		
		JMenuBar menuBar = new JMenuBar();
		frmIndikom.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("General");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmConfiguration = new JMenuItem("Configuration");
		mnNewMenu.add(mntmConfiguration);
		
		JMenuItem mntmConnectionSettings = new JMenuItem("Connection settings");
		mnNewMenu.add(mntmConnectionSettings);
		
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mnNewMenu.add(mntmQuit);
		frmIndikom.setVisible(true);
		
		mntmConfiguration.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Configuration configWindow = new Configuration();
				configWindow.setVisible(true);
				
			}
			
		});
		
		mntmConnectionSettings.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				ConnectionWindow conWin = new ConnectionWindow();
				conWin.setVisible(true);
				
			}
			
		});
		
		mntmQuit.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				frmIndikom.dispose();
				System.exit(0);
			}
			
		});
	}

	@Override
	public void update(Observable o, Object arg) {
		SlackMessagePosted incomingMessage = (SlackMessagePosted) arg;
		String[] timeInMilisArray = splitStringByDot(incomingMessage.getTimestamp());
		String timeInMilis = timeInMilisArray[0];
		
		
		//Message filtering
		ArrayList<FileEvent> files = ActivityTracker.getFiles();
		ArrayList<String> keywords = new ArrayList<String>();
		
		for (FileEvent f : files) {
			for (String s : f.getKeywords())
				keywords.add(s);
		}
		
		
		try {
			//System.out.println(slackClient.Translator.callUrlAndParseResult("sk", "en", incomingMessage.getMessageContent()));
			System.out.println(MSTranslator.callTranslate(incomingMessage.getMessageContent()));
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		
		textArea.setText(textArea.getText() + "> SK: " + incomingMessage.getSender().getUserName() + ": " + incomingMessage.getMessageContent() + "\n");
		textArea.setText(textArea.getText() + "> EN: " + incomingMessage.getSender().getUserName() + ": " + MSTranslator.callTranslate(incomingMessage.getMessageContent()) + "\n");
		
		for (String s : keywords) {
			try {
				if (incomingMessage.getMessageContent().toLowerCase().contains(s.toLowerCase()) || 
						MSTranslator.callTranslate(incomingMessage.getMessageContent()).toLowerCase().contains(s.toLowerCase()) ) {
				//if (incomingMessage.getMessageContent().toLowerCase().contains(s.toLowerCase()) ) {
					/*
					try {
						//textArea.setText(textArea.getText() + incomingMessage.getSender().getUserName() + " [" + millisToDate3(timeInMilis) + "]" + ":\n" + "> EN: " + slackClient.Translator.callUrlAndParseResult("sk", "en", incomingMessage.getMessageContent()) + "\n");
						textArea.setText(textArea.getText() + "> SK: " + incomingMessage.getMessageContent() + "\n");
					} catch (Exception e1) {
						e1.printStackTrace();
					}	
					*/

					/*
					// TEXT TO SPEECH
					try {
						slackClient.ActivityTracker.textToSpeech(incomingMessage.getSender().getUserName() + " said " + slackClient.Translator.callUrlAndParseResult("sk", "en", incomingMessage.getMessageContent()));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					*/
					
					receivedMessages++;
								
					MessageProcessing mpr = new MessageProcessing();
					MessageObject msg = new MessageObject();
					msg = mpr.getFinalMessage(incomingMessage.getMessageContent(), receivedMessages);		
					
					if (true){
						JFrame AutoMessageFrame = new JFrame();
						AutoMessageFrame.setUndecorated(true);
						AutoMessageFrame.getContentPane().add(new AutoMessage(incomingMessage.getMessageContent(), msg, incomingMessage.getSender().getUserName()));
						AutoMessageFrame.setBounds(100, 100, 450, 300);
						AutoMessageFrame.setOpacity(0.6f);
						AutoMessageFrame.setLocationRelativeTo(null);
						AutoMessageFrame.setAlwaysOnTop(true);
						
						Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
						int x = (int) ((dimension.getWidth() - AutoMessageFrame.getWidth()) - (AutoMessageFrame.getWidth()/4));
					    int y = (int) ((dimension.getHeight() - AutoMessageFrame.getHeight()) - (AutoMessageFrame.getHeight()/4));
						AutoMessageFrame.setLocation(x, y);
						AutoMessageFrame.setVisible(true);
						
						
						try {
							TimeUnit.SECONDS.sleep(6);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						AutoMessageFrame.dispose();
						
						
					}
					break;
				}
			} catch (HeadlessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		saveMessageInfo(incomingMessage);
				
	}
	
	/**
	 * metoda ulozi cas prijatia spravy a meno jej odosielatela do textoveho suboru received_messages.txt
	 * @param incomingMessage
	 */
	public void saveMessageInfo(SlackMessagePosted incomingMessage){
		FileWriter saveWriter = null;
		
		try {
			saveWriter = new FileWriter(getUserDataDirectory() + "received_messages.txt", true);
			System.out.println("ZAPISUJEM");
			saveWriter.write(incomingMessage.getTimestamp() + " - " + incomingMessage.getSender().getUserName() + "\n");
			
			
		} catch (IOException e) {
			System.out.println("Error writing info about received message");
			//e.printStackTrace();
		} finally {
			try {
				saveWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String[] splitStringByDot(String string){
		String[] words = string.split("\\.");
		return words;
	}
	
	public String millisToDate(String timeInMillis){
		Long timeStamp = Long.parseLong(timeInMillis);
		Date date = new Date(timeStamp);
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String dateFormatted = formatter.format(date);
		
		return dateFormatted;
	}
	
	public String millisToDate2(String timeInMillis){
		final Calendar cal = Calendar.getInstance();
		Long timeStamp = Long.parseLong(timeInMillis);
		cal.setTimeInMillis(timeStamp);
		final String timeString = new SimpleDateFormat("dd.MM. HH:mm:ss").format(cal.getTime());
		
		return timeString;
	}
	
	public String millisToDate3(String timeInMillis){
		Long timeStamp = Long.parseLong(timeInMillis);
		String date = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new java.util.Date (timeStamp*1000));
		
		return date;
	}
	
	public String millisToTime(String timeInMillis){
		String timeString = null;
		Long timeStamp = Long.parseLong(timeInMillis);
		
		long second = (timeStamp / 1000) % 60;
		long minute = (timeStamp / (1000 * 60)) % 60;
		long hour = (timeStamp / (1000 * 60 * 60)) % 24;
		
		timeString = String.format("%02d:%02d:%02d", hour, minute, second);
		
		return timeString;		
	}
	
	public static String getUserDataDirectory() {
	    return System.getProperty("user.home") + File.separator + ".indikom" + File.separator;
	}
}

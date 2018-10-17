package gui;

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextArea;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;

import slackClient.MessageObject;

public class AutoMessage extends JPanel {

	/**
	 * Create the panel.
	 */
	public AutoMessage(final String wholeMessage, final MessageObject finalMessage, final String sender) {
		setBackground(new Color(30, 144, 255));
		
		String messageToBeShown = finalMessage.getMessage();
		
		final JTextArea textArea = new JTextArea();
		textArea.setForeground(new Color(255, 255, 255));
		textArea.setFont(new Font("Calibri", Font.BOLD, 21));
		//textArea.setText(messageToBeShown);
		textArea.setText(sender + ": " + wholeMessage);
		textArea.setBackground(new Color(30, 144, 255));
		textArea.setOpaque(true);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(textArea, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(textArea, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
					.addContainerGap())
		);
		setLayout(groupLayout);
		
		textArea.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
               System.out.println("pouzivatel klikol na spravu"); 
               textArea.setText(wholeMessage);
             
               FileReader saveRead;
               FileWriter saveWriter = null;
               try {
            	               	   
            	   saveWriter = new FileWriter(getUserDataDirectory() + "click_info.txt", true);
            	   
            	   long timeStamp = System.currentTimeMillis() / 1000L;
            	   saveWriter.write(Long.toString(timeStamp) + "\n");
            	   if (finalMessage.getFirst_m_words() == true){
            		   saveWriter.write("first m words\n");
            	   }            	   
            	   if (finalMessage.getFirst_m_words_key_word() == true){
            		   saveWriter.write("first m words key word\n");
            	   }
            	   if (finalMessage.getWhole_msg() == true){
            		   saveWriter.write("whole msg\n");
            	   }
            	   if (finalMessage.getN_th_msg() == true){
            		   saveWriter.write("nth message\n");
            	   }
            	   if (finalMessage.getNot_nth_msg() == true){
            		   saveWriter.write("not nth message\n");
            	   }
            	   if (finalMessage.getWork_context() == true){
            		   saveWriter.write("work context\n");
            	   }
            	   if (finalMessage.getKey_word() == true){
            		   saveWriter.write("key word\n");
            	   }
            	   if (finalMessage.getSentence_type() == true){
            		   saveWriter.write("sentence type\n");
            		   
            	   }
            	   saveWriter.write("//----------\n");
            	   
            	               	   
            	   
               } catch (FileNotFoundException e1) {
            	   //e1.printStackTrace();
            	   System.out.println("Error while writing into click_info.txt");
               } catch (IOException e1) {
				e1.printStackTrace();
               } finally {
            	   try {
					saveWriter.close();
				} catch (IOException e1) {
					System.out.println("Error closing file writer.");
					e1.printStackTrace();
				}
               }
   				
            }
        });
		
	}
	
	public static String getUserDataDirectory() {
	    return System.getProperty("user.home") + File.separator + ".indikom" + File.separator;
	}
}

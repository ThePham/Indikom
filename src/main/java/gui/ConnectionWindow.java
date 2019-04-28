package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;

public class ConnectionWindow extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textFieldRabbit;
	private JTextField textFieldGHUsername;
	private JTextField textFieldGHToken;
	private JTextField textFieldWordnik;
	private JTextField textFieldSlackName;

	
	/**
	 * Create the frame.
	 */
	
	public ConnectionWindow() {
		setResizable(false);
		setTitle("Connection settings");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		textField = new JTextField();
		textField.setColumns(10);
		
		textFieldRabbit = new JTextField();
		textFieldRabbit.setColumns(10);
		
		textFieldGHUsername = new JTextField();
		textFieldGHUsername.setColumns(10);
		
		textFieldGHToken = new JTextField();
		textFieldGHToken.setColumns(10);
		
		textFieldWordnik = new JTextField();
		textFieldWordnik.setColumns(10);
		
		textFieldSlackName = new JTextField();
		textFieldSlackName.setColumns(10);
			
		JLabel lblToken = new JLabel("Token:");
		
		JLabel lblTokenRabbit = new JLabel("User activity location (e.g. /Users/phamv/Rabbit/C.Users.phamv.workspace/):");
		
		JLabel lblgithubUser = new JLabel("Github username:");
		JLabel lblgithubToken = new JLabel("Github oauth token:");
		
		JLabel lblWordnik= new JLabel("Wordnik API key (leave empty to not use Wordnik thesaurus):");
		
		JLabel lblSlackName = new JLabel("Slack username:");
		
		JButton btnOk = new JButton("OK");
		
		JButton btnCancel = new JButton("Cancel");
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addContainerGap(274, Short.MAX_VALUE)
					.addComponent(btnCancel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnOk, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblToken)
					.addContainerGap())
				.addComponent(textField, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addComponent(lblTokenRabbit)
						.addContainerGap())
				.addComponent(textFieldRabbit, GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addComponent(lblgithubUser)
						.addContainerGap())
				.addComponent(textFieldGHUsername, GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addComponent(lblgithubToken)
						.addContainerGap())
				.addComponent(textFieldGHToken, GroupLayout.DEFAULT_SIZE, 724, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addComponent(lblWordnik)
						.addContainerGap())
				.addComponent(textFieldWordnik, GroupLayout.DEFAULT_SIZE, 824, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addComponent(lblSlackName)
						.addContainerGap())
				.addComponent(textFieldSlackName, GroupLayout.DEFAULT_SIZE, 924, Short.MAX_VALUE)
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblToken)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(lblTokenRabbit)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textFieldRabbit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(lblgithubUser)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textFieldGHUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(lblgithubToken)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textFieldGHToken, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(lblWordnik)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textFieldWordnik, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(lblSlackName)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textFieldSlackName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 189, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnOk)
						.addComponent(btnCancel)))
		);
		contentPane.setLayout(gl_contentPane);
		
		try{
			
			File file = new File(getUserDataDirectory() + "connection.txt");
			if (file.exists() == true){
				FileReader connectionFileReader = new FileReader(file);
				BufferedReader br = new BufferedReader(connectionFileReader);
				String line = br.readLine();
				textField.setText(line);
				
				line = br.readLine();
				textFieldRabbit.setText(line);
				
				line = br.readLine();
				textFieldGHUsername.setText(line);
				
				line = br.readLine();
				textFieldGHToken.setText(line);
				
				line = br.readLine();
				textFieldWordnik.setText(line);
				
				line = br.readLine();
				textFieldSlackName.setText(line);
			}
		} catch (Exception ex) {
			
		}
		
		btnOk.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				File file = new File(getUserDataDirectory() + "connection.txt");
				if (file.exists() == false){
					file.getParentFile().mkdirs();
				}
				FileWriter fw = null;				
				
				if (textField.getText().equals("")){
					JOptionPane.showMessageDialog(null, "Please, enter correct connection token.", "Warning", JOptionPane.WARNING_MESSAGE);
					dispose();
				}
				else{
					try {
						fw = new FileWriter(file);
						fw.write(textField.getText() + System.lineSeparator());
						fw.write(textFieldRabbit.getText() + System.lineSeparator());
						fw.write(textFieldGHUsername.getText() + System.lineSeparator());
						fw.write(textFieldGHToken.getText() + System.lineSeparator());
						fw.write(textFieldWordnik.getText() + System.lineSeparator());
						fw.write(textFieldSlackName.getText() + System.lineSeparator());
						
					} catch (IOException e1) {
						System.out.println("Unable to write connection token.");
						System.exit(2);
						//e1.printStackTrace();
					} finally{
						try {
							fw.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					JOptionPane.showMessageDialog(null, "Connection token successfully saved. Please, restart the application.");
					System.exit(0);
				}
				
				
			}
			
		});
		
		btnCancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);				
			}
			
		});
	}
	
	public static String getUserDataDirectory() {
	    return System.getProperty("user.home") + File.separator + ".indikom" + File.separator;
	}
}

package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.Font;

public class Configuration extends JFrame {

	private JPanel contentPane;
	private JTextField n_value;
	private JTextField m_value;

	
	
	/**
	 * Create the frame.
	 */
	public Configuration() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 601);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JScrollPane scrollPane1 = new JScrollPane();
		JScrollPane scrollPane2 = new JScrollPane();
		
		ButtonGroup periodicityButtonsGroup = new ButtonGroup();
		
		JLabel lblMessageAppearance = new JLabel("Message appearance:");
		
		JLabel lblPartOfThe = new JLabel("Part of the message:");
		
		final JCheckBox chckbxFirstMWords = new JCheckBox("First m words");
		
		final JCheckBox chckbxFirstMWords_1 = new JCheckBox("First m words after the keyword");
		
		JLabel lblMessageSelection = new JLabel("Message selection:");
		
		JLabel lblPeriodicalSelection = new JLabel("Periodical selection:");
		
		final JRadioButton rdbtnEveryNthMessage = new JRadioButton("Show every n-th message");
		
		final JRadioButton rdbtnDontShowEvery = new JRadioButton("Don`t show every n-th message");
		
		final JRadioButton rdbtnOff = new JRadioButton("Off");
		
		periodicityButtonsGroup.add(rdbtnEveryNthMessage);
		periodicityButtonsGroup.add(rdbtnDontShowEvery);
		periodicityButtonsGroup.add(rdbtnOff);
		
		JLabel lblNValue = new JLabel("n value:");
		
		n_value = new JTextField();
		n_value.setColumns(10);
		
		JLabel lblMValue = new JLabel("m value:");
		
		m_value = new JTextField();
		m_value.setColumns(10);
		
		JLabel lblSemantics = new JLabel("Semantics:");
		
		final JCheckBox chckbxWorkContext = new JCheckBox("Work context");
		
		final JTextArea textAreaWorkContext = new JTextArea();
		
		textAreaWorkContext.setFont(new Font("Monospaced", Font.PLAIN, 11));
		textAreaWorkContext.setLineWrap(true);
		textAreaWorkContext.setWrapStyleWord(true);
		
		final JCheckBox chckbxKeyWords = new JCheckBox("Key words");
		
		final JTextArea textAreaKeyWords = new JTextArea();
		textAreaKeyWords.setFont(new Font("Monospaced", Font.PLAIN, 11));
		textAreaKeyWords.setLineWrap(true);
		textAreaKeyWords.setWrapStyleWord(true);
		
		JLabel lblSyntax = new JLabel("Syntax:");
		
		final JCheckBox chckbxSentenceType = new JCheckBox("Sentence type");
		
		JLabel lblMessageAppearance_1 = new JLabel("Message appearance:");
		
		final JCheckBox chckbxColorHighlight = new JCheckBox("Color highlight");
		
		JButton btnOk = new JButton("OK");
		
		JButton btnCancel = new JButton("Cancel");
		
		try {
			FileReader saveRead = new FileReader(getUserDataDirectory() + "configsave.txt");
			System.out.println("subor existuje");
			BufferedReader br = new BufferedReader(saveRead);
			
			String line2;
			String line = br.readLine();
			while(line != null){
				if (line.equals("m_words true")){
					chckbxFirstMWords.setSelected(true);
					line2 = br.readLine();
					m_value.setText(line2);
				}
				if (line.equals("m_words_key true")){
					chckbxFirstMWords_1.setSelected(true);
					line2 = br.readLine();
					m_value.setText(line2);
				}
				if (line.equals("n_th_message true")){
					rdbtnEveryNthMessage.setSelected(true);
					line2 = br.readLine();
					n_value.setText(line2);
				}
				if (line.equals("n_th_message_not true")){
					rdbtnDontShowEvery.setSelected(true);
					line2 = br.readLine();
					n_value.setText(line2);
				}
				if (line.equals("work_context true")){
					chckbxWorkContext.setSelected(true);
					line2 = br.readLine();
					textAreaWorkContext.setText(line2);
				}
				if (line.equals("key_words true")){
					chckbxKeyWords.setSelected(true);
					line2 = br.readLine();
					textAreaKeyWords.setText(line2);
				}
				if (line.equals("sentence_type true")){
					chckbxSentenceType.setSelected(true);
				}
				if (line.equals("color_highlight true")){
					chckbxColorHighlight.setSelected(true);
				}
				
				if (line.equals("n_th_message false")){
					String lineNext = br.readLine();
					lineNext = br.readLine();
					if (lineNext.equals("n_th_message_not false")){
						rdbtnOff.setSelected(true);
					}
					
				}
				
				line = br.readLine();
			}
			saveRead.close();
			
			
		} catch (FileNotFoundException e2) {
			//e2.printStackTrace();
			System.out.println("subor neexistuje");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		btnCancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();				
			}
			
		});
		
		btnOk.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(getUserDataDirectory());
				
				try {
					File file = new File(getUserDataDirectory() + "configsave.txt");
					if (file.exists() == false){
						file.getParentFile().mkdirs();
					}
					
					PrintWriter saveWriter = new PrintWriter(getUserDataDirectory() + "configsave.txt");
					
					//m words:
					if (chckbxFirstMWords.isSelected()){
						saveWriter.println("m_words true");
						saveWriter.println(m_value.getText());
					}
					else{
						saveWriter.println("m_words false");
						saveWriter.println("0");
					}
					//m words after keyword:
					if (chckbxFirstMWords_1.isSelected()){
						saveWriter.println("m_words_key true");
						saveWriter.println(m_value.getText());
					}
					else{
						saveWriter.println("m_words_key false");
						saveWriter.println("0");
					}
					//every n-th message
					if (rdbtnEveryNthMessage.isSelected()){
						saveWriter.println("n_th_message true");
						saveWriter.println(n_value.getText());
					}
					else{
						saveWriter.println("n_th_message false");
						saveWriter.println("0");
					}
					//every n-th message does not show
					if (rdbtnDontShowEvery.isSelected()){
						saveWriter.println("n_th_message_not true");
						saveWriter.println(n_value.getText());
					}
					else{
						saveWriter.println("n_th_message_not false");
						saveWriter.println("0");
					}
					//work context
					if (chckbxWorkContext.isSelected()){
						saveWriter.println("work_context true");
						saveWriter.println(textAreaWorkContext.getText());
					}
					else{
						saveWriter.println("work_context false");
						saveWriter.println("**null**");
					}
					//key words
					if (chckbxKeyWords.isSelected()){
						saveWriter.println("key_words true");
						saveWriter.println(textAreaKeyWords.getText());
					}
					else{
						saveWriter.println("key_words false");
						saveWriter.println("**null**");
					}
					//sentence type
					if (chckbxSentenceType.isSelected()){
						saveWriter.println("sentence_type true");
					}
					else{
						saveWriter.println("sentence_type false");
					}
					//color highlight
					if(chckbxColorHighlight.isSelected()){
						saveWriter.println("color_highlight true");
					}
					else{
						saveWriter.println("color_highlight false");
					}
					
					saveWriter.close();
					dispose();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				
			}
			
		});
		
		
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
							.addComponent(lblMessageAppearance)
							.addGroup(gl_contentPane.createSequentialGroup()
								.addContainerGap()
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
									.addComponent(lblPartOfThe)
									.addGroup(gl_contentPane.createSequentialGroup()
										.addGap(10)
										.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
											.addComponent(chckbxFirstMWords_1)
											.addComponent(chckbxFirstMWords)))))
							.addComponent(lblMessageSelection)
							.addGroup(gl_contentPane.createSequentialGroup()
								.addContainerGap()
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
									.addComponent(lblPeriodicalSelection)
									.addGroup(gl_contentPane.createSequentialGroup()
										.addGap(10)
										.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
											.addGroup(gl_contentPane.createSequentialGroup()
												.addComponent(rdbtnDontShowEvery)
												.addGap(18)
												.addComponent(rdbtnOff))
											.addGroup(gl_contentPane.createSequentialGroup()
												.addComponent(rdbtnEveryNthMessage)
												.addGap(48)
												.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
													.addComponent(lblNValue)
													.addComponent(lblMValue))
												.addPreferredGap(ComponentPlacement.RELATED)
												.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
													.addComponent(m_value, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(n_value, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))))))
							.addGroup(gl_contentPane.createSequentialGroup()
								.addContainerGap()
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
									.addComponent(lblSemantics)
									.addGroup(gl_contentPane.createSequentialGroup()
										.addGap(10)
										.addComponent(chckbxWorkContext))))
							.addGroup(gl_contentPane.createSequentialGroup()
								.addGap(20)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
									.addComponent(textAreaWorkContext, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
									.addComponent(textAreaKeyWords, GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
									.addGroup(gl_contentPane.createSequentialGroup()
										.addComponent(chckbxKeyWords)
										.addPreferredGap(ComponentPlacement.RELATED, 255, Short.MAX_VALUE)))))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblSyntax)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(10)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
										.addComponent(chckbxColorHighlight)
										.addComponent(chckbxSentenceType)))))
						.addComponent(lblMessageAppearance_1))
					.addContainerGap(84, Short.MAX_VALUE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(290, Short.MAX_VALUE)
					.addComponent(btnCancel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnOk, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblMessageAppearance)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblPartOfThe)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(chckbxFirstMWords)
						.addComponent(lblMValue)
						.addComponent(m_value, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxFirstMWords_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblMessageSelection)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblPeriodicalSelection)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(rdbtnEveryNthMessage)
						.addComponent(lblNValue)
						.addComponent(n_value, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(rdbtnDontShowEvery)
						.addComponent(rdbtnOff))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblSemantics)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxWorkContext)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textAreaWorkContext, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxKeyWords)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(textAreaKeyWords, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblSyntax)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxSentenceType)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblMessageAppearance_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxColorHighlight)
					.addPreferredGap(ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnOk)
						.addComponent(btnCancel)))
		);
		contentPane.setLayout(gl_contentPane);
	}
	
	public static String getUserDataDirectory() {
	    return System.getProperty("user.home") + File.separator + ".indikom" + File.separator;
	}
}

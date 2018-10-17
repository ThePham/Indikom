package slackClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.jeremybrooks.knicker.AccountApi;
import net.jeremybrooks.knicker.KnickerException;
import net.jeremybrooks.knicker.WordApi;
import net.jeremybrooks.knicker.dto.Related;
import net.jeremybrooks.knicker.dto.TokenStatus;

public class MessageProcessing {

	public MessageProcessing(){};
	
	/**
	 * Metoda vracia zadany pocet slov od zaciatku povodneho retazca.
	 * 
	 * @param message retazec, z ktoreho sa bude vypisovat cast
	 * @param m pocet slov, ktore sa budu vypisovat
	 * @return retazec m slov od zaciatku povodneho retazca
	 */
	public String getMWordsOfMessage(String message, int m){
		StringBuilder messageBuild = new StringBuilder();
		int pocetSlov = 0;
		for (int i = 0 ; i < message.length() ; i++){
			messageBuild.append(message.substring(i, i+1));
			if (((i+1) < message.length()) || (i+1) == message.length()){
				if (message.substring(i, i+1).equals(" ")){
					pocetSlov++;
				}
			}
			if (pocetSlov == m){
				break;
			}
		}
		return messageBuild.toString();
	}
	
	/**
	 * Metoda vracia retazec so zadanym poctom slov nachadzajucimi sa za zadanym klucovym slovom v zadanom retazci.
	 * 
	 * @param message retazec, z ktoreho sa bude vypisovat cast
	 * @param m pocet slov za klucovym slovom, ktore sa budu vypisovat
	 * @param keyWord klucove slovo, za ktorym slova vratane klucoveho slova sa budu vypisovat
	 * @return retazec m slov od klucoveho slova vratane v povodnom retazci
	 */	
	public String getMWordsOfMessage(String message, int m, String keyWord){
		StringBuilder messageBuild = new StringBuilder();
		int pocetSlov = 0;
		if (message.contains(keyWord) == false){
			return getMWordsOfMessage(message, m);
		}
		int keyWordPosition = message.indexOf(keyWord);
		for (int i = keyWordPosition ; i < message.length() ; i++){
			messageBuild.append(message.substring(i, i+1));
			if (((i+1) < message.length()) || (i+1) == message.length()){
				if (message.substring(i, i+1).equals(" ")){
					pocetSlov++;
				}
			}
			if (pocetSlov == m){
				break;
			}
		}
		
		return messageBuild.toString();
	}
	
	
	public MessageObject getFinalMessage(String message, int messageNum){
		
		boolean color = false;
		boolean showMessage = false;
		boolean keyWords = false;
		boolean workContext = false;
		
		boolean first_m_words = false;
		boolean first_m_words_key_word = false;
		boolean whole_msg = false;
		boolean n_th_msg = false;
		boolean not_nth_msg = false;
		boolean sentence_type = false;
		
		int controlValue = 0;	//ak je podmienka pre zobrazenie spravy platna, hodnota controlValue sa inkrementuje - ak je na konci hodnota controlValue 0, znamena to, ze sprava sa zobrazovat nebude
		String finalString = null;
		String keyWord = "";
		//zistujeme, ci spravu zobrazime na zaklade periodicity
		int periodicity = getPeriodicityInterval();
		if (periodicity != 0){
			if (messageNum % periodicity == 0){
				controlValue++;
				n_th_msg = true;
			}
		}
		//zistujeme, ci spravu zobrazime na zaklade semantiky - najprv pracovny kontext, potom klucove slova
		if (isMessageWorkingContextRelevant(message).equals("//**N-U-L-L**//") == false){
			workContext = true;
			controlValue++;
			keyWord = isMessageWorkingContextRelevant(message); 
		}
		if (isMessageKeyWordsRelevant(message).equals("//**N-U-L-L**//") == false){
			controlValue++;
			keyWords = true;
			keyWord = isMessageKeyWordsRelevant(message);	//za tento keyWord potrebujeme nalepit urcity pocet slov, ak je zapnute zobrazenie m slov za klucovym slovom
		}
		//zistujeme, ci spravu zobrazujeme na zaklade syntaxe - otazniky, vykricniky
		if (isSentenceTypRelevant(message) == true){
			controlValue++;
			sentence_type = true;
			
			//zistime, ci spravu budeme zobrazovat farebne
			try{
				FileReader saveRead = new FileReader(getUserDataDirectory() + "configsave.txt");
				BufferedReader br = new BufferedReader(saveRead);
				String line = "";
				for (int i = 0 ; i < 14 ; i++){
					line = br.readLine();
				}
				if (line.equals("color_highlight true")){
					color = true;
				}
			} catch(FileNotFoundException e){
				System.out.println("Configuration save file not found");
				showMessage = false;
			} catch (IOException e) {
				System.out.println("Error reading configuration save file");
				showMessage = false;
			}
		}
		
		if (controlValue > 0){
			showMessage = true;
		}
		else {
			MessageObject fMsg = new MessageObject(finalString, color, showMessage);
			return fMsg;
		}
		
		int m;
		
		if ((workContext == true) || (keyWords == true)){
			m = numberOfWordsAfterKeywords();
			if (m != 0){
				finalString = getMWordsOfMessage(message, m, keyWord);
				first_m_words_key_word = true;
			}
			else{
				m = numberOfWordsOfMessage();
				if (m != 0){
					finalString = getMWordsOfMessage(message, m);
					first_m_words = true;
				}
				else{
					finalString = message;
					whole_msg = true;
				}
			}
		}
		else{
			m = numberOfWordsOfMessage();
			if (m != 0){
				finalString = getMWordsOfMessage(message, m);
				first_m_words = true;
			}
			else{
				finalString = message;
				whole_msg = true;
			}
		}
		
		MessageObject finalMessage = new MessageObject(finalString, color, showMessage);
		finalMessage.setN_th_msg(n_th_msg);
		finalMessage.setWork_context(workContext);
		finalMessage.setKey_word(keyWords);
		finalMessage.setSentence_type(sentence_type);
		finalMessage.setFirst_m_words_key_word(first_m_words_key_word);
		finalMessage.setFirst_m_words(first_m_words);
		finalMessage.setWhole_msg(whole_msg);
		
		if (showMessage == true){		//ulozime si informacie o zobrazenej sprave do textoveho suboru auto_show_msg.txt
			saveAutoShowMessageInfo(finalMessage);
		}
		
		return finalMessage;
	}
	
	/**
	 * metoda do suboru auto_show_msg.txt ulozi informacie o automaticky zobrazenej sprave
	 * @param msg
	 */
	public void saveAutoShowMessageInfo(MessageObject msg){
		FileWriter saveWriter = null;
		
		try {
			saveWriter = new FileWriter(getUserDataDirectory() + "auto_show_msg.txt", true);
			long timeStamp = System.currentTimeMillis() / 1000L;
     	    saveWriter.write(Long.toString(timeStamp) + "\n");
			if (msg.getFirst_m_words() == true){
     		   	saveWriter.write("first m words\n");
     	   	}            	   
     	   	if (msg.getFirst_m_words_key_word() == true){
     	   		saveWriter.write("first m words key word\n");
     	   	}
     	   	if (msg.getWhole_msg() == true){
     	   		saveWriter.write("whole msg\n");
     	   	}
     	   	if (msg.getN_th_msg() == true){
     	   		saveWriter.write("nth message\n");
     	   	}
     	   	if (msg.getNot_nth_msg() == true){
     	   		saveWriter.write("not nth message\n");
     	   	}
     	   	if (msg.getWork_context() == true){
     	   		saveWriter.write("work context\n");
     	   	}
     	   	if (msg.getKey_word() == true){
     	   		saveWriter.write("key word\n");
     	   	}
     	   	if (msg.getSentence_type() == true){
     	   		saveWriter.write("sentence type\n");  
     	   	}
     	   	saveWriter.write("//----------\n");
			
			
		} catch (IOException e) {
			System.out.println("Error writing info about automatic shown message");
		} finally {
			try {
				saveWriter.close();
			} catch (IOException e) {
				System.out.println("Error closing file writer.");
			}
		}
	}
	
	/**
	 * metoda vrati hodnotu, ktora oznacuje, kolko slov spravy sa ma zobrazit, ak funkcia podla konfiguracneho suboru nie je aktivovana, vrati 0
	 * @return pocet slov zobrazenych z prijatej spravy, 0 ak je funkcia vypnuta
	 */
	public int numberOfWordsOfMessage(){
		
		int param_m = 0;
		
		try{
			FileReader saveRead = new FileReader(getUserDataDirectory() + "configsave.txt");
			BufferedReader br = new BufferedReader(saveRead);
			String line = "";
			line = br.readLine();
			
			if (line.equals("m_words true")){
				param_m = Integer.parseInt(br.readLine());
			}
			
			saveRead.close();			
		} catch(FileNotFoundException e){
			System.out.println("Configuration save file not found");
			return 0;
		} catch (IOException e) {
			System.out.println("Error reading configuration save file");
			return 0;
		}
		
		return param_m;
	}
	
	/**
	 * metoda vrati hodnotu, ktora oznacuje kolko slov sa ma zobrazit za klucovym slovom. Ak zobrazovanie slov za klucovym slovom nie je zapnute - podla konfiguracneho suboru, metoda vrati 0
	 * @return pocet slov zobrazenych za klucovym slovom, 0 ak funkcia nie je zapnuta
	 */
	public int numberOfWordsAfterKeywords(){
		
		int param_m = 0;
		
		try{
			FileReader saveRead = new FileReader(getUserDataDirectory() + "configsave.txt");
			BufferedReader br = new BufferedReader(saveRead);
			String line = "";
			for (int i = 0 ; i < 3 ; i++){
				line = br.readLine();
			}
			if (line.equals("m_words_key true")){
				param_m = Integer.parseInt(br.readLine());
			}
			
			saveRead.close();			
		} catch(FileNotFoundException e){
			System.out.println("Configuration save file not found");
			return 0;
		} catch (IOException e) {
			System.out.println("Error reading configuration save file");
			return 0;
		}
		
		return param_m;
	}
	
	public static String getUserDataDirectory() {
	    return System.getProperty("user.home") + File.separator + ".indikom" + File.separator;
	}
	
	/**
	 * method returns number meaning how often are messages to be shown periodically given from the configuration save file
	 * 
	 * @return integer - every n-th message that will be shown
	 */
	public int getPeriodicityInterval(){
		int param_n = 0;
		try{
			FileReader saveRead = new FileReader(getUserDataDirectory() + "configsave.txt");
			BufferedReader br = new BufferedReader(saveRead);
			String line = "";
			for (int i = 0 ; i < 5 ; i++){
				line = br.readLine();
			}
			if (line.equals("n_th_message true")){
				param_n = Integer.parseInt(br.readLine());
			}
			
			saveRead.close();			
		} catch(FileNotFoundException e){
			System.out.println("Configuration save file not found");
			return 0;
		} catch (IOException e) {
			System.out.println("Error reading configuration save file");
			return 0;
		}
		return param_n;
	}
	
	
	/**
	 * metoda zistuje, ci zadana sprava obsahuje klucove slova, ktore su ulozene v konfiguracnom subore
	 * @param message - prijata sprava, v ktorej sa budu hladat klucove slova
	 * @return
	 */
	public String isMessageWorkingContextRelevant(String message){
		
		String keyWord = "//**N-U-L-L**//";
		
		try {
			FileReader saveRead = new FileReader(getUserDataDirectory() + "configsave.txt");
			BufferedReader br = new BufferedReader(saveRead);
		    
			message = message.toLowerCase();
			
			String line = "";
			for (int i = 0 ; i < 9 ; i++){
				line = br.readLine();
			}
			if (line.equals("work_context true")){
				String keyWords = br.readLine();
				List<String> keyWordsList = getKeyWords(keyWords);
				for (int i = 0 ; i < keyWordsList.size() ; i++){
					if (message.contains(keyWordsList.get(i)) == true){
						return keyWordsList.get(i);
					}
				}
				String words = "projekt*project*data*document*dokument*class*trieda*metoda*metóda*funkcia*method*function*kod*kód*code*source*zdroj*bug*git*bit*comm*push*merge*pull*vers*verz*ver.*web*task*dizaj*design*plan*plán*program*diagram*analýza*analyza*analys*test*klient*client*implement*sluzb*služb*service*prezent*branch*sprint*šprint*dokument*docum*.doc*repozit*reposit*error";
				List<String> keyWordsListStatic = getKeyWords(words);
				for (int i = 0 ; i < keyWordsListStatic.size() ; i++){
					if (message.contains(keyWordsListStatic.get(i)) == true){
						return keyWordsListStatic.get(i);
					}
				}
			}
			saveRead.close();
			
		} catch(FileNotFoundException e){
			System.out.println("Configuration save file not found");
			return keyWord;
		} catch (IOException e) {
			System.out.println("Error reading configuration save file");
			return keyWord;
		}
		
		return keyWord;
	}
	
	public String isMessageKeyWordsRelevant(String message){
		
		String keyWord = "//**N-U-L-L**//";
		
		try {
			FileReader saveRead = new FileReader(getUserDataDirectory() + "configsave.txt");
			BufferedReader br = new BufferedReader(saveRead);
		    
			message = message.toLowerCase();
			
			String line = "";
			for (int i = 0 ; i < 11 ; i++){
				line = br.readLine();
			}
			if (line.equals("key_words true")){
				String keyWords = br.readLine();
				List<String> keyWordsList = getKeyWords(keyWords);
				for (int i = 0 ; i < keyWordsList.size() ; i++){
					if (message.contains(keyWordsList.get(i)) == true){
						return keyWordsList.get(i);
					}
				}
			}
			saveRead.close();
			
		} catch(FileNotFoundException e){
			System.out.println("Configuration save file not found");
			return keyWord;
		} catch (IOException e) {
			System.out.println("Error reading configuration save file");
			return keyWord;
		}
		return keyWord;
	}
	
	/**
	 * 
	 * @param words - string where words are delimited with "*" for example word1*word2*word3
	 * @return list of words
	 */
	List<String> getKeyWords(String words){
		
		String[] wordsArray = words.split("\\*");
		List<String> wordsList = Arrays.asList(wordsArray);  
		
		return wordsList;
	}
	
	/**
	 * metoda vrati true, ak sprava obsahuje "?" alebo "!"
	 * @param message
	 * @return
	 */
	public boolean isSentenceTypRelevant(String message){
		
		try{
			FileReader saveRead = new FileReader(getUserDataDirectory() + "configsave.txt");
			BufferedReader br = new BufferedReader(saveRead);
			
			String line = "";
			for (int i = 0 ; i < 13 ; i++){
				line = br.readLine();
			}
			if (line.equals("sentence_type true")){
				if ((message.contains("?") == true) || (message.contains("!") == true)){
					br.close();
					return true;
				}
				else{
					br.close();
					return false;
				}
				
			}
			saveRead.close();
		} catch(FileNotFoundException e){
			System.out.println("Configuration save file not found");
			return false;
		} catch (IOException e) {
			System.out.println("Error reading configuration save file");
			return false;
		}
		return false;
	}
	
	public List<String> getRelatedWords(String word) {
		
		List<String> relatedWords = null;
		List<Related> words = null;
		
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
    	    return relatedWords;
    	}	
		
		try {
			words = WordApi.related(word);
		} catch (KnickerException e) {
			e.printStackTrace();
		}
		
		if (relatedWords.size() == 0) {
			System.out.println("Nothing was found.");			
		}
					
    	for (Related w : words) {
    		//System.out.println(word.toString());
    		//System.out.println("Type: " + word.getRelType());
    		//System.out.println("-----------------");
    		if (!("rhyme").equals(w.getRelType()) && !("cross-reference").equals(w.getRelType())) {
	    		List<String> x = w.getWords();
	    		for (String y : x)
	    			relatedWords.add(y);
    		}
    	}
		
		return relatedWords;
	}
}

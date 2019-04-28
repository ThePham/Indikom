package slackClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.jeremybrooks.knicker.KnickerException;
import net.jeremybrooks.knicker.WordApi;
import net.jeremybrooks.knicker.dto.Related;

public class WordnikDictionary {
	
	public static List<String> getRelatedWords(List<String> pomList) {
	
		List<String> relatedKeywords = new ArrayList<String>();
	
		for (String keyword : pomList) {
			List<Related> relatedWords = null;
			
			try {
				relatedWords = WordApi.related(keyword);
				
				if (relatedWords.size() == 0)
					System.out.println("Nothing was found.");		
				else {
			    	for (Related word : relatedWords) {
			    		if (!("rhyme").equals(word.getRelType()) && !("cross-reference").equals(word.getRelType())) {
				    		List<String> words = word.getWords();
				    		for (String w : words) {
				    			if (!(w.equals(null)))
				    					relatedKeywords.add(w);
				    		}
			    		}
			    	}
				}
				
				try {
					TimeUnit.MILLISECONDS.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			} catch (KnickerException e) {
				e.printStackTrace();
			}
		
		} 		
		
		return relatedKeywords;
	}

}

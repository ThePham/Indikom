package slackClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class GitHubTracker {
	
	//Get changed files 
	public static List<String> getFileChanges(String currentProject) {
		
		List<String> keywords = new ArrayList<String>();

		try {	
			GitHub github = GitHub.connect(App.getGHUsername(), App.getGHToken());
	
			GHRepository repo = github.getRepository("ThePham/" + currentProject);
			
			for (GHCommit c : repo.listCommits()) {
				
				for (org.kohsuke.github.GHCommit.File f : c.getFiles()) {
					System.out.println("Changed file: " + f.getFileName());
					keywords.add(f.getFileName());
				}
			}	
				
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return keywords;
	}
	
	//Get commit message
	public static List<String> getCommitMessage(String currentProject) {
			
		List<String> keywords = new ArrayList<String>();

		try {	
			GitHub github = GitHub.connect(App.getGHUsername(), App.getGHToken());
		
			GHRepository repo = github.getRepository("ThePham/" + currentProject);
			System.out.println(repo.getFullName());
				
			for (GHCommit c : repo.listCommits()) {		
				for (String s : c.getCommitShortInfo().getMessage().split(" "))
						keywords.add(s);
			}				
				
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		return keywords;
	}
}

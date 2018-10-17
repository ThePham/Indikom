package slackClient;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.RateLimitHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import marytts.LocalMaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.util.data.audio.MaryAudioUtils;
import model.FileEvent;
import model.Resource;
import opennlp.tools.coref.mention.Parse;

public class ActivityTracker {

	private static ArrayList<Resource> resources;
	private static ArrayList<FileEvent> files;

	public static void initialize() {

		// File activity tracking WIP
		resources = loadResources();
		files = loadFileEvents();

		files = getInfoFromResources(resources, files);
		files = sortByDuration(files);

		HashMap<String, String> projects = new HashMap();

		for (FileEvent f : files) {
			System.out.println("Project: " + f.getProjectName());
			System.out.println("Package: " + f.getPackageName());
			System.out.println("Class: " + f.getClassName()); 	
			System.out.println("Path: " + f.getPath());
			System.out.println("File ID: " + f.getFileId());
			System.out.println("Duration: " + f.getDuration()); 
			System.out.println("Keywords: " + StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(f.getClassName()), ' '));
			System.out.println("------------------------------------------"); 
			
			if (!projects.containsKey(f.getProjectName()))
				projects.put(f.getProjectName(), f.getDuration());
			else {
				double newDuration = Double.parseDouble(projects.get(f.getProjectName())) + Double.parseDouble(f.getDuration());
				projects.put(f.getProjectName(), String.valueOf(newDuration));
			}
			
		}
		
		String currentProject = files.get(1).getProjectName();
		String pomDuration = projects.get(files.get(1).getProjectName());
		
		for (String key: projects.keySet()) {
		    System.out.println("Project : " + key);
		    System.out.println("Duration : " + projects.get(key));
		    
		    if (Double.parseDouble(projects.get(key)) > Double.parseDouble(pomDuration)) {
		    	currentProject = key;
		    	pomDuration = projects.get(key);
		    }	    
		    
		}
				
		//System.out.println("Project name: " + files.get(files.size() - 1).getProjectName());
		System.out.println("Project name: " + currentProject);

		/*
		// GitHub tracking
		GitHubClient client = new GitHubClient();
		client.setCredentials("ThePham", "GitHubPassword456");
		
		RepositoryService service = new RepositoryService();
		try {
			for (Repository repo : service.getRepositories("ThePham"))
			  System.out.println("Repository: " + repo.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CommitService commitService = new CommitService();
		try {
			List<RepositoryCommit> commitList = commitService.getCommits(service.getRepository("ThePham", files.get(0).getProjectName()));
			for (RepositoryCommit c : commitList) {
				
				if ("ThePham".equals(c.getAuthor().getLogin())) {
					System.out.println("Commit author: " + c.getCommit().getAuthor().getName());
					System.out.println("Commit message: " + c.getCommit().getMessage());
					System.out.println("Commit user: " + c.getAuthor().getLogin());
				}		
			}
		*/
		
		//Github tracking WIP
		try {	
			GitHub github = GitHub.connect("GITHUB_USERNAME", "GITHUB_API_KEY");
			//System.out.println(github.getMyself().getLogin());		
			//System.out.println(github.getRateLimit().limit);
			//System.out.println(github.getRateLimit().remaining);

			//GHRepository repo = github.getRepository("ThePham/" + files.get(files.size() - 1).getProjectName());
			GHRepository repo = github.getRepository("ThePham/" + currentProject);
			System.out.println(repo.getFullName());
			for (GHCommit c : repo.listCommits()) {
				System.out.println("Author login: " + c.getAuthor().getLogin());
				System.out.println("Author name: " + c.getAuthor().getName());
				System.out.println("Date: " + c.getCommitDate().toString());
				System.out.println("Commit message: " + c.getCommitShortInfo().getMessage());
				System.out.println("------------------------------------------"); 

				/*
				for (org.kohsuke.github.GHCommit.File f : c.getFiles()) {
					System.out.println("Changed file: " + f.getFileName());
				}
				*/
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	

	}

	public static ArrayList<Resource> getResources() {
		return resources;
	}

	public static ArrayList<FileEvent> getFiles() {
		return files;
	}

	// Load resources (Java classes)
	public static ArrayList<Resource> loadResources() {
		ArrayList<Resource> resources = new ArrayList<Resource>();

		try {

			File fXmlFile = new File("/Users/USERNAME/Rabbit/C.Users.USERNAME.workspace/ResourceDB/Resources.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("resource");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					// System.out.println("Path : " + eElement.getAttribute("path"));
					// System.out.println("resourceId : " +
					// eElement.getElementsByTagName("resourceId").item(0).getTextContent());

					String[] className = eElement.getAttribute("path").split("/");

					resources.add(new Resource(eElement.getElementsByTagName("resourceId").item(0).getTextContent(),
							eElement.getAttribute("path"), className[className.length - 1]));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return resources;

	}

	// Load file open events
	public static ArrayList<FileEvent> loadFileEvents() {

		ArrayList<FileEvent> files = new ArrayList<FileEvent>();

		// Get current date
		DateFormat dateFormatFull = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		// System.out.println(dateFormatFull.format(date));

		DateFormat dateFormatMonth = new SimpleDateFormat("yyyy-MM");
		Date dateMonth = new Date();
		// System.out.println(dateFormatMonth.format(dateMonth));

		try {
			File fXmlFile = new File("/Users/USERNAME/Rabbit/C.Users.USERNAME.workspace/fileEvents-"
					+ dateFormatMonth.format(dateMonth) + ".xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("fileEvent");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					Element parent = (Element) eElement.getParentNode();

					if (parent.getAttribute("date").equals(dateFormatFull.format(date))) {
						// System.out.println("FileId: " + eElement.getAttribute("fileId"));
						// System.out.println("Duration : " + eElement.getAttribute("duration"));

						files.add(new FileEvent(eElement.getAttribute("fileId"), eElement.getAttribute("duration")));
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return files;
	}

	// Combine data from resources with file events
	public static ArrayList<FileEvent> getInfoFromResources(ArrayList<Resource> resources, ArrayList<FileEvent> files) {
		ArrayList<FileEvent> replacedFiles = new ArrayList<FileEvent>();

		replacedFiles = files;

		for (FileEvent e : replacedFiles) {

			for (Resource r : resources) {
				if (e.getFileId().equals(r.getResourceId())) {
					e.setClassName(r.getClassName());
					e.setPath(r.getPath());

					String[] packages = r.getPath().split("/");
					// System.out.println(r.getPath());

					e.setProjectName(packages[1]);
					e.setPackageName(packages[packages.length - 2]);
				}

			}
		}

		return replacedFiles;
	}

	// Sort files by duration
	public static ArrayList<FileEvent> sortByDuration(ArrayList<FileEvent> files) {
		ArrayList<FileEvent> sortedFiles = files;

		Collections.sort(sortedFiles, new Comparator<FileEvent>() {
			public int compare(FileEvent f1, FileEvent f2) {
				return Double.compare(Double.parseDouble(f1.getDuration()), Double.parseDouble(f2.getDuration()));
			}
		});

		return sortedFiles;
	}

	// Create textToSpeech wav file and play it
	public static void textToSpeech(String text) {

		String fileName = "test.wav"; // Name of sound file
		long delay = 1000; // Delay in ms between TTS instances

		// Initialize
		LocalMaryInterface mary = null;
		try {
			mary = new LocalMaryInterface();
		} catch (MaryConfigurationException e) {
			System.err.println("Could not initialize MaryTTS interface: " + e.getMessage());
			try {
				throw e;
			} catch (MaryConfigurationException e1) {
				e1.printStackTrace();
			}
		}

		// Synthesize
		AudioInputStream audio = null;
		try {
			audio = mary.generateAudio(text);
		} catch (SynthesisException e) {
			System.err.println("Synthesis failed: " + e.getMessage());
		}

		// Write to output
		double[] samples = MaryAudioUtils.getSamplesAsDoubleArray(audio);
		try {
			MaryAudioUtils.writeWavFile(samples, "test.wav", audio.getFormat());
		} catch (IOException e) {
			System.err.println("Could not write to file: \n" + e.getMessage());
		}

		// Play
		try {
			AudioInputStream stream;
			AudioFormat format;
			DataLine.Info info;
			Clip clip;

			stream = AudioSystem.getAudioInputStream(new File(fileName));
			format = stream.getFormat();
			long frames = stream.getFrameLength();
			long durationInms = (long) ((frames + 0.0) / format.getFrameRate() * 1000);
			info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.start();
			Thread.sleep(durationInms + delay);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

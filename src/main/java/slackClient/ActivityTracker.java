package slackClient;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import marytts.LocalMaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.util.data.audio.MaryAudioUtils;
import model.CsvActivity;
import model.FileEvent;
import model.Resource;
import net.jeremybrooks.knicker.KnickerException;
import net.jeremybrooks.knicker.WordApi;
import net.jeremybrooks.knicker.dto.Related;

public class ActivityTracker {

	private static ArrayList<Resource> resources;
	private static ArrayList<FileEvent> files;
	private static boolean eclipse = false;
	
	private static ArrayList<FileEvent> filesAll = new ArrayList<FileEvent>();

	public static void initialize() {

		// File activity tracking
		resources = loadResources();
		files = loadFileEvents();
		
		files = getInfoFromResources(resources, files);
		
		if (filesAll.isEmpty())
			filesAll.addAll(files);
		else {
			files = updateAllFiles(files, filesAll);
		}

		files = removeInactive(files, filesAll);
		files = sortByWeightedDuration(files);
		
		HashMap<String, String> projects = new HashMap();
		
		try {

			for (FileEvent f : files) {
				
				//Delete dot. py and java from keywords
				String[] pomArray = StringUtils.splitByCharacterTypeCamelCase(f.getClassName());
				ArrayList<String> pomList = new ArrayList<String>(Arrays.asList(pomArray));
				pomList.remove("py");
				pomList.remove(".");
				pomList.remove("java");
				pomList.add(f.getPackageName());
				pomList.addAll(Arrays.asList(StringUtils.splitByCharacterTypeCamelCase(f.getPackageName())));
				
				//Get GitHub file changes
				pomList.addAll(GitHubTracker.getFileChanges(f.getProjectName()));
						
				//Get related words to keywords using thesaurus
				if (App.getUseWordnik() == true) {
					pomList.addAll(WordnikDictionary.getRelatedWords(pomList));
				}
				
				f.setKeywords(pomList);
				
				if (!projects.containsKey(f.getProjectName()))
					projects.put(f.getProjectName(), f.getDuration());
				else {
					double newDuration = Double.parseDouble(projects.get(f.getProjectName())) + Double.parseDouble(f.getDuration());
					projects.put(f.getProjectName(), String.valueOf(newDuration));
				}	
			}
			
		} catch (Exception e) {
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
	private static ArrayList<Resource> loadResources() {
		ArrayList<Resource> resources = new ArrayList<Resource>();

		try {

			File fXmlFile = new File(App.getRabbitLocation() + "ResourceDB/Resources.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("resource");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

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
	private static ArrayList<FileEvent> loadFileEvents() {

		ArrayList<FileEvent> files = new ArrayList<FileEvent>();

		// Get current date
		DateFormat dateFormatFull = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();

		DateFormat dateFormatMonth = new SimpleDateFormat("yyyy-MM");
		Date dateMonth = new Date();

		//Try loading Eclipse IDE activity
		try {
			File fXmlFile = new File(App.getRabbitLocation() + "fileEvents-"
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
						files.add(new FileEvent(eElement.getAttribute("fileId"), eElement.getAttribute("duration")));
					}

				}
			}
			eclipse = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Try loading Jetbrains IDE activity
		try {
			String CSV_FILENAME = (App.getRabbitLocation() + "ide-events.csv");

	    	ArrayList<String> listA = new ArrayList<String>();
	    	
	    	//Load .csv file
	    	try(ICsvBeanReader beanReader = new CsvBeanReader(new FileReader(CSV_FILENAME), 
	    			CsvPreference.STANDARD_PREFERENCE)) {
	            final String[] headers = new String[]{
	            		"atr1", "atr2", "atr3", "atr4", "atr5", "atr6", "atr7", "atr8", "atr9", "atr10", "atr11"
	            };
	            final CellProcessor[] processors = getProcessors();
	 
	            CsvActivity activity;
	            while ((activity = beanReader.read(CsvActivity.class, headers, processors)) != null) {
	                listA.add(activity.getAtr7());
	            }
	        }
	    	
	    	listA.removeAll(Collections.singleton(null));
	    	
	    	//Set duration
	    	Set<String> pom = new HashSet<String>(listA);
	    	ArrayList<Integer> fileDuration = new ArrayList<Integer>();
	    	
	    	for (String s : listA) {
	    		pom.add(s);
	    	}
	    	
	    	ArrayList<String> fileName = new ArrayList<String>(pom);

	    	for (String s : fileName) {
	    		fileDuration.add(0);
	    	}
	    	
	    	for (String s : listA) {
	    		for (String s2 : fileName) {
	    			if (s2.equals(s)) {
	    				Integer pom2 = fileDuration.get(fileName.indexOf(s2));
	    				fileDuration.set(fileName.indexOf(s2), pom2 + 1000);
	    			}
	    		}
	    	}
	    	
	    	for (String s : fileName) {
	    		files.add(new FileEvent(s, fileDuration.get(fileName.indexOf(s)).toString()));
	    	}
	    	eclipse = false;
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return files;
	}

	// Combine data from resources with file events
	private static ArrayList<FileEvent> getInfoFromResources(ArrayList<Resource> resources, ArrayList<FileEvent> files) {
		ArrayList<FileEvent> replacedFiles = new ArrayList<FileEvent>();

		replacedFiles = files;

		for (FileEvent e : replacedFiles) {
			
			if (eclipse) {
				for (Resource r : resources) {
					if (e.getFileId().equals(r.getResourceId())) {
						e.setClassName(r.getClassName());
						e.setPath(r.getPath());
	
						String[] packages = r.getPath().split("/");
	
						e.setProjectName(packages[1]);
						e.setPackageName(packages[packages.length - 2]);
						
						e.setWeightedDuration(Double.parseDouble(e.getDuration()));
					}
	
				}
			}
			else {
				e.setPath(e.getFileId());
				String[] packages = e.getPath().split("/");
				
				e.setProjectName(packages[packages.length - 3]);
				e.setPackageName(packages[packages.length - 2]);
				e.setClassName(packages[packages.length - 1]);
				
				e.setWeightedDuration(Double.parseDouble(e.getDuration()));
			}
		}

		return replacedFiles;
	}

	// Sort files by duration
	private static ArrayList<FileEvent> sortByDuration(ArrayList<FileEvent> files) {
		ArrayList<FileEvent> sortedFiles = files;

		Collections.sort(sortedFiles, new Comparator<FileEvent>() {
			public int compare(FileEvent f1, FileEvent f2) {
				return Double.compare(Double.parseDouble(f1.getDuration()), Double.parseDouble(f2.getDuration()));
			}
		});

		return sortedFiles;
	}
	
	// Sort files by weighted duration
	private static ArrayList<FileEvent> sortByWeightedDuration(ArrayList<FileEvent> files) {
		ArrayList<FileEvent> sortedFiles = files;

		Collections.sort(sortedFiles, new Comparator<FileEvent>() {
			public int compare(FileEvent f1, FileEvent f2) {
				return Double.compare(f1.getWeightedDuration(), f2.getWeightedDuration());
			}
		});

		return sortedFiles;
	}
	
	//Update list of all files accessed
	private static ArrayList<FileEvent> updateAllFiles(ArrayList<FileEvent> files, ArrayList<FileEvent> filesAll) {

		for (FileEvent f : files) {	
			for (FileEvent fa : filesAll) {
				if (f != null && fa != null && f.getPath().equals(fa.getPath()) && f.getWeightedDuration() == fa.getWeightedDuration()) {
					fa.setDuration(f.getDuration());
					fa.setWeightedDuration(f.getWeightedDuration());
				}				
			}

		}
		
		return filesAll;
	}
	
	//Remove files that were inactive
	private static ArrayList<FileEvent> removeInactive(ArrayList<FileEvent> files, ArrayList<FileEvent> filesAll) {
		
		ArrayList<FileEvent> files1 = new ArrayList<FileEvent>(files);
		ArrayList<FileEvent> files2 = new ArrayList<FileEvent>(filesAll);
		
		for (FileEvent f : files1) {	
			for (FileEvent fa : files2) {
				
				//Reduce weighted duration by half if file was not accessed
				if (f != null && fa != null && f.getPath().equals(fa.getPath()) && f.getDuration().equals(fa.getDuration())) {
					f.setWeightedDuration(f.getWeightedDuration() * 0.5);
					fa.setWeightedDuration(fa.getWeightedDuration() * 0.5);
				}				
			}
			
			//Remove files accessed for less than 1 minute after weighing
			if (f != null && f.getWeightedDuration() < 60000)
				files.remove(f);
			
		}
		
		return files1;
	}

	// Create textToSpeech wav file and play it
	public static void textToSpeech(String text) {

		String fileName = "test.wav"; 
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
	
	//Helper function for csv file processing
	private static CellProcessor[] getProcessors() {
 
        final CellProcessor[] processors = new CellProcessor[] {
        		new Optional(),
        		new Optional(),
        		new Optional(),
        		new Optional(),
        		new Optional(),
        		new Optional(),
         		new Optional(),
        		new Optional(),
        		new Optional(),
        		new Optional(),
        		new Optional()

        };
        return processors;
    }
}

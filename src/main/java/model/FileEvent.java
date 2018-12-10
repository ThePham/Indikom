package model;

import java.util.ArrayList;

public class FileEvent {
	private String fileId;
	private String duration;
	private String path;
	private String className;
	private String projectName;
	private String packageName;
	private double weightedDuration;
	private ArrayList<String> keywords;
	
	public String getClassName() {
		return className;
	}

	public double getWeightedDuration() {
		return weightedDuration;
	}

	public void setWeightedDuration(double weightedDuration) {
		this.weightedDuration = weightedDuration;
	}

	public ArrayList<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(ArrayList<String> keywords) {
		this.keywords = keywords;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public FileEvent(String fileId, String duration) {
		this.fileId = fileId;
		this.duration = duration;
	}
	
	public String getFileId() {
		return fileId;
	}
	
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
}

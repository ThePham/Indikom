package model;

public class Resource {
	
	private String resourceId;
	private String path;
	private String className;
	
	public Resource(String resourceId, String path, String className) {
		this.resourceId = resourceId;
		this.path = path;  
		this.className = className;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}

}

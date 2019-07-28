package classes;

import java.util.Date;

public class TDEntry {
	
	private String title = "";
	private String body = "";
	private boolean checked;
	private Date dateCreated;
	private Date dateChecked;
	private String category;
	
	public TDEntry(String title, String body, boolean checked, 
			Date dCreated, Date dChecked, String category) {
		this.title = title;
		this.body = body;
		this.checked = checked;
		this.dateCreated = dCreated;
		this.dateChecked = dChecked;
		this.category = category;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public Date getDCreated() {
		return dateCreated;
	}

	public void setDCreated(Date created) {
		this.dateCreated = created;
	}

	public Date getDChecked() {
		return dateChecked;
	}

	public void setDChecked(Date dChecked) {
		this.dateChecked = dChecked;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
}

package classes;

import java.util.Date;
import java.util.List;

public class TDCategory {
	
	private List<TDEntry> entries;
	private String name;
	private Date dateCreated;
	
	public TDCategory(List<TDEntry> entries, String name, Date date) {
		super();
		this.entries = entries;
		this.name = name;
		this.dateCreated = date;
	}
	public List<TDEntry> getEntries() {
		return entries;
	}
	public void setEntries(List<TDEntry> entries) {
		this.entries = entries;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDCreated() {
		return dateCreated;
	}
	public void setDCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public int getNEntries() {
		return entries.size();
	}
	public int getNChecked() {
		int nChecked = 0;
		for(TDEntry e : entries) {
			if(e.isChecked()) nChecked ++;
		}
		return nChecked;
	}	
	public int getNPending() {
		int nPendig = 0;
		for(TDEntry entry : entries){
			if(!entry.isChecked()) nPendig++;
		}
		return nPendig;
	}
}

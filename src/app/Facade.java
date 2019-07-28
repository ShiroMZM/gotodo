package app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import classes.TDCategory;
import classes.TDEntry;

public class Facade {
	
	private DataManager dm;
	
	public Facade() {
		dm = new DataManager();
	}

	public int addCategory (String name) throws IOException {
		if(getCategoryIndex(name) == -1) {
			dm.getCategories().add(
				new TDCategory(
					new ArrayList<TDEntry>(), name, new Date()));
			dm.updateData();
			return 1;
		}
		return 0;
	}

	public int deleteCategory(String name) throws IOException {
		int index = getCategoryIndex(name);
		if(index != -1) {
			dm.getCategories().remove(index);
			dm.updateData();
		}
		return 1;
	}
	
	public int addEntry(String body) throws IOException {
		if(getCategoryIndex("Uncategorized") == -1)
			addCategory("Uncategorized");
		TDCategory c = getCategory("Uncategorized");
		if(c != null) {
			c.getEntries().add(new TDEntry(assignEntryName(c), body, false, new Date(), null, "Uncategorized"));
			dm.updateData();
			return 1;
		}
		return 0;
	}

	public int addEntry (String name, String body, boolean isEntryName) throws Exception {
		String eName;
		TDCategory c;
		if(isEntryName){
			c = getCategory("Uncategorized");
			eName = name;
		} else {
			c = getCategory(name);
			eName = assignEntryName(c);
		}
		if(c != null) {
			if(getEntry(c.getName(), eName) != null){
				System.out.println("Ya existe una entrada en esa categoria con ese titulo.");
				throw new Exception("addEException");
			}
			c.getEntries().add(new TDEntry(eName, body, false, new Date(), null, c.getName()));
			dm.updateData();
			return 1;
		}
		return 0;
	}
	
	public int addEntry (String cName, String body, String eName) throws Exception {
		TDCategory c = getCategory(cName);
		if(c != null) {
			if(getEntry(c.getName(), eName) != null){
				System.out.println("Ya existe una entrada en esa categoria con ese titulo.");
				throw new Exception("addEException");
			}
			c.getEntries().add(new TDEntry(eName, body, false, new Date(), null, cName));
			dm.updateData();
			return 1;
		}
		return 0;
	}

	public int addEntry (int cIndex, String body) throws IOException {
		try {
			TDCategory c = dm.getCategories().get(cIndex);
			c.getEntries().add(new TDEntry(assignEntryName(c), body, false, new Date(), null, c.getName()));
			dm.updateData();
			return 1;			
		} catch(IndexOutOfBoundsException e) {
			return 0;
		}
	}
	
	public int addEntry (int cIndex, String body, String eName) throws Exception {
		try {
			TDCategory c = dm.getCategories().get(cIndex);
			if(getEntry(c.getName(), eName) != null){
				System.out.println("Ya existe una entrada en esa categoria con ese titulo.");
				throw new Exception("addEException");
			}
			c.getEntries().add(new TDEntry(eName, body, false, new Date(), null, c.getName()));
			dm.updateData();
			return 1;			
		} catch(IndexOutOfBoundsException e) {
			return 0;
		}
	}
	
	public int delEntry (int index) throws IOException {
		try {
			TDEntry entry = getEntries(/*checked=*/false).get(index);
			TDCategory cat = getCategory(entry.getCategory());
			TDEntry e;
			for(int i = 0 ; i < cat.getEntries().size() ; i++) {
				e = cat.getEntries().get(i);
				if(entry.getTitle().equals(e.getTitle())) {
					cat.getEntries().remove(i);
					dm.updateData();
					return 1;
				}
			}
		} catch (IndexOutOfBoundsException e) {}
		return 0;
	}
	
	public int delEntry (int cIndex, String eName) throws IOException {
		try {
			TDCategory cat = dm.getCategories().get(cIndex);
			TDEntry e;
			for(int i = 0 ; i < cat.getEntries().size() ; i++) {
				e = cat.getEntries().get(i);
				if(eName.equals(e.getTitle())) {
					cat.getEntries().remove(i);
					dm.updateData();
					return 1;
				}
			}			
		} catch (IndexOutOfBoundsException e) {}
		return 0;
	}
	
	public int delEntry (String cName, String eName) throws IOException {
		TDCategory cat = getCategory(cName);
		TDEntry e;
		for(int i = 0 ; i < cat.getEntries().size() ; i++) {
			e = cat.getEntries().get(i);
			if(eName.equals(e.getTitle())) {
				cat.getEntries().remove(i);
				dm.updateData();
				return 1;
			}
		}
		return 0;
	}
	
	public int checkEntry(int index) throws IOException {
		try {
			List<TDEntry> unchecked = getEntries(/*checked=*/false);
			TDEntry entry = unchecked.get(index);
			entry.setChecked(true);
			entry.setDChecked(new Date());
			dm.updateData();
			return 1;			
		} catch(IndexOutOfBoundsException e) {
			return 0;
		}
	}
	
	public int setCheckEntry(String cName, String eName, boolean state) throws IOException {
		TDEntry entry = getEntry(cName, eName);
		if(entry != null) {
			entry.setChecked(state);
			if(state)
				entry.setDChecked(new Date());
			else
				entry.setDChecked(new Date(0,0,1));
			dm.updateData();
			return 1;			
		}
		return 0;
	}
	
	public int setCheckEntry(int cIndex, String eName, boolean state) throws IOException {
		TDEntry entry = getEntry(cIndex, eName);
		if(entry != null) {
			entry.setChecked(state);
			if(state)
				entry.setDChecked(new Date());
			dm.updateData();
			return 1;			
		}
		return 0;
	}
	
	public List<TDEntry> getEntries(Boolean checked){
		List<TDEntry> result = new ArrayList<TDEntry>();
		if (checked == null) 
			for (TDCategory category : dm.getCategories())
				for (TDEntry entry : category.getEntries())				
					result.add(entry);			
		else 
			for (TDCategory category : dm.getCategories())
				for (TDEntry entry : category.getEntries())
					if (entry.isChecked() == checked.booleanValue())
						result.add(entry);
			
		return result;
	}
	
	public List<TDEntry> getEntriesByCategory(String cName, Boolean checked){
		TDCategory cat = getCategory(cName);
		if(cat != null && !cat.getEntries().isEmpty())
			if(checked == null) {				
				return cat.getEntries();
			} else {
				List<TDEntry> entries = new ArrayList<TDEntry>();
				for(TDEntry entry : cat.getEntries()) {					
					if (entry.isChecked() == checked.booleanValue()) {
						entries.add(entry);
					}
				}
				return entries;
			}
		return null;
	}
	
	public List<TDEntry> getEntriesByCategory(int index, Boolean checked){
		try {
			TDCategory cat = getCategories().get(index);
			if(cat != null && !cat.getEntries().isEmpty()){
				if(checked == null) {				
					return cat.getEntries();
				} else {
					List<TDEntry> entries = new ArrayList<TDEntry>();
					for(TDEntry entry : cat.getEntries()) {					
						if (entry.isChecked() == checked.booleanValue()) {
							entries.add(entry);
						}
					}
					return entries;
				}
			}
		} catch(IndexOutOfBoundsException e) {}
		return null;
	}
	
	public List<TDCategory> getCategories(){
		return dm.getCategories();
	}
	
	public TDEntry getEntry(String cName, String eName) {
		TDCategory c = getCategory(cName);
		if(c != null)
			for(TDEntry entry : c.getEntries()) {
				if(entry.getTitle().equals(eName))
					return entry;
			}
		return null;
	}
	
	public TDEntry getEntry(int cIndex, String eName) {
		TDCategory c = getCategories().get(cIndex);
		if(c != null)
			for(TDEntry entry : c.getEntries()) {
				if(entry.getTitle().equals(eName))
					return entry;
			}
		return null;
	}

	public TDCategory getCategory(String name) {
		for(TDCategory tdc : dm.getCategories()) {
			if(tdc.getName().equals(name))
				return tdc;
		}
		return null;
	}
	
	private int getCategoryIndex(String name) {
		int index = 0;
		if(dm.getCategories() != null){
			for(TDCategory tdc : dm.getCategories()) {
				if(tdc.getName().equals(name))
				return index;
				index += 1;
			}
		}	
		return -1;
	}

	private String assignEntryName(TDCategory category) {
		return "TODO #" + String.valueOf(category.getEntries().size());
	}
}

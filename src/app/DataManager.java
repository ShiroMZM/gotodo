package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import classes.TDCategory;
import classes.TDEntry;

public class DataManager {
	
	private JSONObject json;
	private File f = new File("Data.json");
	private List<TDCategory> categories;

	public DataManager() {  
		try {
			if(!f.exists()) {
				f.createNewFile();
				resetData();
			}
			loadData();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public void loadData() throws IOException {
		categories = new ArrayList<TDCategory>();
		String jText = "";
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(f));
		while((line = reader.readLine()) != null)
			jText += line + "\n";
		reader.close();		
		json = new JSONObject(jText);
		JSONArray arr = json.getJSONArray("categories");
		if(arr!=null && arr.length()>0){
			for (int i = 0; i < arr.length(); i++) {
				JSONObject jsonObject = arr.getJSONObject(i);
				List<TDEntry> cEntries = new ArrayList<TDEntry>();
				String cName = jsonObject.getString("name");
				Date cDate = new Date(jsonObject.getString("DCreated"));
				JSONArray entries = jsonObject.getJSONArray("entries");
				if(entries!=null && entries.length()>0){
					//variables for entry instance
					String eTitle; String eBody; boolean eChecked; Date eDCr; 
					Date eDCh; String eCat; JSONObject entry;
					for (int w = 0; w < entries.length(); w++) {
						entry = entries.getJSONObject(w);
						eTitle = entry.getString("title");
						eBody = entry.getString("body");
						eChecked = entry.getBoolean("checked");
						eDCr = new Date(entry.getString("DCreated"));
						eDCh = entry.has("DChecked") ?
							new Date(entry.getString("DChecked")) : new Date(0, 0, 1);
						eCat = entry.getString("category");
						cEntries.add(new TDEntry(eTitle, eBody, eChecked, eDCr, eDCh, eCat));
					}
				}
				TDCategory category = new TDCategory(cEntries,cName,cDate);
				categories.add(category);
			}
		}
	}
	
	public void resetData() throws IOException {
		categories = new ArrayList<TDCategory>();
		categories.add(new TDCategory(
				new ArrayList<TDEntry>(),
				"Uncategorized",
				new Date()));
		updateData();	
	}
	
	public void updateData() throws IOException {
		json = new JSONObject(this);
		FileWriter fw = new FileWriter(f);
		fw.write(json.toString());
		fw.close();
	}

	public List<TDCategory> getCategories(){
		return this.categories;
	}
}

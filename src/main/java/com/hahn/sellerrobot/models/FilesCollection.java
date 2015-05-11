package com.hahn.sellerrobot.models;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.hahn.sellerrobot.util.RunDeterminant;

public class FilesCollection {
	private static Logger log = LogManager.getLogger(FilesCollection.class);
	
	private Map<String, CSVFile> csvs;
	private Map<String, Object> jsons;
	private JSONParser jsonParser;
	
	public FilesCollection() {
		csvs = new HashMap<String, CSVFile>();
		jsons = new HashMap<String, Object>();
		
		jsonParser = new JSONParser();
	}
	
	public void readFile(String fileName) throws IOException, ParseException {
		String extension = FilenameUtils.getExtension(fileName);
		switch (extension) {
		case "csv":
			parseCSV(fileName);
			break;
		case "json":
			parseJSON(fileName);
			break;
		default:
			throw new IOException("Unhandled file extension " + extension);
		}
	}
	
	public CSVFile parseCSV(String fileName) throws IOException {
		CSVParser parser = CSVParser.parse(getFileWithUtil(fileName), CSVFormat.DEFAULT);
		CSVFile csv = new CSVFile(parser);
		
		csvs.put(FilenameUtils.getBaseName(fileName), csv);
		
		return csv;
	}
	
	public CSVFile getCSV(String baseFileName) {
		if (!csvs.containsKey(baseFileName)) throw new IllegalArgumentException("No file named " + baseFileName + ".csv has been loaded yet!");
		
		return csvs.get(baseFileName);
	}
	
	public Object parseJSON(String fileName) throws ParseException {
		Object obj = jsonParser.parse(getFileWithUtil(fileName));
		jsons.put(FilenameUtils.getBaseName(fileName), obj);
		return obj;
	}
	
	public Object getJSON(String baseFileName) {
		if (!jsons.containsKey(baseFileName)) throw new IllegalArgumentException("No file named " + baseFileName + ".json has been loaded yet!");
		
		return jsons.get(baseFileName);
	}
	
	private String getFileWithUtil(String fileName) {
		String result = "";

		log.debug("Loading file " + fileName);
		ClassLoader classLoader = getClass().getClassLoader();
		try {
			result = IOUtils.toString(classLoader.getResourceAsStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	public static class CSVFile implements RunDeterminant {
		private List<CSVRecord> records;
		private int row;
		
		public CSVFile(CSVParser parser) throws IOException {
			this.records = parser.getRecords();
			this.row = 0;
		}
		
		public String get(int column) {
			return records.get(row).get(column);
		}

		@Override
		public boolean hasNext() {
			return row < records.size();
		}

		@Override
		public void next() {
			row += 1;
		}

		@Override
		public void reset() {
			row = 0;
		}
		
	}
}

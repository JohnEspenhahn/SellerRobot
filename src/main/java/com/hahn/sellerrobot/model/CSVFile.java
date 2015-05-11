package com.hahn.sellerrobot.model;

import java.io.IOException;
import java.util.List;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CSVFile implements RunDeterminant {
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
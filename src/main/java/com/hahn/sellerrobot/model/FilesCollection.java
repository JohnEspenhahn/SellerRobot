package com.hahn.sellerrobot.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FilesCollection {
	private static Logger log = LogManager.getLogger(FilesCollection.class);
	
	private Map<String, TypedFileCollection<?>> collections;
	
	public FilesCollection() {
		collections = new HashMap<String, TypedFileCollection<?>>();
		
		collections.put("csv", new TypedFileCollection<CSVFile>("csv") {
			@Override
			public CSVFile doParse(String filename) throws IOException {
				return new CSVFile(CSVParser.parse(getFileWithUtil(filename), CSVFormat.DEFAULT));
			}
		});
				
		collections.put("json", new TypedFileCollection<String>("json") {
			@Override
			public String doParse(String filename) throws IOException {
				return getFileWithUtil(filename);
			}
		});
	}
	
	public Object parse(String filename) throws IOException {
		return parse(FilenameUtils.getBaseName(filename), FilenameUtils.getExtension(filename));		
	}
	
	public Object parse(String basename, String extension) throws IOException {
		TypedFileCollection<?> collection = collections.get(extension);
		if (collection != null) return collection.parse(basename + "." + extension);
		else throw new IllegalArgumentException("Unhandled file extension " + extension);
	}
	
	public Object get(String filename) {
		return get(FilenameUtils.getBaseName(filename), FilenameUtils.getExtension(filename));		
	}
	
	public Object get(String baseName, String extension) {
		TypedFileCollection<?> collection = collections.get(extension);
		if (collection != null) return collection.get(baseName);
		else throw new IllegalArgumentException("Unhandled file extension " + extension);
	}
	
	private String getFileWithUtil(String filename) {
		String result = "";

		final File file = new File(filename);
		final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		
		log.debug("Loading file " + filename);
		ClassLoader classLoader = getClass().getClassLoader();
		try {
			if (jarFile.isFile()) {
				if (file.isFile()) {
					result = IOUtils.toString(new FileInputStream(file));
				} else {
					log.debug("Extracting file " + filename + " from jar");
					result = IOUtils.toString(classLoader.getResourceAsStream(filename));
					IOUtils.write(result, new FileOutputStream(file));
				}
			} else {
				result = IOUtils.toString(classLoader.getResourceAsStream(filename));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	abstract class TypedFileCollection<E> {
		private Map<String, E> files;
		private String extension;
		
		public TypedFileCollection(String extension) {
			this.files = new HashMap<String, E>();
			this.extension = extension;
		}
		
		public final E parse(String filename) throws IOException {
			E parsed = doParse(filename);
			put(filename, parsed);
			return parsed;
		}
		
		protected abstract E doParse(String filename) throws IOException;
		
		public void put(String filename, E file) {
			files.put(FilenameUtils.getBaseName(filename), file);
		}
		
		public E get(String baseFileName) {
			if (!files.containsKey(baseFileName)) throw new IllegalArgumentException(String.format("No file named %s.%s has been loaded yet!", baseFileName, extension));
			
			return files.get(baseFileName);
		}
	}
}

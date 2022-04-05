package it.polito.elite.sifis.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFileReader {
	public static Properties loadProperties(String fileName) {
		Properties props = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try{
			
			InputStream is = loader.getResourceAsStream(fileName);
	        props.load(is);
		}
		catch(IOException e){
			return null;
		}
        return props;
	}
}

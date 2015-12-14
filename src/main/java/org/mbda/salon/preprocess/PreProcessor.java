package org.mbda.salon.preprocess;

import java.util.logging.FileHandler;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


public class PreProcessor 
{
	private ApplicationContext ctx = null;
	private ReadWriteService service = null;
	final static Logger logger = Logger.getLogger(PreProcessor.class.getName());

	
	public PreProcessor(){
		//logger.g
		//PropertyConfigurator.configure("log4j.properties");
		ctx = new FileSystemXmlApplicationContext("preprocessor-bean.xml");
		service = (ReadWriteService) ctx.getBean("readerService");
	}
	
	public ReadWriteService getservice() {
		return service;
	}
	
    /**
     * @param args
     */
    public static void main( String[] args )
    {

    	
        System.out.println( "Hello World!" );
        PreProcessor preProcessor = new PreProcessor();
        
        //logger.
        logger.info("TEST INFO MESSAGE");
        logger.debug("TEST DEBUG MESSAGE");
        String[] files = preProcessor.getservice().getInputFiles();
        for (String file : files){
        	//System.out.println("Processing file__: " + file);
        	logger.info("Processing file__: " + file);
        	preProcessor.getservice().processInputFile(file);
        }
        
        
        //System.out.println( "Hello World2222!" );
    }
}

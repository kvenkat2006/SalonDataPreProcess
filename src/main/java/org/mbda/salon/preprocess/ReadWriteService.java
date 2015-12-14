package org.mbda.salon.preprocess;

public class ReadWriteService {
	private InputProcessorInterface iInputProcess = null;
	public ReadWriteService (InputProcessorInterface iInputProcess) {
		this.iInputProcess = iInputProcess;
	}
	
	public String[] getInputFiles() {
		return iInputProcess.getInputFiles();
	}
	
	public void processInputFile(String fileName){
		iInputProcess.processInputFile(fileName);
	}

}
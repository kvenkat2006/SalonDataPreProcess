package org.mbda.salon.preprocess;

public interface InputProcessorInterface {
	public String[] getInputFiles();
	public void processInputFile(String fileName);
}

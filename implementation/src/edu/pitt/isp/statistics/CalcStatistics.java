package edu.pitt.isp.statistics;

import java.io.IOException;
import java.sql.SQLException;

import blast.blast.annotator.BlasTool;
import blast.blast.annotator.BlastConstants;

/**
 * Calculate statistics using Blast package
 * the input file should be in Blast format
 */
public class CalcStatistics {
	static String blastFileAlignError = "../results/autoError.MeteorPOS.fr-en.JHU.3003.txt";
	static String catagoryFile = "../categories/ESL-vilar-cats";
	
	static String outputStatFile = "../results/stats";
	
	public static void main(String args[]) throws IOException, SQLException{
		loadAnnotatedFile();
	}

	/**
	 * write the statistics in order to be able to report them in the results
	 * @throws IOException 
	 */
	private static void loadAnnotatedFile() {
		BlastConstants.catFile = catagoryFile;
		BlastConstants.firstFileName = blastFileAlignError;
		
		BlasTool tool = new BlasTool();
        
		tool.calcStats(outputStatFile);
		
	}
	
}

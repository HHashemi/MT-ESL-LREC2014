package edu.pitt.isp.automatic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.cmu.meteor.scorer.MeteorConfiguration;
import edu.cmu.meteor.scorer.MeteorScorer;
import edu.cmu.meteor.scorer.MeteorStats;
import edu.cmu.meteor.util.Constants;
import edu.pitt.isp.alignment.BlastFormat;
import edu.pitt.isp.alignment.POStagging;

public class AutoDetectError {
	static String refFileRaw = "../results/refSents.3003.raw.txt";
	static String hypFileRaw = "../results/sysSents.UG.100.raw.txt";
	
	static String fileAlignment = "../results/FCE.alignments_Huma.txt";	
	static String blastFileAlignError = "../results/autoError.MeteorPOS.FCE_Huma.txt";
	
	static String catagoryFile = "ESL-vilar-cats";
	
	public static void main(String args[]) throws IOException, SQLException{
		detectErrorHypRefUsingAlinmentFile(fileAlignment, blastFileAlignError); 
	}

	/**
	 * This method sends a reference and hypothesis sentences to detects and classifies errors
	 * and then write it on Blast format file:
	 * i.e. : ref sys meteorAlign errors  
	 */
	public static void detectErrorHypRefUsingAlinmentFile(String fileAlignment, String blastFileAlignError) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileAlignment), "UTF8"));
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(blastFileAlignError), "UTF-8"));

		String line;
		line = br.readLine();
		line = br.readLine();
		out.write("#Sentencetypes ref sys \n" + "#catfile " + catagoryFile + "\n");
		while((line = br.readLine()) != null){
			 
			String ref = br.readLine();
			String hyp = br.readLine();
			String alignmentsBlastFormat = br.readLine();
			line = br.readLine();
					
	    	//Main Functionality of this approach:
	    	//Automatically Detect and Classify MT output errors
	    	String errorBlastFormat = (new AutoDetectErrorOnePair()).detectClassifyError("", ref, hyp, alignmentsBlastFormat);
	    	
	    	out.write("\n");
	    	out.write(ref + "\n");
	    	out.write(hyp + "\n");
	    	out.write(alignmentsBlastFormat + "\n");
	    	out.write(errorBlastFormat + "\n");
	    	out.flush();
		}
		br.close();	
		out.close();
	}
}

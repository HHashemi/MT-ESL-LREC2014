package edu.pitt.isp.alignment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLException;

import edu.cmu.meteor.scorer.MeteorConfiguration;
import edu.cmu.meteor.scorer.MeteorScorer;
import edu.cmu.meteor.scorer.MeteorStats;
import edu.cmu.meteor.util.Constants;

/**
 * This class uses Meteor to align sentences of two files.
 * Input: two files with sentences in each line.
 * Output: Alined sentences in Blast format.
 */
public class Align {
	static String refFileRaw = "../results/refSents.fr-en.Fluency.raw.LIMSI-JHU.txt";
	static String hypFileRaw = "../results/sysSents.fr-en.Fluency.raw.LIMSI-JHU.txt";
	
	static String blastFileAlign = "../results/alignments.fr-en.Fluency.LIMSI-JHU.txt";
	static String catagoryFile = "ESL-vilar-cats";
	
	public static void main(String args[]) throws IOException, SQLException{
		alingHypRefSentences();
	}


	/**
	 * This method align to sentences using METEOR and 
	 * then write the alignments in Blast format in order to do annotation using Blast
	 * I only change MeteorConstancts class in Blast package to not consider caseExact module
	 * @throws SQLException
	 * @throws IOException
	 */
	private static void alingHypRefSentences() throws IOException {
		BufferedReader brRef = new BufferedReader(new InputStreamReader(new FileInputStream(refFileRaw), "UTF8"));
		BufferedReader brHyp = new BufferedReader(new InputStreamReader(new FileInputStream(hypFileRaw), "UTF8"));
		 
		//write alignments to files
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(blastFileAlign), "UTF-8"));
			    
	    //use Meteor alignment + normalization
	    MeteorConfiguration config = new MeteorConfiguration();
		config.setLanguage("en");
		config.setNormalization(Constants.NORMALIZE_KEEP_PUNCT);
		MeteorScorer scorer = new MeteorScorer(config);
		
		int count = 0;
		String lineRef, lineHyp;
		out.write("#Sentencetypes ref sys \n#catfile " + catagoryFile + "\n");
	    while((lineRef = brRef.readLine() ) != null && (lineHyp = brHyp.readLine()) != null ){
	    	count++;

	    	//because if UG system output, one sentence was null!
	    	if(lineHyp.length()<1){	    		
	    		continue;
	    	}
	    	
	    	//Meteor: align two sentences
	    	MeteorStats stats = scorer.getMeteorStats(lineHyp, lineRef);
	    	
	    	//write alignments in Blast format
	    	String alignmentsBlastFormat = (new BlastFormat()).convertMeteorToBlastFormat(stats.alignment);
	    	
	    	//add POS tagging to sentences for easier annotation
	    	//Using Stanford POS tagger
	    	String words2POS = (new POStagging()).addPOStagging(stats.alignment.words2);
	    	String words1POS = (new POStagging()).addPOStagging(stats.alignment.words1);
	    	
	    	
	    	//System out print
	    	System.out.println(count);
	    	System.out.println("score: " +stats.score);
	    	System.out.println(words2POS + "\n" + words1POS);
	    	System.out.println(stats.alignment + "\n");
	    	
	    	//write on file
	    	out.write("\n");
	    	out.write(words2POS + "\n");
	    	out.write(words1POS + "\n");
	    	out.write(alignmentsBlastFormat + "\n\n");
	    	//out.flush();
	    }
	    out.close();
	}
}

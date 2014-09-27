package edu.pitt.isp.statistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

import blast.blast.annotator.AnnotationItem;
import blast.blast.annotator.BlasTool;
import blast.blast.annotator.BlastConstants;
import blast.blast.annotator.CategoryModel;
import blast.blast.annotator.Sentence;
import blast.blast.annotator.StatCalculator;
import blast.blast.annotator.StatItem;

public class CompareAnnotations {

	static String blastFileAlignErrorManual = "../results/manual.MeteorPOS.UG.100.txt";
	static String blastFileAlignErrorManualESL = "../results/manual.MeteorPOS.Quaero.100.edited.ESLcat.txt";
	
	static String blastFileAlignErrorAuto = "../results/autoError.MeteorPOS.Quaero.100.txt";
	
	static String catagoryFile = "../categories/ESL-vilar-cats";

	private static int exactMatchType = 0;
	private static int exactMatchwithoutType = 0;
	private static int matchExactType = 0;
	private static int match = 0;
	
	private static CategoryModel models;
	private static StatCalculator statManual;
	private static StatCalculator statAuto;
	
	public static void main(String args[]) throws IOException, SQLException{
		compareManualAuto();
	}

	/**
	 * comparing Manual and Automated annotated MT output sentences
	 */
	private static void compareManualAuto() {
		BlastConstants.catFile = catagoryFile;
		BlastConstants.firstFileName = blastFileAlignErrorManualESL;		
		BlasTool toolManual = new BlasTool();
		
		BlastConstants.firstFileName = blastFileAlignErrorAuto;		
		BlasTool toolAuto = new BlasTool();
		
		ArrayList<Sentence> sentManual = toolManual.getSentences();
		ArrayList<Sentence> sentAuto = toolAuto.getSentences();
		
		models = toolManual.getModels();
		
		int mErrors = 0;
		int aErrors = 0;
		
		TreeMap<String,StatItem> catsMatchType = new TreeMap<String,StatItem>();
		statManual = new StatCalculator(sentManual.size(), models);
		statAuto = new StatCalculator(sentManual.size(), models);
		
		for (int i=0; i<sentManual.size(); i++) {
			mErrors += sentManual.get(i).getErrorAnnots().size();
			aErrors += sentAuto.get(i).getErrorAnnots().size();
			
			statManual.add(sentManual.get(i));
			statAuto.add(sentAuto.get(i));
			
			if (sentManual.get(i).getErrorAnnots().isEmpty() || sentAuto.get(i).getErrorAnnots().isEmpty()) continue;			
			
			for (AnnotationItem m: sentManual.get(i).getErrorAnnots()) {
				for (AnnotationItem a: sentAuto.get(i).getErrorAnnots()) {
					
					//in order to find P, R, F
					Scanner s = new Scanner(a.getType());
	                s.useDelimiter("-");
	                String mainCat = s.next();
	                	                

					//This is the good one
					if(hasOverlap(m, a) && m.getType().equals(a.getType())){
						matchExactType++;						
		                if (!catsMatchType.containsKey(mainCat)) {
		                    String name = models.getLongName("main", mainCat);
		                    String subModelName = models.getSubModel("main", mainCat);
		                    catsMatchType.put(mainCat, new StatItem(mainCat, name,subModelName));
		                }
		                catsMatchType.get(mainCat).add(s, models);
		                
					}					
	                
					if(hasOverlap(m,a)){
						match++;
					}
					
					
					if(m.equals(a)){
						exactMatchwithoutType++;
					}	
					
					if(m.equalsExactly(a)){
						exactMatchType++;
					}			

				}			
			}
			
		}
		
		//calculate Precision,Recall and F-measure
		calcPRF(statManual.getCats(), statAuto.getCats(),catsMatchType);
				
	}

	/**
	 * Calculate Precision, Recall and F-measure
	 * @param cats
	 * @param cats2
	 * @param catsMatchType
	 */
	private static void calcPRF(TreeMap<String, StatItem> catsM, TreeMap<String, StatItem> catsA, TreeMap<String, StatItem> catsC) {
	
		PrintWriter pw = new PrintWriter(System.out, true);
		System.out.println("\ntotally: #M #A #c     P R F");
		double p = (double) matchExactType/statAuto.getNumErrors();
		double r = (double) matchExactType/statManual.getNumErrors();
		double f = 2*((p*r)/(p+r));
		
		System.out.println(statManual.getNumErrors() + " " + statAuto.getNumErrors() + " " + matchExactType + "     "
						+ p + " " + r + " " + f);
		
        System.out.println("\nMain Classifications: #M #A #c      P R F");
        for (String name: catsC.keySet()) {
        	StatItem siC = catsC.get(name);
        	StatItem siM = catsM.get(name);
        	StatItem siA = catsA.get(name);

            siC.printMainPRF(pw, siM, siA); // P = C/A, R = C/M, F = 2(pr/(p+r))
        }
        
        
        System.out.println("\nAll Classifications: #M #A #c       P R F");
        for (String name: catsC.keySet()) {
        	StatItem siC = catsC.get(name);
        	StatItem siM = catsM.get(name);
        	StatItem siA = catsA.get(name);
        	
            siC.printAllPRF(pw, "", siM, siA);
            pw.println();
        }
		
	}

	private static boolean hasOverlap(AnnotationItem m, AnnotationItem a) {
		if((m.getSet("ref").isEmpty() && a.getSet("sys").isEmpty()) || (m.getSet("sys").isEmpty() && a.getSet("ref").isEmpty()))
			return false;
		
		if(hasOverlap(m,a,"ref") && hasOverlap(m,a,"sys"))
			return true;
		return false;
	}

	private static boolean hasOverlap(AnnotationItem m, AnnotationItem a, String type) {
		TreeSet<Integer> mm = m.getSet(type);
		TreeSet<Integer> aa = a.getSet(type);
		
		if(mm.containsAll(aa) || aa.containsAll(mm))
			return true;
	
		return false;
	}
	
}

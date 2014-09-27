package edu.pitt.isp.automatic;

import blast.blast.annotator.AnnotationItem;
import blast.blast.annotator.AnnotationUnit;
import blast.blast.annotator.Sentence;

public class ErrorRefHyp {

	private Sentence sentence;
	private String type = "ref";
	private String[] refList;
	private String[] sysList;
	
	/**
	 * step 3: check alignments of reference and hypothesis
	 * @param sentence 
	 * @param sys 
	 * @param ref 
	 * @param res 
	 * @return
	 */
	public String addErrorRefHyp(Sentence sentence, String ref, String sys, String res) {
		this.sentence = sentence;
		refList = ref.split(" ");
		sysList = sys.split(" ");
		String refWordPOS, sysWordPOS, refPOS, sysPOS;
		int j;
		
		for(int i=0; i< refList.length; i++){
			String type = getAlignType(i);
			
			if(type.length()<1)
				continue;
			
			j = getAlignedSysWord(i);
			refWordPOS = refList[i];
			sysWordPOS = sysList[j];
			refPOS = getPOS(refWordPOS);
			sysPOS = getPOS(sysWordPOS);			
			
			if(type.equals("exact")){
				
				//check their previous words
				if(i>0 && j>0 && !refList[i-1].equals(sysList[j-1])){
					//if their previous words are different DT, label them as confused and delete them from res
					if(getPOS(refList[i-1]).equals("DT") && getPOS(sysList[j-1]).equals("DT")){
						res = res.replaceAll("-1#\\S+#"+(i-1)+"#[^\\s]+", "");
						res = res.replaceAll("-1#"+(j-1)+"#\\S+#[^\\s]+", "");
						res = res + "-1#" + (j-1) + "#"+ (i-1) +"#A-conf ";
					} 
					//if pos tag was IN, propositions are confused
					else if(getPOS(refList[i-1]).equals("IN") && getPOS(sysList[j-1]).equals("IN")){
						res = res.replaceAll("-1#\\S+#"+(i-1)+"#[^\\s]+", "");
						res = res.replaceAll("-1#"+(j-1)+"#\\S+#[^\\s]+", "");
						res = res + "-1#" + (j-1) + "#"+ (i-1) +"#P-repl ";
					}
				}
				continue;
				
			} else if(type.equals("stem")){
				//check POS 1) if is VB or MD add  V. 2) if they are NN-NNS add N. 3) if they are NN or NN-VB add W. 
				if(refPOS.startsWith("VB") && sysPOS.startsWith("VB")) 
					res = res + "-1#" + j + "#"+ i + "#V ";
				else if((refPOS.equals("NN") && sysPOS.equals("NNS")) || (refPOS.equals("NNS") && sysPOS.equals("NN")))
					res = res + "-1#" + j + "#" + i + "#N ";
				else 
					res = res + "-1#" + j + "#" + i + "#W ";
					
			} else if(type.equals("syn")){
				//add word substitution
				res = res + "-1#" + j + "#" + i + "#R-sub ";
				
			} else if(type.equals("para")){
				//if there are two words that are exactly the same ignore them and add the others as replacement
				if(refWordPOS.equals(sysWordPOS) && refPOS.equals(sysPOS))
					continue;
				else{
					if(refPOS.equals("DT") && sysPOS.equals("DT"))
						res = res + "-1#" + j + "#" + i + "#A-conf ";
					else if(refPOS.equals("IN") && sysPOS.equals("IN"))
						res = res + "-1#" + j + "#"+ i +"#P-repl ";
					else if(refPOS.startsWith("VB") && sysPOS.startsWith("VB"))
						res = res + "-1#" + j + "#" + i + "#V ";			
					else	
						res = res + "-1#" + j + "#" + i + "#R-sub ";
				}
			}
		}
		return res;
	}

	private String getPOS(String word) {
		//in order to handle special case of //CD
		if(word.contains("//CD"))
			return "CD";
		
		String[] words = word.split("/");
		if(words.length>0)
			return words[1];
		else{
			System.out.println("ERROR on POS: " + word);
			return "";
		}
	}
	
	private String getWord(String word) {
		//in order to handle special case of //CD
		if(word.contains("//CD"))
			return "/";
		
		String[] words = word.split("/");
		return words[0];
	}

	private int getAlignedSysWord(int refPos) {
		int sysPOS = -1;
		int sysPOSgood = -1;
		int maxCommonLen = 0; 
		for (AnnotationItem a: sentence.getGoodAnnots()) {
            for (AnnotationUnit an: a.getAnnot()) {
                if(an.getPosition() == refPos && an.getType().equals(type)){
                	for (AnnotationUnit an2: a.getAnnot()) {
                		if(an2.getType().equals("sys")){
                			sysPOS = an2.getPosition();
                			if(getPOS(refList[refPos]).equals(getPOS(sysList[sysPOS]))){
                				sysPOSgood = sysPOS;
                				//return sysPOS;
                			}
                			int commonLen = longestSubstr(getWord(refList[refPos]), getWord(sysList[sysPOS]));
                			if( getPOS(refList[refPos]).length()<2 || getPOS(sysList[sysPOS]).length()<2) continue;
                			if((getPOS(refList[refPos]).substring(0, 1)).equals(getPOS(sysList[sysPOS]).substring(0, 1)) &&
                				commonLen > maxCommonLen){
                				sysPOSgood = sysPOS;
                			}
                		}
                	}            	
                }
            }
        }		
		if(sysPOSgood == -1)
			return sysPOS;
		return sysPOSgood;
	}

	private String getAlignType(int pos) {
		for (AnnotationItem a: sentence.getGoodAnnots()) {
            for (AnnotationUnit an: a.getAnnot()) {
                if(an.getPosition() == pos && an.getType().equals(type))
                	return a.getType();
            }
        }
		return "";
	}
	
	private static int longestSubstr(String first, String second) {
	    if (first == null || second == null || first.length() == 0 || second.length() == 0) {
	        return 0;
	    }
	 
	    int maxLen = 0;
	    int fl = first.length();
	    int sl = second.length();
	    int[][] table = new int[fl][sl];
	 
	    for (int i = 0; i < fl; i++) {
	        for (int j = 0; j < sl; j++) {
	            if (first.charAt(i) == second.charAt(j)) {
	                if (i == 0 || j == 0) {
	                    table[i][j] = 1;
	                }
	                else {
	                    table[i][j] = table[i - 1][j - 1] + 1;
	                }
	                if (table[i][j] > maxLen) {
	                    maxLen = table[i][j];
	                }
	            }
	        }
	    }
	    return maxLen;
	}
}

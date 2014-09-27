package edu.pitt.isp.automatic;

import blast.blast.annotator.AnnotationItem;
import blast.blast.annotator.AnnotationUnit;
import blast.blast.annotator.Sentence;

public class ErrorRef {
	
	private Sentence sentence;
	private String type = "ref";
	
	/**
	 * step 1: check missing words at reference
	 * @param sentence 
	 * @param ref 
	 * @return
	 */
	public String addErrorRef(Sentence sentence, String ref) {
		this.sentence = sentence;
		String res = "";
		String[] refList = ref.split(" ");
		for(int i=0; i< refList.length; i++){
			String type = getAlignType(i);
			if(type.equals("")){
				//there is an unaligned word, add deletion error
				res = res + addUnalignedErrorRef(refList[i], i);				
			}
		}		
		return res;
	}

	private String addUnalignedErrorRef(String wordPOS, int i) {
		String out = "";
		if(wordPOS.equals("the/DT"))  // missing the
			out = "-1#-1#" + i + "#A-mis-the ";
		
		else if(wordPOS.equals("a/DT") || wordPOS.equals("an/DT")) // missing a/an
			out = "-1#-1#" + i + "#A-mis-a "; 
		
		else if(wordPOS.endsWith("/DT"))
			out = "-1#-1#" + i + "#A-other "; 
			
		else if(wordPOS.endsWith("/IN")) //proposition deletion
			out = "-1#-1#" + i + "#P-del ";
		
		else if(isPunctuation(wordPOS)) // punctuation
			out = "-1#-1#" + i + "#Punc ";
		
		else {	//word replacement - deletion
			out = "-1#-1#" + i + "#R-del ";
		
		}
		return out;
	}
	
	private boolean isPunctuation(String wordPOS) {
		String[] words = wordPOS.split("/");
		if(words[0].length()==1 && !Character.isLetter(words[0].charAt(0)) && !Character.isDigit(words[0].charAt(0)))
			return true;
		return false;
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
}

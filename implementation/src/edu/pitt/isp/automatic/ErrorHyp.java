package edu.pitt.isp.automatic;

import blast.blast.annotator.AnnotationItem;
import blast.blast.annotator.AnnotationUnit;
import blast.blast.annotator.Sentence;

public class ErrorHyp {
	
	private Sentence sentence;
	private String type = "sys";
	
	/**
	 * step 2: check missing words at hypothesis
	 * @param sentence 
	 * @param sys 
	 * @param ref 
	 * @return
	 */
	public String addErrorHyp(Sentence sentence, String sys) {
		this.sentence = sentence;
		String res = "";
		String[] sysList = sys.split(" ");
		for(int i=0; i< sysList.length; i++){
			String type = getAlignType(i);
			if(type.equals("")){
				//there is an extra word, add insertion error
				res = res + addUnalignedErrorHyp(sysList[i], i);				
			}
		}		
		return res;
	}

	private String addUnalignedErrorHyp(String wordPOS, int i) {
		String out = "";
		if(wordPOS.equals("the/DT"))  // extra the
			out = "-1#" + i + "#-1#A-ex-the ";
		
		else if(wordPOS.equals("a/DT") || wordPOS.equals("an/DT")) // extra a/an
			out = "-1#" + i + "#-1#A-ex-a "; 
		
		else if(wordPOS.endsWith("/DT"))
			out = "-1#" + i + "#-1#A-other "; 
		
		else if(wordPOS.endsWith("/IN")) //proposition insertion
			out = "-1#" + i + "#-1#P-ins ";
		
		else if(isPunctuation(wordPOS)) // punctuation
			out = "-1#" + i + "#-1#Punc ";
		
		else	//word replacement - insertion
			out = "-1#" + i + "#-1#R-ins ";
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

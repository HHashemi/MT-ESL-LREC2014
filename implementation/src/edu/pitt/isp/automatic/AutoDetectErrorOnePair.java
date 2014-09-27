package edu.pitt.isp.automatic;

import blast.blast.annotator.Sentence;

public class AutoDetectErrorOnePair {
	Sentence sentence;
	
	public String detectClassifyError(String src, String ref, String sys, String goodMETEOR) {
		String res = "";
		sentence = new Sentence(src, ref, sys, goodMETEOR, "");
		
		//preprocessing: if a word is to/TO and its next word is not a verb, then we can consider it as an IN (preposition)
		ref = prepocessToPreposition(ref);
		sys = prepocessToPreposition(sys);
		
		//step1: add unaligned words in reference
		res = res + (new ErrorRef()).addErrorRef(sentence, ref);
		
		//step2: add extra words in hypothesis
		res = res + (new ErrorHyp()).addErrorHyp(sentence, sys);
		
		//step3: check the aligned words
		res = (new ErrorRefHyp()).addErrorRefHyp(sentence, ref, sys, res);

		return res.trim();
	}

	/**
	 * only consider to/To and change its POS tag if its next word is not a verb.
	 * @param ref
	 * @param sys
	 */
	private String prepocessToPreposition(String sent) {
		String[] sentList = sent.split(" ");
		
		for(int i=0; i< sentList.length; i++){			
			if(i < sentList.length-1 && sentList[i].equals("to/TO")){
				String nextPOS = getPOS(sentList[i+1]);
				if(!nextPOS.startsWith("VB")){
					sentList[i] = "to/IN";
				}
			}
		}
		String newSent = "";
		for(int i=0; i< sentList.length; i++){	
			newSent = newSent + " " + sentList[i];
		}
		return newSent.trim();
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

}

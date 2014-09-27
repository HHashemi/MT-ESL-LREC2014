package edu.pitt.isp.alignment;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class POStagging {

	public String addPOStagging(List<String> words) {		
		MaxentTagger tagger = new MaxentTagger("./lib/wsj-0-18-left3words-distsim.tagger");
		List<HasWord> sentence = toWordList(words);
	    ArrayList<TaggedWord> tSentence = tagger.tagSentence(sentence);	    
		return Sentence.listToString(tSentence, false);
	}

	public static List<HasWord> toWordList(List<String> words) {
		List<HasWord> sent = new ArrayList<HasWord>();
		for (String word : words) {
			CoreLabel cl = new CoreLabel();
		    cl.setValue(word);
		    cl.setWord(word);
		    sent.add(cl);
		 }
		return sent;
	}
}

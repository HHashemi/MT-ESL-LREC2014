package edu.pitt.isp.alignment;

import blast.meteor.util.MeteorConstants;
import edu.cmu.meteor.aligner.Alignment;
import edu.cmu.meteor.aligner.Match;

public class BlastFormat {

	public String convertMeteorToBlastFormat(Alignment alignment) {
		StringBuilder out = new StringBuilder();
		for (int j = 0; j < alignment.matches.length; j++) {
            Match m = alignment.matches[j];
            if (m != null) {
                out.append("-1#");  // no match in source
                // Second string word
                out.append(m.matchStart);
                for (int i = m.matchStart+1; i < m.matchStart+m.matchLength; i++) {
                    out.append(","+i); 
                }
                out.append("#");
                // First string word
                out.append(m.start);
                for (int i = m.start+1; i < m.start+m.length; i++) {
                    out.append(","+i); //""-"+(m.matchStart+m.matchLength));
                } 
                out.append("#" + MeteorConstants.getBlastModuleName(m.module+1) + " ");
            }
        }		
		return out.toString();
	}

}

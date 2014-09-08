## A Comparison of MT Errors and ESL Errors

This package includes data and code used in our [LREC's paper](http://www.lrec-conf.org/proceedings/lrec2014/pdf/911_Paper.pdf). The paper describes a comparison between machine translation errors and English as a Second Language writers' errors. 
In order to perform MT error analysis on a large sample of translation outputs, we developed a method to automatically identify and extract translation mistakes. 
Also, to verify automatically flagged errors, we manually annotated a subset of the translation outputs.


#### Annotated data
We annotated mistakes of the reported highest-performing and lowest-performing MT systems of [WMT12](http://www.statmt.org/wmt12/results.html) German-English shared task.
We randomly picked 100 reference sentences and then select their corresponding translation outputs from high and low-performing systems. So, we manually annotated totally 200 sentences according to the ESL taxonomy(categories/ESL-vilar-cats).
For manual error analysis, we used [BLAST 1.0](http://www.ida.liu.se/~sarst/blast/) which is an error annotation tool for MT outputs.

The Blast annotation file format is created and read by Blast (as mentioned in its README file):

> The file format is a simple text format. First there is a header with comments and settings, that starts with '#', then each sentence is represented by five lines:
>  1: Source sentence (left blank if no source)
>  2: Reference sentence (left blank if no reference)
>  3: System sentence
>  4: Support annotations
>  5: Error annotations

> Annotations are a quadruple consisting of index/indeces for source, reference and systems, and the type of the annotations, separated by '#', e.g. -1#16#12#e -1#9#-1#W-sense-dis-PRO. '-1' represents that no index is marked in that sentences


#### Citation
If you use this data set please cite this article by Hashemi and Hwa.

> Homa B. Hashemi and Rebecca Hwa. [A Comparison of MT Errors and ESL Errors](http://www.lrec-conf.org/proceedings/lrec2014/pdf/911_Paper.pdf). In proceedings of the 9th edition of the Language Resources and Evaluation Conference (LREC), 2014.


#### Contact
If you have any questions, please contact Homa Hashemi, Hashemi (at) cs dot pitt dot edu.
=========================================================
README for NLP Homework 3
by Victoria A. Lestari (val565)
=========================================================
1. Make sure that the file "wsj_02_10.conllx" and the folder "brown-conllx" are in the same directory as the Python script "create_seed.py".

2. Run "create_seed.py" to produce the corresponding files

	> python create_seed.py

	This will create the following folder and files:
	- the folder "wsj", containing:
		- wsj_1000.conllx
		- wsj_2000.conllx
		- wsj_3000.conllx
		- wsj_4000.conllx
		- wsj_5000.conllx
		- wsj_7000.conllx 
		- wsj_10000.conllx
		- wsj_12000.conllx
		- wsj_14000.conllx
	- the files "brown-train1.conllx" to "brown-train8.conllx". These files contain the texts from each Brown folder (i.e. "brown-train1.conllx" contains the texts in the folder "brown-conllx/cf", etc.).
	- the folder "brown", containing:
		- brown_1000.conllx
		- brown_2000.conllx
		- brown_3000.conllx
		- brown_4000.conllx
		- brown_5000.conllx
		- brown_7000.conllx 
		- brown_10000.conllx
		- brown_13000.conllx
		- brown_17000.conllx
		- brown_21000.conllx

3. Compile and run "DependencyParserAPIUsage.java".

Here is how I compiled it:
> javac -cp path/to/stanford-corenlp-full-2016-10-31/stanford-corenlp-3.7.0.jar DependencyParserAPIUsage.java

Here is how I run it:
> java -cp path/to/stanford-corenlp-full-2016-10-31/stanford-corenlp-3.7.0.jar:. DependencyParserAPIUsage

	Supply one command line argument.
	1 - source: wsj, target: wsj, mode: in-domain.
	2 - source: wsj, target: brown, mode: no self-training.
	3 - source: wsj, target: brown, mode: with self-training.
	4 - source: wsj, target: brown, mode: increasing self-training set.
	5 - source: brown, target: brown, mode: in-domain.
	6 - source: brown, target: wsj, mode: no self-training.
	7 - source: brown, target: wsj, mode: with self-training.
	8 - source: brown, target: wsj, mode: increasing self-training set.

	Example: java DependencyParserAPIUsage 1

If you want to change the sentence size, change wsjCases or brownCases to include just one size.

=========================================================
Description for the trace files
=========================================================
- log_wsj_wsj_10000.txt --> trace file for WSJ in-domain
- log_wsj_brown_10000.txt --> trace file for WSJ as source and Brown as target, no self-training
- log_wsj_brown_retrain_10000.txt --> trace file for WSJ as source and Brown as target, with self-training
- log_wsj_brown_retrain_fs_10000.txt --> trace file for WSJ as source and Brown as target, increasing self-training set
- log_brown_brown_10000.txt --> trace file for Brown in-domain
- log_brown_wsj_10000.txt --> trace file for Brown as source and WSJ as target, no self-training
- log_brown_wsj_retrain_10000.txt --> trace file for Brown as source and WSJ as target, with self-training
- log_brown_wsj_retrain_fs_10000.txt --> trace file for Brown as source and WSJ as target, increasing self-training set

PS: I directed the output printed by Stanford parser to these files. For some strange reason, the iteration numbers were not printed on the logs for 10000 sentences, but on the logs for 1000 sentences. Perhaps it was because I did not close the file, so the program printed the iteration numbers on another files while printing the "Percent actually necessary to compute" on these trace files. However, the UAS and LAS are printed in these trace files. 

For the self-training scenarios, I did not save the trace files before self-training either. Usually the LAS was 1% lower than after self-training. 
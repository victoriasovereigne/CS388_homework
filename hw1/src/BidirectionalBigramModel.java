import java.io.*;
import java.util.*;

public class BidirectionalBigramModel extends BigramModel
{
	public BigramModel bm;
	public BackwardBigramModel bbm;
	
	public BidirectionalBigramModel()
	{
		bm = new BigramModel();
		bbm = new BackwardBigramModel();
	}

	public void train(List<List<String>> sentences)
	{
		bm.train(sentences);
		bbm.train(sentences);
	}
	
	/** Like test1 but excludes predicting end-of-sentence when computing perplexity */
	public void test(List<List<String>> sentences) {
		double totalLogProb = 0;
		double totalNumTokens = 0;
		for (List<String> sentence : sentences) {
			totalNumTokens += sentence.size();
			double sentenceLogProb = sentenceLogProb(sentence);
			//	    System.out.println(sentenceLogProb + " : " + sentence);
			totalLogProb += sentenceLogProb;
		}

		// System.out.println("total log prob: " + totalLogProb);
		// System.out.println("total num tokens: " + totalNumTokens);

		double perplexity = Math.exp(-totalLogProb / totalNumTokens);
		System.out.println("Word Perplexity = " + perplexity );
	}
	
	public double sentenceLogProb(List<String> sentence)
	{
		int size = sentence.size();
		
		// the forward token probs include end of sentence at the end of the list
		double[] fwdTokenProbs = bm.sentenceTokenProbs(sentence);
		
		// the backward token probs include start of sentence at the end of the list
		double[] bckTokenProbs = bbm.sentenceTokenProbs(sentence);
		double sentenceLogProb = 0;
		
		for (int i = 0; i < size; i++){
			sentenceLogProb += Math.log((fwdTokenProbs[i] + bckTokenProbs[size-i-1]) / 2);
		}	
		return sentenceLogProb;
	}
	
	
	/** Train and test a bigram model.
	 *  Command format: "nlp.lm.BigramModel [DIR]* [TestFrac]" where DIR 
	 *  is the name of a file or directory whose LDC POS Tagged files should be 
	 *  used for input data; and TestFrac is the fraction of the sentences
	 *  in this data that should be used for testing, the rest for training.
	 *  0 < TestFrac < 1
	 *  Uses the last fraction of the data for testing and the first part
	 *  for training.
	 */
	public static void main(String[] args) throws IOException {
		// System.out.println("BidirectionalBigramModel");
		// All but last arg is a file/directory of LDC tagged input data
		File[] files = new File[args.length - 1];
		for (int i = 0; i < files.length; i++) 
			files[i] = new File(args[i]);
		
		// Last arg is the TestFrac
		double testFraction = Double.valueOf(args[args.length -1]);
		
		// Get list of sentences from the LDC POS tagged input files
		List<List<String>> sentences = 	POSTaggedFile.convertToTokenLists(files);
		int numSentences = sentences.size();
		
		// Compute number of test sentences based on TestFrac
		int numTest = (int)Math.round(numSentences * testFraction);
		
		// Take test sentences from end of data
		List<List<String>> testSentences = sentences.subList(numSentences - numTest, numSentences);
		
		// Take training sentences from start of data
		List<List<String>> trainSentences = sentences.subList(0, numSentences - numTest);
		System.out.println("# Train Sentences = " + trainSentences.size() + 
				" (# words = " + wordCount(trainSentences) + 
				") \n# Test Sentences = " + testSentences.size() +
				" (# words = " + wordCount(testSentences) + ")");
		
		// Create a bigram model and train it.
		BidirectionalBigramModel model = new BidirectionalBigramModel();
		System.out.println("Training...");
		model.train(trainSentences);

		// Test on training data using test
		model.test(trainSentences);
		System.out.println("Testing...");
		
		// Test on test data using test
		model.test(testSentences);	
	}

}
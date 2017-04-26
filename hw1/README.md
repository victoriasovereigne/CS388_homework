To run BackwardBigramModel.java and Bidirectional Bigram Model.java, simply give two arguments in the command line like BigramModel.java. The first argument is the directory of the corpus. The second argument is the proportion of the test set.

Example: 
java BackwardBigramModel ../../brown 0.1
java BidirectionalBigramModel ../../brown 0.1

(I used the relative path "../../brown", but it can also accept absolute paths.)

BackwardBigramModel and BidirectionalBigramModel are subclasses of BigramModel. I only implemented methods that are different from the methods in BigramModel. Otherwise, they will use their superclass' methods. 

I included the classes DoubleValue and POSTaggedFile because the BigramModel, BackwardBigramModel, and BidirectionalBigramModel depend on them. 
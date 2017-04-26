-------------------------------------------
How to run the program
-------------------------------------------
python pos_bilstm.py [corpus] [training_directory] [standard/not] [train/test] [features (p/s/o/-)] [concat_type]

- corpus: wsj
- training_directory: any path for the training directory
- standard/not: if standard, the split is 1-18 for training, 19-21 for validation, 22-24 for testing
- train/test: if 'train', run the program in training mode. If 'test', run the program in testing mode
- features: p = prefix, s = suffix, o = others, - = no features
- concat_type: 'input' (no quotation mark) for concatenating with input layer, 'output' for concatenating with classification layer

Examples:
1) Standard training with prefixes, suffixes, and others as orthographic features, concatenating with input layer
python pos_bilstm.py wsj train_dir standard train pso input

2) Standard testing with prefixes, suffixes, and others as orthographic features, concatenating with input layer
python pos_bilstm.py wsj train_dir standard train pso input

Note: When testing, the directory 'train_dir' must contain model from training.

3) Standard training with prefixes, suffixes, and others as orthographic features, concatenating with classification layer
python pos_bilstm.py wsj train_dir standard train pso output

4) Standard training with other features only (capitalization, hyphen, digit), concatenating with input layer
python pos_bilstm.py wsj train_dir standard train o input

5) Standard training with prefix and suffix as orthographic features
python pos_bilstm.py wsj train_dir standard train ps input

6) Standard training with no orthographic features
python pos_bilstm.py wsj train_dir standard train - -
import glob
from random import shuffle

MAX_LENGTH = 100
PREFIX_LIST = ['ante', 'anti', 'circum', 'co', 'de', 'dis', 'em', 'en', 'epi',
		'ex', 'extra', 'fore', 'homo', 'hyper', 'il', 'im', 'in', 'ir', 'infra',
		'inter', 'intra', 'macro', 'micro', 'mid', 'mis', 'mono', 'non', 'ob', 
		'omni', 'para', 'post', 'pre', 're', 'semi', 'sub', 'super', 'therm', 
		'trans', 'tri', 'un', 'uni']
SUFFIX_LIST = ['able', 'ac', 'acity', 'ocity', 'ade', 'age', 'aholic', 'oholic', 'al', 'algia', 
		'an', 'ian', 'ance', 'ant', 'ar', 'ard', 'arian', 'arium', 'orium', 'ary', 
		'ate', 'ation', 'ative', 'cide', 'cracy', 'crat', 'cule', 'cy', 'cycle', 'dom', 
		'dox', 'ectomy', 'ed', 'ee', 'eer', 'emia', 'en', 'ence', 'ency', 'ent', 
		'er', 'ern', 'escence', 'ese', 'esque', 'ess', 'est', 'etic', 'ette', 'ful', 
		'fy', 'gam', 'gamy', 'gon', 'gonic', 'hood', 'ial', 'ian', 'iasis', 'iatric', 
		'ible', 'ic', 'ical', 'ify', 'ile', 'ily', 'ine', 'ing', 'ion', 'ious', 'ish', 
		'ism', 'ist', 'ite', 'itis', 'ity', 'ive', 'ization', 'ize', 'less', 'let', 
		'like', 'ling', 'loger', 'logist', 'log','ly', 'man','ment', 'ness', 'oid', 'ology', 
		'oma', 'onym', 'opia', 'opsy', 'or', 'ory', 'osis', 'ostomy', 'otomy', 'ous', 
		'path', 'pathy', 'phile', 'phobia', 'phone', 'phyte', 'plegia', 'plegic', 'pnea', 'scopy', 
		'scope', 'scribe', 'script', 'sect', 'ship', 'sion', 'some', 'sophy', 'sophic', 'th', 
		'tion', 'tome', 'tomy', 'trophy', 'tude', 'ty', 'ular', 'uous', 'ure', 'ward', 
		'ware', 'wise', 'woman','y']

class PreprocessData:

	def __init__(self, dataset_type='wsj'):
		self.vocabulary = {}
		self.pos_tags = {}
		self.dataset_type = dataset_type
		self.suffixes = {x:i+1 for i,x in enumerate(SUFFIX_LIST)}
		self.prefixes = {x:i+1 for i,x in enumerate(PREFIX_LIST)}

	## Get standard split for WSJ
	def get_standard_split(self, files):
		if self.dataset_type == 'wsj':
			train_files = []
			val_files = []
			test_files = []
			for file_ in files:
				partition = int(file_.split('/')[-2])
				if partition >= 0 and partition <= 18:
					train_files.append(file_)
				elif partition <= 21:
					val_files.append(file_)
				else:
					test_files.append(file_)
			return train_files, val_files, test_files
		else:
			raise Exception('Standard Split not Implemented for '+ self.dataset_type)

	@staticmethod
	def isFeasibleStartingCharacter(c):
		unfeasibleChars = '[]@\n'
		return not(c in unfeasibleChars)

	## unknown words represented by len(vocab)
	def get_unk_id(self, dic):
		return len(dic)

	def get_pad_id(self, dic):
		return len(self.vocabulary) + 1

	## get id of given token(pos) from dictionary dic.
	## if not in dic, extend the dic if in train mode
	## else use representation for unknown token
	def get_id(self, pos, dic, mode):
		if pos not in dic:
			if mode == 'train':
				dic[pos] = len(dic)
			else:
				return self.get_unk_id(dic)
		return dic[pos]

	def getOrtographicFeatures(self, word):
		# orthographic features
		capitalized = 0
		ending = ''
		suffix_index = 0
		hyphen = 0
		number = 0
		prefix_index = 0

		# find suffix
		for key in self.suffixes.keys():
			if word.endswith(key):
				if len(key) > len(ending):
					ending = key
					suffix_index = self.suffixes[key]

		# find prefix
		for key in self.prefixes.keys():
			if word.startswith(key):
				prefix_index = self.prefixes[key]

		# first letter is capitalized
		if word[0].isupper():
			capitalized = 1

		# first letter is number
		if word[0].isdigit():
			number = 1

		# contains hyphen
		if '-' in word:
			hyphen = 1
		return [prefix_index, suffix_index, capitalized, number, hyphen]

	## Process single file to get raw data matrix
	def processSingleFile(self, inFileName, mode):
		matrix = []
		row = []
		with open(inFileName) as f:
			lines = f.readlines()
			for line in lines:
				line = line.strip()
				if line == '':
					pass
				else:
					tokens = line.split()
					for token in tokens:
						## ==== indicates start of new example					
						if token[0] == '=':
							if row:
								matrix.append(row)
							row = []
							break
						elif PreprocessData.isFeasibleStartingCharacter(token[0]):
							wordPosPair = token.split('/')
							if len(wordPosPair) == 2:
								## get ids for word and pos tag
								feature = self.get_id(wordPosPair[0], self.vocabulary, mode)
								
								# include all pos tags.
								tag = self.get_id(wordPosPair[1], self.pos_tags, 'train')
								ortho = self.getOrtographicFeatures(wordPosPair[0])

								arr = [feature, tag]
								arr.extend(ortho) # append orthographic features

								tup = tuple(arr)
								row.append(tup)
								print (token, arr[0], mode)
		if row:
			matrix.append(row)
		return matrix


	## get all data files in given subdirectories of given directory
	def preProcessDirectory(self, inDirectoryName, subDirNames=['*']):
		if not(subDirNames):
			files = glob.glob(inDirectoryName+'/*.pos')
		else:
			files = [glob.glob(inDirectoryName+ '/' + subDirName + '/*.pos')
					for subDirName in subDirNames]
			files = set().union(*files)
		return list(files)


	## Get basic data matrix with (possibly) variable sized senteces, without padding
	def get_raw_data(self, files, mode):
		matrix = []
		for f in files:
			matrix.extend(self.processSingleFile(f, mode))
		return matrix

	def split_data(self, data, fraction):
		split_index = int(fraction*len(data))
		left_split = data[:split_index]
		right_split = data[split_index:]
		if not(left_split):
			raise Exception('Fraction too small')
		if not(right_split):
			raise Exception('Fraction too big')
		return left_split, right_split

	## Get rid of sentences greater than max_size
	## and pad the remaining if less than max_size
	def get_processed_data(self, mat, max_size):
		X = []
		y = []
		
		prefix = []
		suffix = []
		others = []

		original_len = len(mat)
		mat = filter(lambda x: len(x) <= max_size, mat)
		no_removed = original_len - len(mat)
		for row in mat:
			X_row = [tup[0] for tup in row]
			y_row = [tup[1] for tup in row]

			p_row = [tup[2] for tup in row]
			s_row = [tup[3] for tup in row]
			o_row = [list(tup[4:]) for tup in row]

			## padded words represented by len(vocab) + 1
			X_row = X_row + [self.get_pad_id(self.vocabulary)]*(max_size - len(X_row))
			
			## Padded pos tags represented by -1
			y_row = y_row + [-1]*(max_size-len(y_row))
			
			# Pad suffix represent by -1
			p_row = p_row + [-1]*(max_size-len(p_row))
			s_row = s_row + [-1]*(max_size-len(s_row))
			o_row = o_row + [[-1,-1,-1]]*(max_size-len(o_row))

			X.append(X_row)
			y.append(y_row)
			prefix.append(p_row)
			suffix.append(s_row)
			others.append(o_row)

		return X, y, prefix, suffix, others, no_removed

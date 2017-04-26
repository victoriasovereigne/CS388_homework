import os

filename = "wsj_02_10.conllx"

def count_num_sent(lines):
	return lines.count('\n')

def create_seed(filename, corpus, seed):
	f = open(filename, 'r')
	lines = f.readlines()

	try:
		print("trying to creating directory", corpus)
		os.mkdir(corpus)
		print("directory", corpus,"created")
	except FileExistsError as fee:
		print("directory exists, continue")

	output = open(corpus + '/' + corpus + '_' + str(seed) + '.conllx', 'w')
	num_sent = 0

	for line in lines:
		if line == '\n':
			output.write('\n')
			num_sent += 1
		else:
			output.write(line)

		if num_sent == seed:
			break

	f.close()
	output.close()

create_seed(filename, 'wsj', 1000)
create_seed(filename, 'wsj', 2000)
create_seed(filename, 'wsj', 3000)
create_seed(filename, 'wsj', 4000)
create_seed(filename, 'wsj', 5000)
create_seed(filename, 'wsj', 7000)
create_seed(filename, 'wsj', 10000)
create_seed(filename, 'wsj', 12000)
create_seed(filename, 'wsj', 14000)

folder = 'brown-conllx'

def create_unlabeled_set(folder):
	subfolders = os.listdir(folder)
	test_file = open('brown-test.conllx', 'w')

	# in each subfolder
	for i, subfolder in enumerate(subfolders):
		files = os.listdir(folder + '/' + subfolder)
		train_file = open('brown-train' + str(i+1) + '.conllx', 'w')

		for afile in files:
			f = open(folder + '/' + subfolder + '/' + afile, 'r')

			lines = f.readlines()
			num_sent = count_num_sent(lines)
			train_num = int(0.9 * num_sent)
			tmp = 0

			for line in lines:
				if tmp < train_num:
					train_file.write(line)
				else:
					test_file.write(line)

				if line == '\n':
					tmp += 1

		train_file.close()
	test_file.close()

def distribute_topics(filename):
	tmp = list()
	train_file = open(filename, 'w')
	for i in range(0,8):
		f = open('brown-train' + str(i+1) + '.conllx', 'r')
		lines = f.readlines()
		tmp.append(lines)
		num_sent = count_num_sent(lines)

	num = [0] * 8
	sent = 0
	while sent < 3961:
		if sent < 2831 and num[0] < len(tmp[0]):
			while tmp[0][num[0]] != '\n':
				train_file.write(tmp[0][num[0]])
				num[0] += 1
			train_file.write('\n')
			num[0] += 1

		if sent < 2934 and num[1] < len(tmp[1]):
			while tmp[1][num[1]] != '\n':
				train_file.write(tmp[1][num[1]])
				num[1] += 1
			train_file.write('\n')
			num[1] += 1

		if sent < 3478 and num[2] < len(tmp[2]):
			while tmp[2][num[2]] != '\n':
				train_file.write(tmp[2][num[2]])
				num[2] += 1
			train_file.write('\n')
			num[2] += 1

		if sent < 3332 and num[3] < len(tmp[3]):
			while tmp[3][num[3]] != '\n':
				train_file.write(tmp[3][num[3]])
				num[3] += 1
			train_file.write('\n')
			num[3] += 1

		if sent < 789 and num[4] < len(tmp[4]):
			while tmp[4][num[4]] != '\n':
				train_file.write(tmp[4][num[4]])
				num[4] += 1
			train_file.write('\n')
			num[4] += 1

		if sent < 3961 and num[5] < len(tmp[5]):
			while tmp[5][num[5]] != '\n':
				train_file.write(tmp[5][num[5]])
				num[5] += 1
			train_file.write('\n')
			num[5] += 1

		if sent < 3536 and num[6] < len(tmp[6]):
			while tmp[6][num[6]] != '\n':
				train_file.write(tmp[6][num[6]])
				num[6] += 1
			train_file.write('\n')
			num[6] += 1

		if sent < 867 and num[7] < len(tmp[7]):			
			while tmp[7][num[7]] != '\n':
				train_file.write(tmp[7][num[7]])
				num[7] += 1
			train_file.write('\n')
			num[7] += 1

		sent += 1

	train_file.close()

filename = "brown-train_new.conllx"	
distribute_topics(filename)
create_unlabeled_set(folder)

create_seed(filename, 'brown', 1000)
create_seed(filename, 'brown', 2000)
create_seed(filename, 'brown', 3000)
create_seed(filename, 'brown', 4000)
create_seed(filename, 'brown', 5000)
create_seed(filename, 'brown', 7000)
create_seed(filename, 'brown', 10000)
create_seed(filename, 'brown', 13000)
create_seed(filename, 'brown', 17000)
create_seed(filename, 'brown', 21000)
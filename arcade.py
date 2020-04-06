# -*- coding: utf-8 -*-
import pandas as pd
from structlog import get_logger
from itertools import combinations


logger = get_logger()
MAX = {
	'sum_amount': 0,
	'sum_time': 0,
	'games': []
}
MAX_TIME = 120


def load_file(filepath):
	logger.info("Loading CSV file: " + filepath)
	df = pd.read_csv(filepath)
	df['PAYOUT_RATE'] = df['PAYOUT_RATE'].apply(lambda x: x.replace("$", ""))
	return(df)


def process_file(filepath):
	df = load_file(filepath)

	# list from 0 to the size of the dataset
	mylist = range(0, len(df))
	for i in mylist:
		for p in combinations(mylist, i+1):
			sum_amount = 0
			sum_time = 0
			games = []

			for index in p:
				time = int(df.iloc[index]["COMPLETION_TIME (in minutes)"])
				game = df.iloc[index]["GAME"]
				amount = int(df.iloc[index]["PAYOUT_RATE"])

				if sum_time + time > MAX_TIME:
					continue

				sum_amount += amount
				sum_time += time
				games.append(game)

			if sum_amount > MAX["sum_amount"]:
				MAX.update({
					'sum_amount': sum_amount,
					'sum_time': sum_time,
					'games': games
				})
	print(MAX)


if __name__ == '__main__':
	import argparse

	parser = argparse.ArgumentParser(description='Actors Ranker')
	parser.add_argument('filepath', metavar='<filepath>', type=str, nargs=1,
	                    help='Path to CSV file')
	args = parser.parse_args()
	process_file(args.filepath[0])
# -*- coding: utf-8 -*-
import pandas as pd
from structlog import get_logger

logger = get_logger()


def process_file(filepath):
	logger.info("Loading CSV file: " + filepath)
	df = pd.read_csv(filepath)
	print(
		df.groupby("ACTOR_NAME")
		  .agg(RANK=pd.NamedAgg(column="ACTOR_NAME", aggfunc="count"))
		  .sort_values(['RANK'], ascending=False).head(10)
	)

if __name__ == '__main__':
	import argparse

	parser = argparse.ArgumentParser(description='Actors Ranker')
	parser.add_argument('filepath', metavar='<filepath>', type=str, nargs=1,
	                    help='Path to CSV file')
	args = parser.parse_args()
	process_file(args.filepath[0])
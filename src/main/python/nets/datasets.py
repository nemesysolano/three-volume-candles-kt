import directories
import pandas as pd
import os
from multiprocessing import pool, cpu_count
from pandarallel import pandarallel
import datetime
import tensorflow as tf

pandarallel.initialize(progress_bar=True)

# https://www.tensorflow.org/api_docs/python/tf/keras/utils/load_img
def add_plot(row, symbol, timeframe, symbol_plots_dir):
    file_path = os.path.join(symbol_plots_dir, "%s-%s-reversions-%s.png" %(symbol, timeframe, row['datetime'].strftime("%Y%m%d%H%M%S")))    
    return None if not os.path.exists(file_path) else tf.keras.utils.load_img(file_path)

def load_reversions_with_images(symbol, timeframe):
    symbol_file_name = "%s-%s-reversions.csv"%(symbol, timeframe)
    symbol_plots_dir = os.path.join(directories.PLOTS_DIR, "%s-%s-reversions" %(symbol, timeframe))
    symbol_data_file = os.path.join(directories.DATA_DIR, symbol_file_name)
    symbol_data = pd.read_csv(symbol_data_file, parse_dates=['datetime'])
    plot = symbol_data.parallel_apply(lambda row: add_plot(row, symbol, timeframe, symbol_plots_dir), axis=1)
    symbol_data['plot'] = plot
    symbol_data.set_index('datetime', inplace=True)
    return symbol_data
    
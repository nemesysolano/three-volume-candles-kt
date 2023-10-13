import directories
import pandas as pd
import os
from multiprocessing import pool, cpu_count
from pandarallel import pandarallel
import tensorflow as tf
import cv2
LOOKBACK_PERIOD = 5

pandarallel.initialize(progress_bar=True)

# https://www.tensorflow.org/api_docs/python/tf/keras/utils/load_img
def add_plot(row, symbol, timeframe, symbol_plots_dir):
    file_path = os.path.join(symbol_plots_dir, "%s-%s-reversions-%s.png" %(symbol, timeframe, row['datetime'].strftime("%Y%m%d%H%M%S")))    
    plot = None if not os.path.exists(file_path) else tf.keras.utils.img_to_array(tf.keras.utils.load_img(file_path)) / 255
    return (row['datetime'], row[-1], plot)

def load_reversions_with_images(symbol, timeframe):
    symbol_file_name = "%s-%s-reversions.csv"%(symbol, timeframe)
    symbol_plots_dir = os.path.join(directories.PLOTS_DIR, "%s-%s-reversions" %(symbol, timeframe))
    symbol_data_file = os.path.join(directories.DATA_DIR, symbol_file_name)
    inference_data =  pd.read_csv(symbol_data_file, parse_dates=['datetime']).iloc[LOOKBACK_PERIOD:,:].parallel_apply(lambda row: add_plot(row, symbol, timeframe, symbol_plots_dir), axis=1)
    return pd.DataFrame.from_records(inference_data.values, columns=('datetime','direction','plot'), index='datetime')

def split_dataset(dataset):
    test_size = 0.8
    border = int(test_size * len(dataset))

    train, test = (dataset.iloc[border:,:], dataset.iloc[:border,:])
    
    return (
        test[['plot']], 
        test[['direction']], 
        train[['plot']], 
        train[['direction']]
    ) # 2018-01-02 02:00:00
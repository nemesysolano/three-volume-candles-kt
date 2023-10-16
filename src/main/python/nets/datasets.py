import directories
import pandas as pd
import os
from multiprocessing import pool, cpu_count
from pandarallel import pandarallel
import tensorflow as tf
from sklearn.utils import shuffle
import numpy as np

LOOKBACK_PERIOD = 5
NUM_CLASSES = 3

pandarallel.initialize(progress_bar=True)

def direction_to_int(direction):
    if direction == "down":
        return -1
    elif direction == "up":
        return 1
    return 0

# https://www.tensorflow.org/api_docs/python/tf/keras/utils/load_img
def add_plot(row, symbol_plots_dir):
    file_path = os.path.join(symbol_plots_dir, "%s.png" %(row['datetime'].strftime("%Y%m%d%H%M%S")))    
    plot = None if not os.path.exists(file_path) else tf.keras.applications.vgg16.preprocess_input(tf.keras.utils.img_to_array(tf.keras.utils.load_img(file_path)))
    direction = direction_to_int(row[-1].strip())
    return (direction, plot)

def load_reversions_with_images(symbol, timeframe):
    symbol_file_name = "%s-%s-reversions.csv"%(symbol, timeframe)
    symbol_plots_dir = os.path.join(directories.PLOTS_DIR, "%s-%s" %(symbol, timeframe))
    symbol_data_file = os.path.join(directories.DATA_DIR, symbol_file_name)
    inference_data =  pd.read_csv(symbol_data_file, parse_dates=['datetime']).iloc[LOOKBACK_PERIOD:,:].parallel_apply(lambda row: add_plot(row, symbol_plots_dir), axis=1)
    return pd.DataFrame.from_records(inference_data.values, columns=('direction','plot')).dropna()

def split_dataset(dataset):
    test_size = 0.8
    border = int(test_size * len(dataset))

    train, test = (dataset.iloc[:border], dataset.iloc[border:])

    return (
        {
            'plot': tf.convert_to_tensor(np.array(train['plot'].tolist()),  np.float32) , 
            'direction': tf.keras.utils.to_categorical(train['direction'], NUM_CLASSES)
        },
        {
            'plot': tf.convert_to_tensor(np.array(test['plot'].tolist()), np.float32) ,
            'direction': tf.keras.utils.to_categorical(test['direction'], NUM_CLASSES)
        },
    )
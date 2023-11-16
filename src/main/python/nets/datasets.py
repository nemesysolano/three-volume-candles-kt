from environment import PLOTS_DIR, DATA_DIR
import pandas as pd
import os
from pandarallel import pandarallel
import tensorflow as tf
import numpy as np
from multiprocessing import cpu_count, Pool

LOOKBACK_PERIOD = 18
NUM_CLASSES = 3
symbols = ("AUDUSD", "EURUSD", "GBPUSD", "USDCAD", "USDJPY")
columns = ('direction', 'plot')

pandarallel.initialize(progress_bar=True)

ident_preprocessor = lambda x: x


# https://www.tensorflow.org/api_docs/python/tf/keras/utils/load_img
def add_plot(row, symbol_plots_dir, preprocessor=ident_preprocessor):
    file_path = os.path.join(symbol_plots_dir, "%s.png" % (row['datetime'].strftime("%Y%m%d%H%M%S")))
    plot = preprocessor(tf.keras.utils.img_to_array(tf.keras.utils.load_img(file_path)))
    direction = int(row[-1])
    return direction, plot

def load_reversions_with_images(symbol, timeframe, train_validate_percent=0.90, preprocessor=ident_preprocessor):
    symbol_file_name = "%s-%s-reversions.csv" % (symbol, timeframe)
    symbol_plots_dir = os.path.join(PLOTS_DIR, "%s-%s" % (symbol, timeframe))
    symbol_data_file = os.path.join(DATA_DIR, symbol_file_name)
    dataset = pd.read_csv(symbol_data_file, parse_dates=['datetime'])
    dataset = dataset.head(len(dataset) - (LOOKBACK_PERIOD - 1))
    dataset.drop(index=dataset.index[:(LOOKBACK_PERIOD-1)], axis=0, inplace=True)
    inference_data = dataset.parallel_apply(lambda row: add_plot(row, symbol_plots_dir, preprocessor), axis=1)
    values = inference_data.values
    train_validate_end = int(train_validate_percent * len(inference_data))

    return (
        values[:train_validate_end],
        values[train_validate_end:],
        len(inference_data)
    )

def get_plot(v):
    return v[1]

def get_plots(values):
    pool = Pool(cpu_count())
    return pool.map(get_plot, values)

def load_reversions_with_images_for_backtesting(symbol, timeframe, train_validate_percent=0.90, preprocessor=ident_preprocessor):
    symbol_file_name = "%s-%s-reversions.csv" % (symbol, timeframe)
    symbol_plots_dir = os.path.join(PLOTS_DIR, "%s-%s" % (symbol, timeframe))
    symbol_data_file = os.path.join(DATA_DIR, symbol_file_name)
    dataset = pd.read_csv(symbol_data_file, parse_dates=['datetime'])
    dataset = dataset.head(len(dataset) - (LOOKBACK_PERIOD - 1))
    inference_data = dataset.parallel_apply(lambda row: add_plot(row, symbol_plots_dir, preprocessor), axis=1)
    values = inference_data.values
    train_validate_end = int(train_validate_percent * len(inference_data))

    backtesting_data = dataset[train_validate_end:].copy()
    backtesting_data['plot'] = get_plots(values[train_validate_end:])
    backtesting_data.rename(columns=lambda x: x.strip(), inplace=True)
    return backtesting_data

def load_reversions_with_images_for_timeframe(timeframe, train_validate_percent=0.9, preprocessor=ident_preprocessor):
    train_validate_datasets = list()
    test_datasets = list()
    inference_data_len_total = 0

    for symbol in symbols:
        train_validate_dataset, test_dataset, inference_data_len = load_reversions_with_images(
            symbol,
            timeframe,
            train_validate_percent,
            preprocessor=preprocessor
        )
        assert train_validate_dataset.shape[0] > test_dataset.shape[0]

        train_validate_datasets.extend(train_validate_dataset)
        test_datasets.extend(test_dataset)

        inference_data_len_total += inference_data_len

    train_validate_joined = pd.DataFrame(train_validate_datasets, columns=columns)
    test_joined = randomize_dataset(pd.DataFrame(test_datasets, columns=columns))
    assert train_validate_joined.shape[0] > test_joined.shape[0]
    assert train_validate_joined.shape[0] + test_joined.shape[0] == inference_data_len_total

    return create_tensors_dictionary(train_validate_joined, test_joined)

def create_tensors_dictionary(train_validate, test, train_percent=0.8):
    train_end = int(len(train_validate) * train_percent)
    train = randomize_dataset(train_validate.iloc[:train_end].copy())
    validate = randomize_dataset(train_validate.iloc[train_end:].copy())

    assert len(train) + len(validate) == len(train_validate)
    assert len(train) > len(validate)
    return (
        {
            'plot': tf.convert_to_tensor(np.array(train['plot'].tolist()), np.float32),
            'direction': tf.keras.utils.to_categorical(train['direction'], NUM_CLASSES)
        },
        {
            'plot': tf.convert_to_tensor(np.array(validate['plot'].tolist()), np.float32),
            'direction': tf.keras.utils.to_categorical(validate['direction'], NUM_CLASSES)
        },
        {
            'plot': tf.convert_to_tensor(np.array(test['plot'].tolist()), np.float32),
            'direction': tf.keras.utils.to_categorical(test['direction'], NUM_CLASSES)
        },
    )


def randomize_dataset(dataset, seed=None):
    np.random.seed(seed)
    perm = np.random.permutation(len(dataset))
    return dataset.iloc[perm]

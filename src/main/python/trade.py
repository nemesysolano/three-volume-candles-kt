import tensorflow as tf
import argparse
import numpy as np
from sklearn.metrics import confusion_matrix
from nets import load_reversions_with_images_for_backtesting, NUM_CLASSES, load_model
from nets import symbols

def preliminary_report(model, backtesting_data):
    print(backtesting_data)
    x = tf.convert_to_tensor(np.array(backtesting_data['plot'].tolist()), np.float32)
    y_test = tf.keras.utils.to_categorical(backtesting_data['direction'], NUM_CLASSES)
    y_hat = np.round(model.predict(x)).astype(np.int32)
    y_true = np.argmax(y_test.astype(np.int32), axis=1) - 1
    y_pred = np.argmax(y_hat.astype(np.int32), axis=1) - 1
    position_distribution = confusion_matrix(y_true, y_pred)
    print(position_distribution)
    y_true_dist = np.array(
        (np.count_nonzero(y_true == -1), np.count_nonzero(y_true == 0), np.count_nonzero(y_true == 1))).reshape(3, 1)
    print(y_true_dist)

    position_probabilities = position_distribution / y_true_dist
    print(position_probabilities)

if __name__ == "__main__":
    # display data on the MetaTrader 5 package  
    parser = argparse.ArgumentParser()
    parser.add_argument('timeframe')
    parser.add_argument('symbol')
    args = parser.parse_args()

    timeframe = args.timeframe
    symbol = args.symbol

    backtesting_data = load_reversions_with_images_for_backtesting(symbol, timeframe,
                                                                   preprocessor=tf.keras.applications.mobilenet.preprocess_input)
    model, probabilities = load_model(timeframe)
    preliminary_report(model,backtesting_data)

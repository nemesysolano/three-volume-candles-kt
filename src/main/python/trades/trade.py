from sklearn.metrics import confusion_matrix
import numpy as np
import os
import tensorflow as tf
import argparse
from nets.datasets import NUM_CLASSES
from nets import load_model, load_reversions_with_images_for_backtesting
from sklearn.svm import SVC
from sklearn.metrics import confusion_matrix
from nets import symbols


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('timeframe')
    args = parser.parse_args()
    timeframe = args.timeframe    
    model, probabilities = load_model(timeframe)  

    for symbol in symbols:
        price_dataset, indicator = load_reversions_with_images_for_backtesting(symbol, timeframe)
        ##y_expected =  tf.keras.utils.to_categorical(price_dataset['direction'], NUM_CLASSES)

        print()
        print("EXPECTATION FOR %s" % (symbol, ))
        print("======================")
        y_test = tf.keras.utils.to_categorical(indicator['direction'], NUM_CLASSES)
        x = tf.convert_to_tensor(np.array(indicator['plot'].tolist()), np.float32)
        y_hat = model.predict(x)

        y_true = np.argmax(y_test.astype(np.int32), axis=1)-1
        y_pred = np.argmax(y_hat.astype(np.int32), axis=1)-1

        position_distribution = confusion_matrix(y_true, y_pred)
        print(position_distribution)
        y_true_dist = np.array((np.count_nonzero(y_true == -1), np.count_nonzero(y_true == 0), np.count_nonzero(y_true == 1))).reshape(3,1)
        print(y_true_dist)

        position_probabilities = position_distribution / y_true_dist
        print(position_probabilities)


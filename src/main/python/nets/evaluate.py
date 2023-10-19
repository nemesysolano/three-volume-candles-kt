from sklearn.metrics import confusion_matrix
import numpy as np
import os
import tensorflow as tf
import argparse
import nets.modelIO as modelIO
from sklearn.svm import SVC
from sklearn.metrics import confusion_matrix

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('timeframe')
    args = parser.parse_args()
    timeframe = args.timeframe
    model, x, y_hat, y_test = modelIO.load_model(timeframe)    
    
    evaluation = model.evaluate(x, y_test)
    print(evaluation)
    categories = (-1, 0, 1)
    y_true = np.argmax(y_test.astype(np.int32), axis=1)-1
    y_pred = np.argmax(y_hat.astype(np.int32), axis=1)-1

    position_distribution = confusion_matrix(y_true, y_pred)
    print(position_distribution)
    y_true_dist = np.array((np.count_nonzero(y_true == -1), np.count_nonzero(y_true == 0), np.count_nonzero(y_true == 1))).reshape(3,1)
    y_pred_dist = np.array((np.count_nonzero(y_true == -1), np.count_nonzero(y_true == 0), np.count_nonzero(y_true == 1))).reshape(3,1)
    print(y_true_dist)

    print(position_distribution / y_true_dist)


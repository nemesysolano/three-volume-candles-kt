from sklearn.metrics import confusion_matrix
import numpy as np
import os
import tensorflow as tf
import argparse
import nets.modelIO as modelIO

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('timeframe')
    args = parser.parse_args()
    timeframe = args.timeframe
    model, x, y_hat, y_test = modelIO.load_model(timeframe)    
    
    evaluation = model.evaluate(x, y_test)
    print(evaluation)
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
    model, probabilities = modelIO.load_model(timeframe)    
    model.summary()
    print(probabilities)
    

    
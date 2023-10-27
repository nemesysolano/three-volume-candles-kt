from sklearn.metrics import confusion_matrix
import numpy as np
import os
import tensorflow as tf
import argparse
from nets import load_model
from sklearn.svm import SVC
from sklearn.metrics import confusion_matrix

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('timeframe')
    args = parser.parse_args()
    timeframe = args.timeframe    
    model, probabilities = load_model(timeframe)    
    model.summary()
    print(probabilities)
    

    
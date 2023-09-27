import pandas as pd
import numpy as np
import tensorflow as tf
import keras
from keras import layers
import argparse
import csv


PREDICTORS = ['prev3_group_weight', 'prev2_group_weight', 'prev1_group_weight', 'prev3_price_delta', 'prev2_price_delta', 'prev1_price_delta' ]
TARGET = ['direction']
PREDICTORS_SIZE = len(PREDICTORS)

def create_model():
    model = keras.Sequential(
        [
            layers.Dense(PREDICTORS_SIZE, activation="tanh", name="layer1"),
            layers.Dense(PREDICTORS_SIZE, activation="tanh", name="layer2"),
            layers.Dense(PREDICTORS_SIZE, activation="tanh", name="layer3"),
            layers.Dense(int(PREDICTORS_SIZE / 2), activation="tanh", name="layer4"),
            layers.Dense(int(PREDICTORS_SIZE / 3), activation="tanh",name="layer5"),
            layers.Dense(1, activation="tanh",name="output"),
        ]
    )

    model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])
    return model

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('-t', '--train', type=str)
    parser.add_argument('-v', '--verify', type=str)
    parser.add_argument('-o', '--output', type=str)

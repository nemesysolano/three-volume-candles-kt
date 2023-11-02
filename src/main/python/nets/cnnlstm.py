from sklearn.feature_selection import SequentialFeatureSelector
from sklearn.metrics import confusion_matrix
import datasets
import tensorflow as tf
import numpy as np
import argparse
from environment import remove_dir, MODELS_DIR, checkpoint_file
import modelIO
import os

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    # display data on the MetaTrader 5 package  
    parser = argparse.ArgumentParser()
    parser.add_argument('timeframe')
    args = parser.parse_args()
    timeframe = args.timeframe

    train, validate, test= datasets.load_reversions_with_images_for_timeframe( timeframe, preprocessor=tf.keras.applications.mobilenet.preprocess_input)    

    #create model
    IMSIZE = input_shape=train['plot'][0].shape
    N_CLASSES = datasets.NUM_CLASSES
    BATCH_SIZE = 50

    #add model layers
    in_shape = (BATCH_SIZE, IMSIZE[0], IMSIZE[1], 3)
    model = tf.keras.models.Sequential()
    model.add(tf.keras.layers.ConvLSTM2D(8, kernel_size=(7, 7), padding='valid', return_sequences=True, input_shape=in_shape))
    model.add(tf.keras.layers.Activation('relu'))
    model.add(tf.keras.layers.MaxPooling3D(pool_size=(1, 2, 2)))
    model.add(tf.keras.layers.ConvLSTM2D(16, kernel_size=(5, 5), padding='valid', return_sequences=True))
    model.add(tf.keras.layers.MaxPooling3D(pool_size=(1, 2, 2)))
    model.add(tf.keras.layers.ConvLSTM2D(24, kernel_size=(3, 3), padding='valid', return_sequences=True))
    model.add(tf.keras.layers.Activation('relu'))
    model.add(tf.keras.layers.ConvLSTM2D(24, kernel_size=(3, 3), padding='valid', return_sequences=True))
    model.add(tf.keras.layers.Activation('relu'))
    model.add(tf.keras.layers.ConvLSTM2D(24, kernel_size=(3, 3), padding='valid', return_sequences=True))
    model.add(tf.keras.layers.MaxPooling3D(pool_size=(1, 2, 2)))
    model.add(tf.keras.layers.Dense(128))
    model.add(tf.keras.layers.Activation('relu'))
    model.add(tf.keras.layers.Dropout(0.5))
     
    out_shape = model.output_shape
    # print('====Model shape: ', out_shape)
    model.add(tf.keras.layers.Reshape((BATCH_SIZE, out_shape[2] * out_shape[3] * out_shape[4])))
    model.add(tf.keras.layers.LSTM(datasets.LOOKBACK_PERIOD, return_sequences=False))
    model.add(tf.keras.layers.Dropout(0.5))
    model.add(tf.keras.layers.Dense(N_CLASSES, activation='softmax'))
    print(model.summary())
   


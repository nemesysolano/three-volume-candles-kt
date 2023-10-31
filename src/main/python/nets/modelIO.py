from sklearn.metrics import confusion_matrix
import numpy as np
import os
from environment import models_directory, checkpoint_file
import tensorflow as tf
import json

def save_model(timeframe, model, x, y_test):
    model_directory_path = models_directory(timeframe)
    model_file_path = os.path.join(model_directory_path, "model.h5")
    probabilities_file_path = os.path.join(model_directory_path, "probabilities.csv")
    checkpoint_file_path = checkpoint_file(timeframe)
    
    if not os.path.exists(model_directory_path):
        os.makedirs(model_directory_path)

    model.load_weights(checkpoint_file_path)
    model.save(model_file_path, save_format='h5') # h5 format

    y_hat = np.round(model.predict(x)).astype(np.int32)  
    y_true = np.argmax(y_test.astype(np.int32), axis=1)-1
    y_pred = np.argmax(y_hat.astype(np.int32), axis=1)-1
    position_distribution = confusion_matrix(y_true, y_pred)
    print(position_distribution)
    y_true_dist = np.array((np.count_nonzero(y_true == -1), np.count_nonzero(y_true == 0), np.count_nonzero(y_true == 1))).reshape(3,1)
    print(y_true_dist)

    position_probabilities = position_distribution / y_true_dist
    print(position_probabilities)
    np.savetxt(probabilities_file_path, position_probabilities, delimiter=',')


def load_model(timeframe):
    model_directory_path = models_directory(timeframe)
    model_file_path = os.path.join(model_directory_path, "model.h5")
    probabilities_file_path = os.path.join(model_directory_path, "probabilities.csv")
    model = tf.keras.models.load_model(model_file_path)
    checkpoint_file_path = checkpoint_file(timeframe)
    model.load_weights(checkpoint_file_path)
    
    position_probabilities = np.loadtxt(probabilities_file_path, delimiter=',')

    return model, position_probabilities
from sklearn.metrics import confusion_matrix
import numpy as np
import os
from environment import models_directory
import tensorflow as tf
import json

def save_model(timeframe, model, x, y_test):
    model_directory_path = models_directory(timeframe)
    model_file_path = os.path.join(model_directory_path, "model.json")
    weights_file_path = os.path.join(model_directory_path, "weights.h5")
    probabilities_file_path = os.path.join(model_directory_path, "probabilities.csv")
    y_hat = np.round(model.predict(x)).astype(np.int32)

    if not os.path.exists(model_directory_path):
        os.makedirs(model_directory_path)

    assert len(y_hat) == len(y_test)

    with open(model_file_path,'w') as json_file:
        json_file.write(model.to_json())        
    model.save_weights(weights_file_path,save_format='h5') # h5 format
    
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
    model_directory_path = os.path.join(directories.MODELS_DIR, timeframe)
    model_file_path = os.path.join(model_directory_path, "model.json")
    weights_file_path = os.path.join(model_directory_path, "weights.h5")
    probabilities_file_path = os.path.join(model_directory_path, "probabilities.csv")

    with open(model_file_path,'r') as json_file:
        model = tf.keras.models.model_from_json(json_file.read())

    model.load_weights(weights_file_path) # h5 format
    model.compile(
        optimizer='adam',
        loss='categorical_crossentropy',
        metrics=['accuracy'],
    )    
    

    position_probabilities = np.loadtxt(probabilities_file_path, delimiter=',')

    return model, position_probabilities
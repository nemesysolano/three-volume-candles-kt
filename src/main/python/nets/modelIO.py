from sklearn.metrics import confusion_matrix
import numpy as np
import os
import directories
import tensorflow as tf
import json

def save_model(timeframe, model, x, y_hat, y_test):
    model_directory_path = os.path.join(directories.MODELS_DIR, timeframe)
    model_file_path = os.path.join(model_directory_path, "model.json")
    weights_file_path = os.path.join(model_directory_path, "weights.h5")
    y_hat_file_path = os.path.join(model_directory_path, "y_hat.csv")
    y_test_file_path = os.path.join(model_directory_path, "y_test.csv")
    x_test_file_path = os.path.join(model_directory_path, "x_test.npy")

    if not os.path.exists(model_directory_path):
        os.makedirs(model_directory_path)

    assert len(y_hat) == len(y_test)

    with open(model_file_path,'w') as json_file:
        json_file.write(model.to_json())        
    model.save_weights(weights_file_path,save_format='h5') # h5 format

    np.savetxt(y_hat_file_path, y_hat, delimiter=',')
    np.savetxt(y_test_file_path, y_test, delimiter=',')
    with open(x_test_file_path, 'wb') as x_file:
        np.save(x_file, x.numpy(), allow_pickle=True)
    
def load_model(timeframe):
    model_directory_path = os.path.join(directories.MODELS_DIR, timeframe)
    model_file_path = os.path.join(model_directory_path, "model.json")
    weights_file_path = os.path.join(model_directory_path, "weights.h5")
    y_hat_file_path = os.path.join(model_directory_path, "y_hat.csv")
    y_test_file_path = os.path.join(model_directory_path, "y_test.csv")
    x_test_file_path = os.path.join(model_directory_path, "x_test.npy")


    with open(model_file_path,'r') as json_file:
        model = tf.keras.models.model_from_json(json_file.read())

    model.load_weights(weights_file_path) # h5 format
    model.compile(
        optimizer='adam',
        loss='categorical_crossentropy',
        metrics=['accuracy'],
    )    
    model.summary()


    y_hat = np.loadtxt(y_hat_file_path, delimiter=',')
    y_test =np.loadtxt(y_test_file_path, delimiter=',')
    with open(x_test_file_path, 'rb') as x_file:
        x = tf.convert_to_tensor(np.load(x_file, allow_pickle=True))
    return model, x, y_hat, y_test
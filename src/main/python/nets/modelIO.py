from sklearn.metrics import confusion_matrix
import numpy as np
import os
import directories
import tensorflow as tf

def save_model(timeframe, model, y_hat, y_test):
    model_directory_path = os.path.join(directories.MODELS_DIR, timeframe)
    model_file_path = os.path.join(model_directory_path, "model.keras")
    y_hat_file_path = os.path.join(model_directory_path, "y_hat.csv")
    y_test_file_path = os.path.join(model_directory_path, "y_test.csv")

    if not os.path.exists(model_directory_path):
        os.makedirs(model_directory_path)

    tf.keras.saving.save_model(
        model, model_file_path, overwrite=True
    )

    np.savetxt(y_hat_file_path, y_hat, delimiter=',')
    np.savetxt(y_test_file_path, y_test, delimiter=',')
    
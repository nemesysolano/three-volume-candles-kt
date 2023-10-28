import os
from pathlib import Path
import shutil

ROOT_DIR=os.getcwd()
PLOTS_DIR=os.path.join(ROOT_DIR, "plots")
DATA_DIR=os.path.join(ROOT_DIR, "data")
MODELS_DIR=os.path.join(ROOT_DIR, "models")

def remove_dir(path):
    if (os.path.exists(path)):
        shutil.rmtree(path)

def models_directory(timeframe):
     model_directory_path = os.path.join(MODELS_DIR, timeframe)
     return model_directory_path

def checkpoint_file(timeframe):
     return os.path.join(models_directory(timeframe),"checkpoint.dat")

def model_file(timeframe):
     return os.path.join(models_directory(timeframe), "model.h5")

def probabilities_file(timeframe):
    return os.path.join(models_directory(timeframe), "probabilities.csv")
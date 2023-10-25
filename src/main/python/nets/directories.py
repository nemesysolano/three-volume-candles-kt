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
import datasets
import directories
import tensorflow as tf
import numpy as np

NUM_CLASSES = 3
if __name__ == "__main__":
    print(directories.DATA_DIR)
    print(directories.PLOTS_DIR)
    test_plot, test_direction, train_plot, train_direction = datasets.split_dataset(datasets.load_reversions_with_images("USDJPY", "M30"))
    print(test_plot.iloc[0]['plot'].shape, train_plot.iloc[0]['plot'].shape)
    
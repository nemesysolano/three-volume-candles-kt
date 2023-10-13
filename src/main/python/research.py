import tensorflow as tf
import matplotlib.pyplot as plt
#  LOAD AND SPLIT DATASET
(train_images, train_labels), (test_images, test_labels) = tf.keras.datasets.cifar10.load_data()
# Normalize pixel values to be between 0 and 1
train_images, test_images = train_images / 255.0, test_images / 255.0
class_names = ['airplane', 'automobile', 'bird', 'cat', 'deer','dog', 'frog', 'horse', 'ship', 'truck']
IMG_INDEX = 7  # change this to look at other images
print(train_images.shape)
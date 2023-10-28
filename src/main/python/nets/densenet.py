import datasets
import main.python.environment.directories as directories
import tensorflow as tf
import numpy as np
import argparse
import datetime
# https://www.programcreek.com/python/example/120433/keras.applications.densenet.DenseNet121
if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    print(directories.DATA_DIR)
    print(directories.PLOTS_DIR)
    # display data on the MetaTrader 5 package    
    parser = argparse.ArgumentParser()
    parser.add_argument('timeframe')
    parser.add_argument('version')
    args = parser.parse_args()
    timeframe = args.timeframe
    version = int(args.version)

    train, test= datasets.create_tensors_dictionary(datasets.load_reversions_with_images_for_timeframe( timeframe))
    print(train['plot'].shape, test['plot'].shape, test['direction'].shape)

    if version == 121:        
        base_model = tf.keras.applications.densenet.DenseNet121(weights="imagenet", include_top=False, input_shape=train['plot'][0].shape)
    elif version == 169:
        base_model = tf.keras.applications.densenet.DenseNet169(weights="imagenet", include_top=False, input_shape=train['plot'][0].shape)
    elif version == 201:
        base_model = tf.keras.applications.densenet.DenseNet201(weights="imagenet", include_top=False, input_shape=train['plot'][0].shape)

    base_model.trainable = False ## Not trainable weights

    global_average_pooling_2D = tf.keras.layers.Flatten() 
    dense_layer_1 = tf.keras.layers.Dense(512,activation='relu')
    dense_layer_2 = tf.keras.layers.Dense(256,activation='relu')
    dense_layer_3 = tf.keras.layers.Dense(128,activation='relu')
    prediction_layer = tf.keras.layers.Dense(datasets.NUM_CLASSES, activation='softmax')

    model = tf.keras.models.Sequential([
        base_model,
        global_average_pooling_2D,
        dense_layer_1,
        dense_layer_2,
        dense_layer_3,
        prediction_layer
    ])

    model.summary()

    model.compile(
        optimizer='adam',
        loss='categorical_crossentropy',
        metrics=['accuracy'],
    )

    earlyStopping = tf.keras.callbacks.EarlyStopping(monitor='val_accuracy', mode='max', patience=5,  restore_best_weights=True)

    model.fit(train['plot'], train['direction'], epochs=5, validation_data=(test['plot'], test['direction']), batch_size=50, callbacks=[earlyStopping])


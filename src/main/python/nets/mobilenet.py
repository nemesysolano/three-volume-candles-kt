import datasets
import directories
import tensorflow as tf
import numpy as np
import argparse
import datetime
import modelIO

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

    train, validate, test= datasets.load_reversions_with_images_for_timeframe( timeframe, preprocessor=tf.keras.applications.mobilenet.preprocess_input)
    
    if version == 1:        
        base_model = tf.keras.applications.mobilenet.MobileNet(weights="imagenet", include_top=False, input_shape=train['plot'][0].shape, classes=datasets.NUM_CLASSES)
    elif version == 2:
        base_model = tf.keras.applications.mobilenet_v2.MobileNetV2(weights="imagenet", include_top=False, input_shape=train['plot'][0].shape, classes=datasets.NUM_CLASSES)

    base_model.trainable = False ## Not trainable weights


    model = tf.keras.models.Sequential([
        base_model,
        tf.keras.layers.GlobalAveragePooling2D(),
        tf.keras.layers.Flatten(),
        tf.keras.layers.Dense(512, activation='relu'),
        tf.keras.layers.Dense(256, activation='relu'),
        tf.keras.layers.Dense(64, activation='relu'),
        tf.keras.layers.Dense(datasets.NUM_CLASSES, activation='softmax')
    ])

    model.summary()

    model.compile(
        optimizer='adam',
        loss='categorical_crossentropy',
        metrics=['accuracy'],
    )

    earlyStopping = tf.keras.callbacks.EarlyStopping(monitor='val_accuracy', mode='max', patience=7,  restore_best_weights=True)

    model.fit(train['plot'], train['direction'], epochs=10, validation_data=(validate['plot'], validate['direction']), batch_size=25, callbacks=[earlyStopping])
    y_hat = model.predict(test['plot'])
    y_test = test['direction']
    modelIO.save_model(timeframe, model, test['plot'] , np.round(y_hat).astype(np.int32), np.round(y_test).astype(np.int32))

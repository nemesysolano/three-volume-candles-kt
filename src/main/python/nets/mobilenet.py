from sklearn.metrics import confusion_matrix
import datasets
import tensorflow as tf
import numpy as np
import argparse
from environment import remove_dir, MODELS_DIR, checkpoint_file
import modelIO
import os

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
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
        tf.keras.layers.Dense(datasets.NUM_CLASSES, activation='softmax')
    ])

    model.summary()

    model.compile(
        optimizer='adam',
        loss='categorical_crossentropy',
        metrics=['accuracy'],
    )

    checkpoint_file_path = checkpoint_file(timeframe)
    remove_dir(checkpoint_file_path)
    earlyStopping = tf.keras.callbacks.EarlyStopping(monitor='val_accuracy', patience = 5, restore_best_weights = False)
    checkpoint = tf.keras.callbacks.ModelCheckpoint(
        filepath=checkpoint_file_path,
        save_weights_only=True,
        monitor='val_accuracy',
        mode='max',
        save_best_only=True, verbose=1
    )


    model.fit(train['plot'], train['direction'], epochs=20, validation_data=(validate['plot'], validate['direction']), batch_size=100, callbacks=[earlyStopping, checkpoint])

    x = test['plot']
    y_test = np.round(test['direction']).astype(np.int32)
    loss, accuracy = model.evaluate(x, y_test, verbose = 0)
    print('Test loss:', loss) 
    print('Test accuracy:', accuracy)

    modelIO.save_model(timeframe, model, x,  y_test)
 
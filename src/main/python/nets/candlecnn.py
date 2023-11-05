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
    args = parser.parse_args()
    timeframe = args.timeframe

    train, validate, test= datasets.load_reversions_with_images_for_timeframe( timeframe, preprocessor=tf.keras.applications.mobilenet.preprocess_input)    

    #create model
    model = tf.keras.models.Sequential()
    
    #add model layers
    model.add(tf.keras.layers.Conv2D(32, kernel_size=5, activation='relu', input_shape=train['plot'][0].shape))
    model.add(tf.keras.layers.AvgPool2D(pool_size=(10, 10), strides=(1, 1)))
    model.add(tf.keras.layers.Conv2D(16, kernel_size=2, activation='relu'))
    model.add(tf.keras.layers.MaxPool2D(pool_size=(5, 5), strides=(1, 1)))
    model.add(tf.keras.layers.Flatten())
    model.add(tf.keras.layers.Dense(3, activation='softmax'))                
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

  
    model.fit(train['plot'], train['direction'], epochs=5, validation_data=(validate['plot'], validate['direction']), batch_size=100, callbacks=[earlyStopping, checkpoint])

    x = test['plot']
    y_test = np.round(test['direction']).astype(np.int32)
    loss, accuracy = model.evaluate(x, y_test, verbose = 0)
    print('Test loss:', loss) 
    print('Test accuracy:', accuracy)

    modelIO.save_model(timeframe, model, x,  y_test)
 


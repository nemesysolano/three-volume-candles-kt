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

    train, validate, test= datasets.split_dataset(datasets.load_reversions_with_images_for_timeframe( timeframe))
    print(train['plot'].shape, test['plot'].shape, test['direction'].shape)
    
    input_shape = train['plot'][0].shape

    #create model
    model = tf.keras.models.Sequential()
    
    #add model layers
    model.add(tf.keras.layers.Conv2D(32, kernel_size=5, activation='relu', input_shape = input_shape))
    model.add(tf.keras.layers.MaxPool2D(pool_size=(10, 10), strides=(1, 1)))
    model.add(tf.keras.layers.Flatten())
    model.add(tf.keras.layers.Dense(3, activation='softmax'))                
    model.summary()

    model.compile(
        optimizer='adam',
        loss='categorical_crossentropy',
        metrics=['accuracy'],
    )

    earlyStopping = tf.keras.callbacks.EarlyStopping(monitor='val_accuracy', mode='max', patience=5,  restore_best_weights=True)

    model.fit(train['plot'], train['direction'], epochs=10, validation_data=(validate['plot'], validate['direction']), batch_size=50, callbacks=[earlyStopping])
    y_hat = model.predict(test['plot'])
    y_test = test['direction']
    modelIO.save_model(timeframe, model, test['plot'] , np.round(y_hat).astype(np.int32), np.round(y_test).astype(np.int32))
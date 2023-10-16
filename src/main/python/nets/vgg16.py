import datasets
import directories
import tensorflow as tf
import numpy as np

if __name__ == "__main__":
    print(directories.DATA_DIR)
    print(directories.PLOTS_DIR)
    train, test= datasets.split_dataset(datasets.load_reversions_with_images("GBPUSD","M30"))
    print(train['plot'].shape, test['plot'].shape)

    base_model = tf.keras.applications.vgg16.VGG16(weights="imagenet", include_top=False, input_shape=train['plot'][0].shape)
    base_model.trainable = False ## Not trainable weights

    flatten_layer = tf.keras.layers.Flatten()
    dense_layer_1 = tf.keras.layers.Dense(50, activation='tanh')
    dense_layer_2 = tf.keras.layers.Dense(20, activation='tanh')
    prediction_layer = tf.keras.layers.Dense(datasets.NUM_CLASSES, activation='softmax')

    model = tf.keras.models.Sequential([
        base_model,
        flatten_layer,
        dense_layer_1,
        dense_layer_2,
        prediction_layer
    ])

    model.summary()

    model.compile(
        optimizer='adam',
        loss='categorical_crossentropy',
        metrics=['accuracy'],
    )

    earlyStopping = tf.keras.callbacks.EarlyStopping(monitor='val_accuracy', mode='max', patience=5,  restore_best_weights=True)

    model.fit(train['plot'], train['direction'], epochs=1, validation_data=(test['plot'], test['direction']), batch_size=10, callbacks=[earlyStopping])


    # # Verifiying input
    # ## Training Dataset
    # print("Training Dataset")

    # x=np.array(x) # Converting to np arrary to pass to the model
    # print(x.shape)

    # y=to_categorical(y) # onehot encoding of the labels
    # # print(y)
    # print(y.shape)    

    
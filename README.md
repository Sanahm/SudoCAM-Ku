# SudoCAM-Ku!
An android camera-based augmented reality application that automatically solve and fill Sudoku puzzles by passing the camera over the puzzle. This project is based on a previous work : [An automous robot that solve and fill sudoku puzzles](https://github.com/Sanahm/Sudoku-robot) (see Sudoku-robot repo for more details).

<p align="center">
<img src="https://github.com/Sanahm/SudoCAM-Ku/blob/master/sudoku.png" width="512" height="340"/>
</p>

## Get the project

    $ git pull https://github.com/Sanahm/SudoCAM-Ku 

## How it works?

First you need to take a picture of the sudoku grid by touching the screen when ready (this is not mandatory! It is just to avoid spending a lot of time on computation). After that, you'll need to touch the screen a second time to process the grid and solve it. Once solved the result will automatically be drawn on the camera frame.

An offline processing is also implemented. That it is, you can also solve a puzzle from your phone storage.

> Note that this app use image processing method to process grid. That means, better is your image more the processing will succeed! You'll certainly have to try and try again according to the quality of your images .

A video is worth a thousand words: [a link to youtube video](https://youtu.be/cELDY2QAqSk)

## Remark

- As the project size is big ( ~1GB with libs both Tensorflow and OpenCV) , you'll find on my repo just .java, .xml files and asset folder containing the tensorflow training model.

- The project is fully documented. If you want more information about how processing works, just go and see Sudoku-robot.

## How to setup the project

Create a nex project on androidStudio and replace the src folder by mine.

You need to install Tensorflow and OpenCV in your AndroidStudio environment.
Here are some indications on how to do that:
- Download the Tensorflow libraries [here](https://ci.tensorflow.org/view/Nightly/job/nightly-android/293/) and decompress. Once done,copy the libandroid_tensorflow_inference_java.jar and the architecture folders inside of the libtensorflow_inference.so in out/native/libtensorflow_inference.so into the android project jniLibs folder locate at src/main/jniLibs.

- Download the OpenCV android pack [here](https://opencv.org/releases.html) and decompress. I use the 3.1.0 version. Then copy the architecture folders inside OpenCV-android-sdk/sdk/native/libs into the same jniLibs as below.

- Now update the gradle file

```
apply plugin: 'com.android.application'

android {
    compileSdkVersion 25
    buildToolsVersion "25.0.3"

    defaultConfig {
        applicationId "com.example.mohamed.sudoku"
        minSdkVersion 16
        targetSdkVersion 23
        versionCode 1
        versionName "1.0"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    testCompile 'junit:junit:4.12'
    compile 'com.android.support:appcompat-v7:25.0.0'
    compile 'com.android.support:design:25.0.0'
}
```
 
## Keywords
- Computer vision

- Image classification - Convolutionnal Neural Network (CNN) with Tensorflow

- Image recognition - Image processing

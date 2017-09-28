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

## Keywords
- Computer vision

- Image classification - Convolutionnal Neural Network (CNN) with Tensorflow

- Image recognition - Image processing

# Machine Learning – Dog / Cat Classifier

This directory contains all the logic related to training, evaluating,
and exporting the image classification model
(dog / cat / other).

Training images are **not** versioned in this repository.
This document describes where the data is located and how it is constructed.

## Data sources

### Dog / Cat

- Dogs vs Cats Dataset (Kaggle)  
  https://www.kaggle.com/c/dogs-vs-cats  
  ~4,000 images

### Other

Images for the `other` class come from multiple sources in order to
cover a wide visual diversity:

- Kaggle Human Images Dataset – Men and Women  
  https://www.kaggle.com/datasets/snmahsa/human-images-dataset-men-and-women  
  ~800 images

- Kaggle Animals Detection Images Dataset  
  https://www.kaggle.com/datasets/antoreepjana/animals-detection-images-dataset  
  ~600 images

- Kaggle Scene Classification Dataset  
  https://www.kaggle.com/datasets/nitishabharathi/scene-classification  
  ~800 images

- Kaggle Vehicle Images Dataset  
  https://www.kaggle.com/datasets/lyensoetanto/vehicle-images-dataset  
  ~400 images

- Kaggle Daily Objects Around the World Dataset (Dollar Street)  
  https://www.kaggle.com/datasets/humansintheloop/dollar-street-dataset  
  ~800 images
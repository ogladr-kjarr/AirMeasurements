# Air Measurements

This repo is a very basic use of Java to learn how to model objects as classes, create interfaces, use lambdas and method references, use JUnit, and to work with databases for storing and retrieving data.

# Data

The data comes from the Europa Data portal [here](https://data.europa.eu/data/datasets/6db44316-9717-4a98-8a83-577d4cb25afc-stadt-zurich?locale=en) and [here](https://data.europa.eu/data/datasets/3d0c33d6-ec57-426a-918c-ac8a60573789-stadt-zurich?locale=en). From these pages are links to the data for each year available of the measurements. The general format of the download links are a uniform URL, with only the year at the end of the URL changing. The final YYYY of each URL stands for the four digit year value. The meteo values range from 1992 to 2022, while the air measurements range from 1983 to 2022.

The program can download the data from these sources into CSV files, then read and parse the files into measurement objects.
# **Clustering and K-Means: a report on parallelization optimization with Apache Spark**

This project is has been built in the context of Advanced Operating System class at the University of Salerno.

The goal of the experimentation is carried out to classify a collection of scientific papers downloaded from ArXiv.org. To achieve that, it is necessary to implement all the pre-processing stages (from raws type-b PDFs to string-based data) required to conduct a k-means algorithm.

## Dataset

Having a good amount of data is essential for the best result from the experiment.
ArXiv.org provide more than 1mln of scientific papers, all of them in PDF type-B format.

To download them we have written a [python tool](https://github.com/co5m0/Arxiv-pdf-downloader) that use an old (2017) archive.org backup to obtain the dataset.

## [Sequencial implementation](SequentialK-MeansPy/app.py)

Our sequential program is implemented by defining a pipeline of sequential executions of the processes. Starting from our input, the data gradually undergo changes and improvements. The pipeline is developed in such a way that every output of a phase is the input of the next one, and that the correctness of the operation of the process is preserved.

The program is written in Python, using _sklearn_ and _nltk_ libs.

## [Spark implementation](SparkK-MeansScala/src/main/scala/it/unisa/soa/App.scala)

We used Apache Spark as the base framework for the following reasons:

- Spark also allows us to cache the data in memory, which is beneficial in case of iterative algorithms such as those used in machine learning.

- Apache Spark defines the parallelization processes by itself in the best way achievable, so the manual division of the high data volumes isn’t necessary.

- Thanks to HDFS and Spark’s parallelization capabilities, the program can be executed on multiple machines concurrently, further optimizing data handling and execution times.

Spark divides the execution into 3 main jobs. First, the PDFs are retrieved as binaryFiles rdds, then the files are converted into rdd strings through a map function. The rdd strings, converted into a DataFrame, are fitted inside a Spark MLlib Pipeline.

ML Pipelines provide a uniform set of high-level APIs built on top of DataFrames that help users create and tune practical machine learning pipelines. MLlib standardizes APIs for machine learning algorithms to make it easier to combine multiple algorithms into a single pipeline, or workflow.

# SparkK-MeansScala

This is the part of the project that use Spark and Yarn to perform above a cluster.

To run the project all files must be already on the HDFS in a folder.

We provide 3 different script to execute the program:

 1. *[runlocal.sh](runlocal.sh)* (runs the program with `--master local` parameter)
 2. *[run.sh](run.sh)* (runs the program with `--master yarn` and  `--deploy-mode cluster` parameters)
 3. *[run_single_node.sh](run_single_node.sh)* (runs the program with `--master yarn` and  `--deploy-mode cluster` parameters but `--num-executors 1` )

To run them properly it needs the **input** folder on the HDFS:

    spark-submit --class it.unisa.soa.App --master yarn --deploy-mode cluster --num-executors 1 --executor-cores 7 --executor-memory 16G --conf "spark.app.id=wordcount" target/app-1.0-jar-with-dependencies.jar <'/path/to/HDFS/input/folder/*'>

Then run it with:
`sh run.sh`

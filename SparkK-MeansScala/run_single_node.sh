#!/bin/bash
#mvn clean
#mvn package
spark-submit --class it.unisa.soa.App --master yarn --deploy-mode cluster --num-executors 1 --executor-cores 7   --executor-memory 16G --conf "spark.app.id=wordcount" target/app-1.0-jar-with-dependencies.jar '/user/soa/conseccio/INPUT/*'

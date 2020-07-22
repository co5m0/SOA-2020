mvn clean
mvn package
spark-submit --master local --driver-memory 2g --executor-memory  2g --class it.unisa.soa.App --conf "spark.app.id=wordcount" target/app-1.0-jar-with-dependencies.jar '/user/soa/conseccio/inputpdf/*'
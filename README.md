# Java Annotation Processing 101

>gradle-version: 7.2  
java-version: 16

This code provides simple Java Annotation Processor available via  
[`javax.annotation.processing`](https://docs.oracle.com/en/java/javase/16/docs/api/java.compiler/javax/annotation/processing/package-summary.html)
API with [Apache Velocity](https://velocity.apache.org/) for class creation

Project structure:

```
java-annotation-processing-101  
├───app 1️⃣ 
└───extention  
    ├───api 2️⃣  
    └───processor 3️⃣
```

1️⃣ `app` that uses `extention-api` and `extention-processor`  
2️⃣ `extention-api` that provides compile-time annotation `@Builder`  
3️⃣ `extention-processor` that creates `*Builder` from [Apache Velocity](https://velocity.apache.org/) template

>⚡ Such project structure separates compile-time annotation API  
from [`javax.annotation.processing`](https://docs.oracle.com/en/java/javase/16/docs/api/java.compiler/javax/annotation/processing/package-summary.html)
implementation (and it's dependencies)

How to run example:

```bash
gradlew :app:build
java -jar app/build/libs/app.jar
```

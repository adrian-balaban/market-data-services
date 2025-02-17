# Flink Deployments Model

1. ### Apache Flink Kubernetes Operator
    - Decision: **Potential production usage**
    - Project status: **Production Ready**
    - Prerequisites:
        - Kubernetes Cluster
        - Tools: kubectl, helm
    - Automatization: Easy (Script: *./infra/k8s/scripts/deployFlinkOperator.sh*)
    - Pros:
        - Natively supported and maintained by Flink
        - 
    - Cons:
        - More manual work
        - 

2. ### Bitnami Flink 
    - Decision: **Potential alternative**
    - Project status: **Production Ready**
    - Prerequisites:
        - Kubernetes Cluster
        - Tools: kubectl, helm

3. ### Confluent Flink
    - Decision: **Potential usage**
    - Project status: **Production Ready**
    - Prerequisites:
        - Confluent Subscription
    - Automatization: ?
    - Pros:
        - Fully managed & scallabe by Confluent
        - Built-in integration with Kafka 
        - Minimal Maintenance: Serverless, reducing operational overhead.
    - Cons:
        - Cost (At this moment it is hard to calculate and predict usage)
        - Vendor Lock-in: Tied to Confluent's platform.


4. ### YARN
    - Decision: **NO GO?**
    - Project status: **Production Ready**
    - Prerequisites:
        - VMs - nodes - working as YARN Nodes
        - YARN Cluster based on Hadoop accepting Flink applications
    - Automatization: DIFFICULT 
    - Key Pros:
        - More enterprise friendly than K8s setup
            - Advanced Security mechanisms out of the box - supports Kerberos (as Flink has no auth layer out-of-the-box)
            - Readiness for usage other Apache Tools 
                - Apache Flink: A stream processing framework that enables scalable, high-throughput, and low-latency data processing.
                -   Apache Hadoop: An open-source framework for distributed storage and processing of large datasets using the MapReduce programming model.
                -   Apache Spark: A powerful analytics engine for big data processing, supporting batch and stream processing.
                -   Apache HBase: A distributed, scalable, big data store that runs on top of HDFS.
                -   Apache Storm: A real-time computation system for processing large streams of data.
                -   Apache Tez: A framework for building high-performance batch and interactive data processing applications.
                -   Apache Samza: A stream processing framework that integrates with YARN for resource management.
                -   Apache Pig: A high-level platform for creating programs that run on Hadoop.
                -   Apache Hive: A data warehouse software that facilitates reading, writing, and managing large datasets residing in distributed storage.
    - Key Cons:
        - Complex Setup: Requires Hadoop expertise.
        - Additional overhelm if YARN is not addapted and integrated within VISA yet
        - Resource Intensive: May require significant resources for Hadoop management.

5. ### AWS EMR (Flink on EMR runs as a YARN application,)
    - Decision: **Open Option**
    - Project status: **Production Ready**
    - Prerequisites:
        - AWS Subscription
    - Automatization: ? 
    - Key Pros:
        - Managed Service - AWS EMR handles the provisioning, configuration, and scaling of the underlying infrastructure
        - Additional integrations, like with Kafka, Amazon Kinesis, Apache Kafka
    - Key Cons:
        - Costly 

6. ### Amazon Managed Service for Apache Flink
    - Decision: **NO GO?**
    - Project status: **Production Ready**
    - Prerequisites:
        - AWS Subscription
    - Automatization: ? 
    - Key Pros:
        - Fully Managed, Automatic Scalling to the volume and thoughput
        - Potentially can be deployed in the same AZ/Region as BPIPE Private Link 
        - Potentially can be deployed in the same AZ/Region with (Amazon MSK) Amazon Kafka Managed Services if needed
        - Dedicated ONLY for Real time processing
        - Integrations with Amazon Kinesis Data, S3, etc
    - Key Cons:
        - Unpredictable costs; Pay As You Go; we predict high workload on B-PIPE

7. ### Standalone
    - Decision: **NO GO**
        - Why: Manual, worhtless, timeconsuming,  
    - Prerequisites:
        - VM (Or any other UNIX env)
        - Java 1.8
        - Flink distribution
    - Consequences:
        - "most barebone way"
        - requires manual management and maintanence 
        - have to take care of restarting failed processes, or allocation and de-allocation of resources during operation

# Flink's Application Deployments Model
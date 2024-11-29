# market-data-processor


### To run everything locally:

1) Infra
    - `cd infra/kafka`
    - Run compose (depends what you have installed):
        - `docker-compose up`

        - `podman compose up`

2) Run Externals
    - `cd vendors/market-data-stub`
    - Build and run container (depends what you have installed)::

        -   ```
            docker build -t market-data-stub . --load
            docker run -d -p 3080:3800 --name fx-market-stub market-data-stub
            ```

        -   ```
            podman build -t market-data-stub . --load
            podman run -d -p  3080:3080 --name fx-market-stub market-data-stub
            ```
3) Run fx-market-services
    -  `cd fx-market-services`
    
    

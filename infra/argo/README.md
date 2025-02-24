## Deploy with ArgoCD:

Run: `./deployAllWithArgo.sh`
```
Usage:
-branch <branch_name>         <- to specify branch name to build - default: master
-env <env_name>               <- to specify env name for and take relevant manifests - default: env
-build <true|false>           <- to build with test mode - default: true
-test <true|false>            <- to build with test mode - default: false
-n <namespace>                <- to specify namespace
-tag <docker_tag>             <- to specify docker tag for services
-registry <DOCKER_REGISTRY>   <- to specify docker registry
```

Samples:
- `./deployAllWithArgo.sh -branch master -env test -build false -n master`
- `./deployAllWithArgo.sh -branch master -env dev -build false -n dev`
- `./deployAllWithArgo.sh -branch <my_branch_with_changed_manifests> -env local -build true -n <my_namespace>`

## Developer notes, setup and maintenance
###   1. How to generate fresh solution manifests:
    - Solution: (WARNING! Remember that this command may be different over time, please adjust!) 
       ``` 
      helm upgrade --install \
       --dry-run \
       --namespace fx-market-master \
       --set tag="1.0.0" \
       --set apps.fxmarketconnector.image.repository="localhost:5001/fx-market-services/fx-market-connector" \
       --set apps.fxmarketprocessor.image.repository="localhost:5001/fx-market-services/fx-market-processor" \
       --set apps.flinkorchestrator.image.repository="localhost:5001/fx-market-services/flink-orchestrator" \
       -f ../helm/services/values-common.yaml \
       -f ../helm/services/values-fxmarket.yaml fx-market-services \
       ../helm/services > manifest.yaml
       ```
   - Stubs: (WARNING! Remember that this command may be different over time, please adjust!)
      ``` 
       helm upgrade --install \
       --dry-run \
       --namespace fx-market-externals \
       --set tag="1.0.0" \
       --set apps.fxmarketdatastub.image.repository="localhost:5001/fx-market-externals/market-data-stub" \
       -f ../helm/services/values-externals.yaml fx-market-externals \
       ../helm/services > externals.yaml
      ```
   
###   2. Generate secret for Github private repo:
      1. Got to Github profile (avatar top right) -> `Settings`
      2. Left full bottom -> `Developer settings` 
      3. Left menu -> `Personal access tokens` -> `Fine-grained tokens`
      4. Top right button: `Generate new token`
      5. Select:
           - All mandatory
           - Only Selected Repositories  - select proper repo
           - Permissions
             - Repository permissions -> for each record select `No access`
             - Repository permissions -> `Read only` for `Read access to code, commit statuses, and metadata`
             - User permissions -> for each record select `No access`
###   3. Adjust githubSecret.yaml -> replace with new token: `stringData.password`
# market-data-processor


### Requirements:
- java Graavlm v 21

## To run everything locally:

1) Manual K8s installation
   - Go to `/infra/k8s` and run `./deployAll.sh help`
2) Hybrid K8s installation - Manual tools and solution via ArgoCD
   - Go to `/infra/argo` and run `./deployAllWithArgo.sh help`
3) Docker Compose - **Not fully maintained**
   - Go to `/infra/local` and run `./runWithDockerCompose.sh`

### HighLevel Architecture:
![HLA](docs/HLA.png)

### Development Workflow:
![DevelopmentWorkflow](docs/DevelopmentWorkflow.drawio.png)


   
### Additional DEV Notes:
- if you are MacOS user, before pushing changes to repo please do
  - `git config --global core.fileMode false` - (it prevents changing files permissions in repo) 
  - `git config --global core.autocrlf input` - (set proper EOF)
  - `git config --global core.autocrlf input`

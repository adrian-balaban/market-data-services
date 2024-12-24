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


helm upgrade --install \
--dry-run \
--namespace fx-market-externals \
--set tag="1.0.0" \
--set apps.fxmarketdatastub.image.repository="localhost:5001/fx-market-externals/market-data-stub" \
-f ../helm/services/values-externals.yaml fx-market-externals \
../helm/services > externals.yaml



└──╼ $kubectl edit clusterrolebinding argocd-application-controller -n mj1

└──╼ $kubectl edit clusterrolebinding argocd-server -n mj1

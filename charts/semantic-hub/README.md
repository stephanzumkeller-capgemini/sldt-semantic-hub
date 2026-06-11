# semantic-hub

![Version: 0.6.0](https://img.shields.io/badge/Version-0.6.0-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: 0.7.0](https://img.shields.io/badge/AppVersion-0.7.0-informational?style=flat-square)

**Helm Chart for the Catena-X Semantic Hub Application** <br/>
This Helm charts installs the Semantic Hub application and its dependencies. 

## Prerequisites
- Kubernetes 1.19+
- Helm 3.2.0+
- PV provisioner support in the underlying infrastructure

## Install
```
kubectl create namespace semantics
helm install hub -n semantics ./charts/semantic-hub
```

## Values

| Key | Type | Default                                                                                                                                                                     | Description |
|-----|------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------|
| enableKeycloak | bool | `false` |  |
| graphdb.args[0] | string | `"--tdb2"`                                                                                                                                                                  |  |
| graphdb.args[1] | string | `"--update"`                                                                                                                                                                |  |
| graphdb.args[2] | string | `"--loc"`                                                                                                                                                                   |  |
| graphdb.args[3] | string | `"databases/"`                                                                                                                                                              |  |
| graphdb.args[4] | string | `"/ds"`                                                                                                                                                                     |  |
| graphdb.containerPort | int | `3030`                                                                                                                                                                      |  |
| graphdb.enabled | bool | `false`                                                                                                                                                                     |  |
| graphdb.image | string | `"jena-fuseki-docker:5.3.0"`                                                                                                                                                |  |
| graphdb.imagePullPolicy | string | `"IfNotPresent"`                                                                                                                                                            |  |
| graphdb.javaOptions | string | `"-Xmx1048m -Xms1048m"`                                                                                                                                                     |  |
| graphdb.password | string | `"admin"`                                                                                                                                                                   |  |
| graphdb.pvcAccessModes[0] | string | `"ReadWriteOnce"`                                                                                                                                                           |  |
| graphdb.queryEndpoint | string | `"query"`                                                                                                                                                                   |  |
| graphdb.replicaCount | int | `1`                                                                                                                                                                         |  |
| graphdb.resources.limits.memory | string | `"1024Mi"`                                                                                                                                                                  |  |
| graphdb.resources.requests.memory | string | `"512Mi"`                                                                                                                                                                   |  |
| graphdb.service.port | int | `3030`                                                                                                                                                                      |  |
| graphdb.storageClassName | string | `"default"`                                                                                                                                                                 |  |
| graphdb.storageSize | string | `"50Gi"`                                                                                                                                                                    |  |
| graphdb.updateEndpoint | string | `"update"`                                                                                                                                                                  |  |
| graphdb.username | string | `"admin"`                                                                                                                                                                   |  |
| hub.authentication | bool | `false`                                                                                                                                                                     |  |
| hub.containerPort | int | `4242`                                                                                                                                                                      |  |
| hub.embeddedTripleStore | bool | `false`                                                                                                                                                                     |  |
| hub.graphdbBaseUrl | string | `"http://graphdb:3030"`                                                                                                                                                     |  |
| hub.host | string | `"minikube"`                                                                                                                                                                |  |
| hub.idpClientId | string | `"default-client"`                                                                                                                                                          |  |
| hub.idpIssuerUri | string | `""`                                                                                                                                                                        |  |
| hub.image.registry | string | `"docker.io"`                                                                                                                                                               |  |
| hub.image.repository | string | `"tractusx/sldt-semantic-hub"`                                                                                                                                              |  |
| hub.image.version | string | `""`                                                                                                                                                                        |  |
| hub.imagePullPolicy | string | `"IfNotPresent"`                                                                                                                                                            |  |
| hub.ingress.annotations | list | `[]`                                                                                                                                                                        |  |
| hub.ingress.className | string | `""`                                                                                                                                                                        |  |
| hub.ingress.enabled | bool | `false`                                                                                                                                                                     |  |
| hub.ingress.tls | bool | `true`                                                                                                                                                                      |  |
| hub.ingress.tlsSecretName | string | `""`                                                                                                                                                                        |  |
| hub.ingress.urlPrefix | string | `"/semantics/hub"`                                                                                                                                                          |  |
| hub.replicaCount | int | `1`                                                                                                                                                                         |  |
| hub.resources.limits.memory | string | `"1024Mi"`                                                                                                                                                                  |  |
| hub.resources.requests.memory | string | `"512Mi"`                                                                                                                                                                   |  |
| hub.service.port | int | `8080`                                                                                                                                                                      |  |
| hub.service.type | string | `"ClusterIP"`                                                                                                                                                               |  |
| keycloak.auth.adminPassword | string | `"admin"` |  |
| keycloak.auth.adminUser | string | `"admin"` |  |
| keycloak.containerPort | int | `8080` |  |
| keycloak.image.registry | string | `"quay.io"` |  |
| keycloak.image.repository | string | `"keycloak/keycloak"` |  |
| keycloak.image.tag | string | `"26.6.3"` |  |
| keycloak.imagePullPolicy | string | `"IfNotPresent"` |  |
| keycloak.resources.limits.cpu | string | `"750m"` |  |
| keycloak.resources.limits.memory | string | `"1024Mi"` |  |
| keycloak.resources.requests.cpu | string | `"250m"` |  |
| keycloak.resources.requests.memory | string | `"512Mi"` |  |
| keycloak.service.port | int | `80` |  |
| keycloak.service.type | string | `"ClusterIP"` |  |

----------------------------------------------
Autogenerated from chart metadata using [helm-docs v1.11.0](https://github.com/norwoodj/helm-docs/releases/v1.11.0)

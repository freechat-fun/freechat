#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_kubectl

kubectl --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} run docker --image=docker:dind --overrides='{
  "apiVersion": "v1",
  "spec": {
    "containers": [
      {
        "name": "docker",
        "image": "docker:dind",
        "securityContext": {
          "privileged": true
        }
      }
    ]
  }
}' --command -- /bin/sh -c "dockerd-entrypoint.sh &; sleep infinity"

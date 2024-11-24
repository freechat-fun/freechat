source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_kubectl

kubectl --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} delete pod docker
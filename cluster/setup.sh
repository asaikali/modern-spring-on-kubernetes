#kapp deploy -a cert-manager -f static/cert-manager/cert-manager.yaml -y
#kapp deploy -a contour -f static/contour -y

# kapp deploy -a envoy-gateway -f https://github.com/envoyproxy/gateway/releases/download/v1.0.2/install.yaml -y
helm template contour ./charts/network/contour --values ./values/contour.yaml  > templates/contour.yaml

helm upgrade contour ./charts/network/contour --values ./values/contour.yaml --install

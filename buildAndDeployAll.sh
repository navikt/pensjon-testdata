#!/bin/bash
echo "Angi versjon: "
read version

imagenameServer="docker.adeo.no:5000/navikt/pensjon-testdata-server:$version"
imagenameClient="docker.adeo.no:5000/navikt/pensjon-testdata-client:$version"

cd ./server/
echo $imagenameServer
docker build -t $imagenameServer .
docker push $imagenameServer

cd ../client/
echo $imagenameClient
docker build -t $imagenameClient .
docker push $imagenameClient

cd ..
kubectl config use-context dev-fss

sed "s/IMAGE_VERSION/$version/g" server/nais.yaml | kubectl apply -f-
sed "s/IMAGE_VERSION/$version/g" client/nais.yaml | kubectl apply -f-
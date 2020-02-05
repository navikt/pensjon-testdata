#!/bin/bash

cd ./server/
cat VERSION | awk '{print $1+1}' > VERSION
serverVersion=`cat VERSION`
imagenameServer="repo.adeo.no:5443/navikt/pensjon-testdata-server:$serverVersion"
echo "Bumped $imagenameServer version to: $serverVersion"
docker build -t $imagenameServer .
docker push $imagenameServer

cd ../client/
cat VERSION | awk '{print $1+1}' > VERSION
clientVersion=`cat VERSION`
imagenameClient="repo.adeo.no:5443/navikt/pensjon-testdata-client:$clientVersion"
echo "Bumped $imagenameClient version to: $serverVersion"
docker build -t $imagenameClient .
docker push $imagenameClient

cd ..
kubectl config use-context dev-fss

sed "s/IMAGE_VERSION/$serverVersion/g" server/nais.yaml | kubectl apply -f-
sed "s/IMAGE_VERSION/$clientVersion/g" client/nais.yaml | kubectl apply -f-
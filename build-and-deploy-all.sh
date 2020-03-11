#!/bin/bash
echo "Angi versjon: "
read version

cd ./server/
imagenameServer="repo.adeo.no:5443/navikt/pensjon-testdata-server:$version"

docker build -t $imagenameServer .
docker push $imagenameServer

cd ../client/
imagenameClient="repo.adeo.no:5443/navikt/pensjon-testdata-client:$version"
docker build -t $imagenameClient .
docker push $imagenameClient

cd ../dolly-facade/
imagenameDollyFacade="repo.adeo.no:5443/navikt/pensjon-testdata-dolly-facade:$version"
docker build -t $imagenameDollyFacade .
docker push $imagenameDollyFacade

cd ..
kubectl config use-context dev-fss
sed "s/IMAGE_VERSION/$version/g" server/nais-q2.yaml | kubectl apply -f-
sed "s/IMAGE_VERSION/$version/g" client/nais-q2.yaml | kubectl apply -f-

sed "s/IMAGE_VERSION/$version/g" dolly-facade/nais.yaml | kubectl apply -f-


#!/bin/bash

echo "Angi versjon: "
read version


imagename="repo.adeo.no:5443/navikt/pensjon-testdata-client:$version"

echo $imagename

kubectl config use-context dev-fss

sed "s/IMAGE_VERSION/$version/g" nais.yaml | kubectl apply -f-


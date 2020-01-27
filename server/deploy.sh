#!/bin/bash

echo "Angi versjon: "
read version


imagename="docker.adeo.no:5000/navikt/pensjon-testdata-server:$version"

echo $imagename

kubectl config use-context dev-fss

sed "s/IMAGE_VERSION/$version/g" nais.yaml | kubectl apply -f-


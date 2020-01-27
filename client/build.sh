#!/bin/bash

echo "Angi versjon: "
read version


imagename="docker.adeo.no:5000/navikt/pensjon-testdata-client:$version"

echo $imagename

docker build -t $imagename .

docker push $imagename
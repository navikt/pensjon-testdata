#!/bin/bash

echo "Angi versjon: "
read version


imagename="repo.adeo.no:5443/navikt/pensjon-testdata-server:$version"

echo $imagename

docker build -t $imagename .

docker push $imagename
#!/bin/bash
mkdir server/secrets
mkdir server/secrets/db

mkdir server/secrets/db/pen
echo "jdbc:oracle:thin:@dm07-scan.adeo.no:1521/pen_XX" > server/secrets/db/pen/jdbc_url

mkdir server/secrets/db/popp
echo "jdbc:oracle:thin:@dm07-scan.adeo.no:1521/popp_XX" > server/secrets/db/popp/jdbc_url

mkdir server/secrets/oracle

mkdir server/secrets/oracle/pen
echo "<username>" > server/secrets/oracle/pen/username
echo "<password>" > server/secrets/oracle/pen/password

mkdir server/secrets/oracle/popp
echo "<username>" > server/secrets/oracle/pen/username
echo "<password>" > server/secrets/oracle/popp/password

mkdir server/secrets/srvpensjon
echo "<username>" > server/secrets/srvpensjon/username
echo "<password>" > server/secrets/srvpensjon/password

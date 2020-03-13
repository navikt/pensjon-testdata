#!/bin/bash
mkdir server/secrets
mkdir server/secrets/db

mkdir server/secrets/db/pen
echo "jdbc:oracle:thin:@dm07-scan.adeo.no:1521/pen_XX" > server/secrets/db/pen/jdbc_url

mkdir server/secrets/db/popp
echo "jdbc:oracle:thin:@dm07-scan.adeo.no:1521/popp_XX" > server/secrets/db/popp/jdbc_url

mkdir server/secrets/db/sam
echo "jdbc:oracle:thin:@dm07-scan.adeo.no:1521/sam_XX" > server/secrets/db/sam/jdbc_url

mkdir server/secrets/oracle

mkdir server/secrets/oracle/pen
echo "<username>" > server/secrets/oracle/pen/username
echo "<password>" > server/secrets/oracle/pen/password

mkdir server/secrets/oracle/popp
echo "<username>" > server/secrets/oracle/popp/username
echo "<password>" > server/secrets/oracle/popp/password

mkdir server/secrets/oracle/sam
echo "<username>" > server/secrets/oracle/sam/username
echo "<password>" > server/secrets/oracle/sam/password

mkdir server/secrets/srvpensjon
echo "<username>" > server/secrets/srvpensjon/username
echo "<password>" > server/secrets/srvpensjon/password

mkdir server/secrets/app
echo "<username>" > server/secrets/app/moog_jdbc_url
echo "<username>" > server/secrets/app/moog_username
echo "<password>" > server/secrets/app/moog_password
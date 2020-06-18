#!/bin/bash

# Enabling noclobber to ensure that existing files are not overwritten
set -o noclobber

mkdir -p server/secrets/db/pen
! echo "jdbc:oracle:thin:@dm07-scan.adeo.no:1521/pen_XX" > server/secrets/db/pen/jdbc_url

mkdir -p server/secrets/db/popp
! echo "jdbc:oracle:thin:@dm07-scan.adeo.no:1521/popp_XX" > server/secrets/db/popp/jdbc_url

mkdir -p server/secrets/db/sam
! echo "jdbc:oracle:thin:@dm07-scan.adeo.no:1521/sam_XX" > server/secrets/db/sam/jdbc_url

mkdir -p server/secrets/oracle

mkdir -p server/secrets/oracle/pen
! echo "<username>" > server/secrets/oracle/pen/username
! echo "<password>" > server/secrets/oracle/pen/password

mkdir -p server/secrets/oracle/popp
! echo "<username>" > server/secrets/oracle/popp/username
! echo "<password>" > server/secrets/oracle/popp/password

mkdir -p server/secrets/oracle/sam
! echo "<username>" > server/secrets/oracle/sam/username
! echo "<password>" > server/secrets/oracle/sam/password

mkdir -p server/secrets/srvpensjon
! echo "<username>" > server/secrets/srvpensjon/username
! echo "<password>" > server/secrets/srvpensjon/password

mkdir -p server/secrets/app
! echo "<username>" > server/secrets/app/moog_jdbc_url
! echo "<username>" > server/secrets/app/moog_username
! echo "<password>" > server/secrets/app/moog_password
#!/bin/bash
set -e

mydir=$(dirname $0)

sudo -H -u postgres bash -c "\
  set -e
  psql < $mydir/clear.sql
  psql -c \"CREATE USER random_chess WITH PASSWORD 'random_chess';\"
  psql -c \"CREATE DATABASE random_chess OWNER random_chess;\"
  psql \"postgresql://random_chess:random_chess@localhost:5432/random_chess\" < $mydir/schema-create.sql
  psql \"postgresql://random_chess:random_chess@localhost:5432/random_chess\" < $mydir/schema-alteration.sql
  if test -f $mydir/openings.sql; then
    psql \"postgresql://random_chess:random_chess@localhost:5432/random_chess\" < $mydir/openings.sql
  fi
  psql \"postgresql://random_chess:random_chess@localhost:5432/random_chess\" < $mydir/example.sql
  if test -f $mydir/secret.sql; then
    psql \"postgresql://random_chess:random_chess@localhost:5432/random_chess\" < $mydir/secret.sql
  fi

"

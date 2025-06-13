try {
  $ErrorActionPreference = "Stop"

  psql -U postgres -f "clear.sql"

  psql -U postgres -c "CREATE USER random_chess WITH PASSWORD 'random_chess';"
  psql -U postgres -c "CREATE DATABASE random_chess OWNER random_chess;"

  $env:PGPASSWORD = "random_chess"

  psql -U random_chess -d random_chess -f "schema-create.sql"
  psql -U random_chess -d random_chess -f "schema-alteration.sql"

  if (Test-Path "openings.sql") {
    psql -U random_chess -d random_chess -f "openings.sql"
  }

  psql -U random_chess -d random_chess -f "example.sql"

  if (Test-Path "secret.sql") {
    psql -U random_chess -d random_chess -f "secret.sql"
  }
}
finally {
    Remove-Item Env:PGPASSWORD
}
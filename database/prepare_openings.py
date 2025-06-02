import csv

input_files = ['a.tsv', 'b.tsv', 'c.tsv', 'd.tsv', 'e.tsv']
output_file = 'openings.sql'

with open(output_file, 'w', encoding='utf-8') as out_sql:
    out_sql.write('INSERT INTO openings (eco, name, partial_fen) VALUES')
    i = 0
    for filename in input_files:
        with open(filename, encoding='utf-8') as f:
            reader = csv.DictReader(f, delimiter='\t')
            for row in reader:
                eco = row['eco'].replace("'", "''")
                name = row['name'].replace("'", "''")
                fen = row['epd'].replace("'","''")
                if i != 0:
                    out_sql.write(',')
                i = i + 1
                out_sql.write(
                    f"\n('{eco}', '{name}', '{fen}')"
                )
    out_sql.write(';')
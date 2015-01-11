gradle -b cobertura.gradle -PnexusLocation=192.168.59.103:9081 -Pcobertura_data_file_clean=$1 -Pcoverage_dir=$2 merge

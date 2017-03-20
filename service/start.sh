USER=`whoami`

memcached -v -d -p 18401 -U 0 -u $USER -m 1 -C -B ascii >log/memd.18401.log 2>&1
memcached -v -d -p 18402 -U 0 -u $USER -m 1 -C -B ascii >log/memd.18402.log 2>&1
memcached -v -d -p 18403 -U 0 -u $USER -m 1 -C -B ascii >log/memd.18403.log 2>&1
memcached -v -d -p 18404 -U 0 -u $USER -m 1 -C -B ascii >log/memd.18404.log 2>&1
memcached -v -d -p 18405 -U 0 -u $USER -m 1 -C -B ascii >log/memd.18405.log 2>&1
memcached -v -d -p 18406 -U 0 -u $USER -m 1 -C -B ascii >log/memd.18406.log 2>&1
memcached -v -d -p 18407 -U 0 -u $USER -m 1 -C -B ascii >log/memd.18407.log 2>&1

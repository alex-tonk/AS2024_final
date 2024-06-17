docker-compose build
docker save localhost:5000/pro-legacy_backend localhost:5000/pro-legacy_front > images.tar
move images.tar distribution/images.tar

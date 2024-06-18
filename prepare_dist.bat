docker-compose build
docker save localhost:5000/pro-legacy_backend localhost:5000/pro-legacy_front > images.tar
move /Y images.tar install/images.tar

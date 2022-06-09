createdb measurements;

create table measurements(
mdate timestamp with time zone,
location varchar,
parameter varchar,
interval varchar,
unit varchar,
value double precision,
status varchar);

docker run -p 5432:5432 -e POSTGRES_PASSWORD=testingjava postgres:latest
docker exec -it container_name bash
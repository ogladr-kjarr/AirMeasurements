create table measurements(
    mdate timestamp with time zone,
    location varchar,
    parameter varchar,
    interval varchar,
    unit varchar,
    value double precision,
    status varchar,
    PRIMARY KEY (mdate, location, parameter)
    );

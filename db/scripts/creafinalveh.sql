drop table if exists finalveh;
drop type if exists public.tipo_vehf;
create type  public.tipo_vehf as(
nomeveh varchar(20),
pf numeric
);

create table finalveh (
id numeric,
dni varchar(10),
nomec varchar(30),
vehf tipo_vehf,
primary key (id));






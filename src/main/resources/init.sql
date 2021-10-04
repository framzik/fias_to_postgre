CREATE TABLE IF NOT EXISTS public.fias_address
(
    id            bigserial NOT NULL,
    objectid      bigint    NOT NULL,
    name          character varying(250),
    typename      character varying(50),
    level         character varying(250),
    oktmo         character varying(11),
    objectguid    character varying(36),
    isactive      boolean,
    parentobjid   bigint,
    apart_number  character varying(50),
    aparttype     integer,
    housenum      character varying(50),
    addnum1       character varying(50),
    addnum2       character varying(50),
    housetype     integer,
    addtype1      integer,
    addtype2      integer,
    steads_number character varying(250)
)
    TABLESPACE pg_default;

ALTER TABLE public.fias_address
    OWNER to fiz;

-- таблица строится по AS_ADDR_OBJ, AS_ADM_HIERARCHY, AS_APARTMENTS,AS_HOUSES, AS_MUN_HIERARCHY,AS_STEADS

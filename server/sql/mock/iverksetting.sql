UPDATE PEN.T_VEDTAK v
SET K_VEDTAK_S          = 'IVERKS',
    v.DATO_TILVERKSETT  = CURRENT_DATE,
    v.DATO_IVERKSATT    = CURRENT_DATE,
    v.DATO_SENDT_SAMORD = CURRENT_DATE,
    v.DATO_SAMORDNET    = CURRENT_DATE,
    v.ENDRET_AV         = 'TESTDATA',
    V.DATO_ENDRET       = CURRENT_TIMESTAMP
WHERE v.vedtak_id = {VEDTAK};

update pen.t_vedtak v
set v.dato_lopende_fom = v.dato_virk_fom,
    ENDRET_AV          = 'TESTDATA',
    DATO_ENDRET        = CURRENT_TIMESTAMP
where not exists(select 1
                 from pen.t_vedtak v2
                 where v2.sak_id = v.sak_id
                   and v2.vedtak_id > v.vedtak_id
                   and v2.dato_iverksatt is not null
                   and v2.dato_virk_fom <= v.dato_virk_fom)
  AND v.SAK_ID = {SAK}
  ;

update pen.t_vedtak v
set v.dato_lopende_tom = (select min(v2.dato_lopende_fom) - 1
                          from pen.t_vedtak v2
                          where v2.sak_id = v.sak_id
                            and v2.vedtak_id > v.vedtak_id
                            and v2.dato_lopende_fom is not null),
    ENDRET_AV          = 'TESTDATA',
    DATO_ENDRET        = CURRENT_TIMESTAMP
where v.dato_lopende_fom is not null
  AND v.SAK_ID = {SAK};

INSERT INTO PEN.T_OS_KVITTERING (VEDTAK_ID, MELDING_KODE, BESK_MELDING, ALVORLIGHETSGRAD, DATO_OPPRETTET, OPPRETTET_AV,
                                 DATO_ENDRET, ENDRET_AV, VERSJON, OS_TRANSAKSJON_ID)
VALUES ({VEDTAK}, null, null,
       '00', CURRENT_TIMESTAMP,
       'TESTDATA', CURRENT_TIMESTAMP,
       'TESTDATA', 0, null
);

UPDATE PEN.T_SAK s
set K_SAK_S   = 'LOPENDE',
    ENDRET_AV = 'TESTDATA',
    DATO_ENDRET = CURRENT_TIMESTAMP
WHERE SAK_ID = {SAK}
    AND NOT EXISTS (
    SELECT 1
    FROM PEN.T_KRAVHODE h
    WHERE h.SAK_ID = s.SAK_ID
    AND h.K_KRAV_S not in ('FERDIG','AVBRUTT')
    );
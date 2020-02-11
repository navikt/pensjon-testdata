SELECT DISTINCT
        'INSERT INTO T_PERSON (PERSON_ID,FNR_FK,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,VERSJON) VALUES (''' ||
        PERSON_ID || ''',''' || FNR_FK || ''',''' || DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' ||
        DATO_ENDRET || ''',''' || ENDRET_AV || ''',''' || VERSJON || ''');'
FROM T_PERSON b
where fnr_fk = '{fnr}'#

SELECT DISTINCT
        'INSERT INTO T_PERSON (PERSON_ID,FNR_FK,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,VERSJON) VALUES (''' ||
        PERSON_ID || ''',''' || FNR_FK || ''',''' || DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' ||
        DATO_ENDRET || ''',''' || ENDRET_AV || ''',''' || VERSJON || ''');'
FROM T_PERSON b
where EXISTS(SELECT 1
             FROM T_OMSORG o
             where o.PERSON_ID_OMSORG = b.person_id
               AND o.K_OMSORG_S = 'G'
               AND EXISTS(SELECT 1 FROM T_PERSON p where p.person_id = o.PERSON_ID AND p.fnr_fk = '{fnr}'))#

SELECT DISTINCT
        'INSERT INTO T_FIL_INFO (FIL_INFO_ID,FIL_NAVN,DATO_MOTTATT,DATO_SENDT,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,LOPENUMMER,K_FIL_T,K_FIL_S,K_KILDE_T,KONTEKST,ANT_REC) VALUES (''' ||
        f.FIL_INFO_ID || ''',''' || f.FIL_NAVN || ''',''' || f.DATO_MOTTATT || ''',''' || f.DATO_SENDT || ''',''' ||
        f.VERSJON || ''',''' || f.DATO_OPPRETTET || ''',''' || f.OPPRETTET_AV || ''',''' || f.DATO_ENDRET || ''',''' ||
        f.ENDRET_AV || ''',''' || f.LOPENUMMER || ''',''' || f.K_FIL_T || ''',''' || f.K_FIL_S || ''',''' ||
        f.K_KILDE_T || ''',''' || f.KONTEKST || ''',''' || f.ANT_REC || ''');'
FROM T_FIL_INFO f
         JOIN T_PERSON p on p.FNR_FK = '{fnr}'
         LEFT OUTER JOIN T_NY_OPPTJN_GRL no on no.PERSON_ID = p.PERSON_ID and f.FIL_INFO_ID = no.UFORE_FIL_INFO_ID
         LEFT OUTER JOIN T_NY_OPPTJN_GRL_BE nob on nob.PERSON_ID = p.PERSON_ID AND f.FIL_INFO_ID = no.UFORE_FIL_INFO_ID
         LEFT OUTER JOIN T_DAGPENGER d on d.PERSON_ID = p.PERSON_ID AND d.FIL_INFO_ID = f.FIL_INFO_ID AND d.K_DAGPENGER_S = 'G'
         LEFT OUTER JOIN T_ENDR_OPPTJN_GRL_BE eogb on eogb.PERSON_ID = p.PERSON_ID AND eogb.FIL_INFO_ID = f.FIL_INFO_ID
         LEFT OUTER JOIN T_F_TJEN_TOT ft on ft.PERSON_ID = p.PERSON_ID AND f.FIL_INFO_ID = ft.FIL_INFO_ID AND ft.K_F_TJEN_TOT_S = 'G'
         LEFT OUTER JOIN T_INNTEKT i on p.PERSON_ID = i.PERSON_ID AND i.FIL_INFO_ID = f.FIL_INFO_ID AND i.K_INNTEKT_STATUS = 'G'
         LEFT OUTER JOIN T_OPPTJN o on o.PERSON_ID_OPPTJN = p.PERSON_ID AND o.FIL_ID_FK = f.FIL_INFO_ID AND o.K_OPPTJN_STATUS = 'G'
WHERE no.NY_OPPTJN_GRL_ID is not null
   OR nob.NY_OPPTJN_GRL_BE_ID is not null
   or d.DAGPENGER_ID is not null
   or eogb.ENDR_OPPTJN_GRL_BE_ID is not null
   or ft.F_TJEN_TOT_ID is not null
   or i.FIL_INFO_ID is not null
   or o.FIL_ID_FK is not null#

SELECT DISTINCT
        'INSERT INTO T_INNTEKT (INNTEKT_ID,K_INNTEKT_T,PERSON_ID,K_KILDE_T,K_INNTEKT_STATUS,K_PI_MERKE_T,FIL_ID_FK,INNTEKT_AR,BELOP,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,KONV_FJERNING_KODE,PI_RAPPDATO,RAPPART,FIL_INFO_ID,RAPPORTERT_FNR,KOMMUNE_NR) VALUES (''' ||
        INNTEKT_ID || ''',''' || K_INNTEKT_T || ''',''' || PERSON_ID || ''',''' || K_KILDE_T || ''',''' ||
        K_INNTEKT_STATUS || ''',''' || K_PI_MERKE_T || ''',''' || FIL_ID_FK || ''',''' || INNTEKT_AR || ''',''' ||
        BELOP || ''',''' || VERSJON || ''',''' || DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' || DATO_ENDRET ||
        ''',''' || ENDRET_AV || ''',''' || KONV_FJERNING_KODE || ''',''' || PI_RAPPDATO || ''',''' || RAPPART ||
        ''',''' || FIL_INFO_ID || ''',''' || RAPPORTERT_FNR || ''',''' || KOMMUNE_NR || ''');'
FROM T_INNTEKT b
where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)
AND b.K_INNTEKT_STATUS = 'G'#

SELECT DISTINCT
        'INSERT INTO T_OMSORG (OMSORG_ID,UGYLDIG_OMSORG_ID,AR,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,K_OMSORG_T,K_OMSORG_S,K_KILDE_T,PERSON_ID_OMSORG,PERSON_ID) VALUES (''' ||
        OMSORG_ID || ''',''' || UGYLDIG_OMSORG_ID || ''',''' || AR || ''',''' || VERSJON || ''',''' || DATO_OPPRETTET ||
        ''',''' || OPPRETTET_AV || ''',''' || DATO_ENDRET || ''',''' || ENDRET_AV || ''',''' || K_OMSORG_T || ''',''' ||
        K_OMSORG_S || ''',''' || K_KILDE_T || ''',''' || PERSON_ID_OMSORG || ''',''' || PERSON_ID || ''');'
FROM T_OMSORG b
where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)
AND b.K_OMSORG_S ='G'#

SELECT DISTINCT
        'INSERT INTO T_FPP_AFP (FPP_AFP_ID,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,PERSON_ID,K_FPP_AFP_S,AFP_FPP,VIRK_FOM,VIRK_TOM,AFP_PENSJONSGRAD,AFP_TYPE) VALUES (''' ||
        FPP_AFP_ID || ''',''' || VERSJON || ''',''' || DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' ||
        DATO_ENDRET || ''',''' || ENDRET_AV || ''',''' || PERSON_ID || ''',''' || K_FPP_AFP_S || ''',''' || AFP_FPP ||
        ''',''' || VIRK_FOM || ''',''' || VIRK_TOM || ''',''' || AFP_PENSJONSGRAD || ''',''' || AFP_TYPE || ''');'
FROM T_FPP_AFP b
where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)
AND b.K_FPP_AFP_S = 'G'#

SELECT DISTINCT
        'INSERT INTO T_FPP_UFORE (FPP_UFORE_ID,K_FPP_UFORE_S,PERSON_ID,UFOREGRAD,DATO_UFORE_FOM,DATO_UFORE_TOM,FPP,DATO_UFG_FOM,DATO_UFG_TOM,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,PAA,UFORE_TYPE) VALUES (''' ||
        FPP_UFORE_ID || ''',''' || K_FPP_UFORE_S || ''',''' || PERSON_ID || ''',''' || UFOREGRAD || ''',''' ||
        DATO_UFORE_FOM || ''',''' || DATO_UFORE_TOM || ''',''' || FPP || ''',''' || DATO_UFG_FOM || ''',''' ||
        DATO_UFG_TOM || ''',''' || VERSJON || ''',''' || DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' ||
        DATO_ENDRET || ''',''' || ENDRET_AV || ''',''' || PAA || ''',''' || UFORE_TYPE || ''');'
FROM T_FPP_UFORE b
where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)
AND b.K_FPP_UFORE_S = 'G'#

SELECT DISTINCT
        'INSERT INTO T_F_TJEN_TOT (F_TJEN_TOT_ID,UGYLDIG_FT_TOT_ID,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,DATO_DIMITTERING,DATO_TJENESTESTART,K_RAPPORT_T,K_F_TJEN_TOT_S,PERSON_ID,K_KILDE_T,FIL_INFO_ID) VALUES (''' ||
        F_TJEN_TOT_ID || ''',''' || UGYLDIG_FT_TOT_ID || ''',''' || VERSJON || ''',''' || DATO_OPPRETTET || ''',''' ||
        OPPRETTET_AV || ''',''' || DATO_ENDRET || ''',''' || ENDRET_AV || ''',''' || DATO_DIMITTERING || ''',''' ||
        DATO_TJENESTESTART || ''',''' || K_RAPPORT_T || ''',''' || K_F_TJEN_TOT_S || ''',''' || PERSON_ID || ''',''' ||
        K_KILDE_T || ''',''' || FIL_INFO_ID || ''');'
FROM T_F_TJEN_TOT b
where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)
AND b.K_F_TJEN_TOT_S ='G'#

SELECT DISTINCT
        'INSERT INTO T_F_TJEN_PERIODE (F_TJEN_PERIODE_ID,DATO_FOM,DATO_TOM,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,K_PERIODE_T,K_TJENESTE_T,F_TJEN_TOT_ID) VALUES (''' ||
        F_TJEN_PERIODE_ID || ''',''' || DATO_FOM || ''',''' || DATO_TOM || ''',''' || VERSJON || ''',''' ||
        DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' || DATO_ENDRET || ''',''' || ENDRET_AV || ''',''' ||
        K_PERIODE_T || ''',''' || K_TJENESTE_T || ''',''' || F_TJEN_TOT_ID || ''');'
FROM T_F_TJEN_PERIODE f
WHERE EXISTS(SELECT 1
             FROM T_F_TJEN_TOT ft
             where ft.F_TJEN_TOT_ID = f.F_TJEN_TOT_ID
               AND ft.K_F_TJEN_TOT_S = 'G'
               AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = ft.PERSON_ID))#

SELECT DISTINCT
        'INSERT INTO T_INNTEKT_REL (INNTEKT_REL_ID,INNTEKT_ID_MESTER,INNTEKT_ID_SLAVE,K_INNTEKT_REL_T,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV) VALUES (''' ||
        rel.INNTEKT_REL_ID || ''',''' || rel.INNTEKT_ID_MESTER || ''',''' || rel.INNTEKT_ID_SLAVE || ''',''' ||
        rel.K_INNTEKT_REL_T || ''',''' || rel.VERSJON || ''',''' || rel.DATO_OPPRETTET || ''',''' || rel.OPPRETTET_AV ||
        ''',''' || rel.DATO_ENDRET || ''',''' || rel.ENDRET_AV || ''');'
FROM T_PERSON p
         JOIN T_INNTEKT i on i.PERSON_ID = p.person_id AND i.K_INNTEKT_STATUS = 'G'
         JOIN T_INNTEKT_REL rel on rel.INNTEKT_ID_SLAVE = i.INNTEKT_ID or rel.INNTEKT_ID_MESTER = i.INNTEKT_ID
WHERE p.fnr_fk = '{fnr}'#

SELECT DISTINCT
        'INSERT INTO T_DAGPENGER (DAGPENGER_ID,UGYLDIG_DAGPENGER_ID,FERIETILLEGG,BARNETILLEGG,DAGPENGER,UAVKORTET_DP_GRLAG,AR,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,FIL_INFO_ID,K_KILDE_T,PERSON_ID,K_DAGPENGER_S,K_DAGPENGER_T,K_RAPPORT_T) VALUES (''' ||
        DAGPENGER_ID || ''',''' || UGYLDIG_DAGPENGER_ID || ''',''' || FERIETILLEGG || ''',''' || BARNETILLEGG ||
        ''',''' || DAGPENGER || ''',''' || UAVKORTET_DP_GRLAG || ''',''' || AR || ''',''' || VERSJON || ''',''' ||
        DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' || DATO_ENDRET || ''',''' || ENDRET_AV || ''',''' ||
        FIL_INFO_ID || ''',''' || K_KILDE_T || ''',''' || PERSON_ID || ''',''' || K_DAGPENGER_S || ''',''' ||
        K_DAGPENGER_T || ''',''' || K_RAPPORT_T || ''');'
FROM T_DAGPENGER b
where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)
AND b.K_DAGPENGER_S = 'G'#

SELECT DISTINCT
        'INSERT INTO T_RP_KOMP (RP_KOMP_ID,BELOP,VEDTAK_ID_FK,DATO_FOM,DATO_TOM,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,K_RP_KOMP_S,K_RP_KOMP_T,PERSON_ID,K_RESTP_BEH_ARSAK) VALUES (''' ||
        RP_KOMP_ID || ''',''' || BELOP || ''',''' || VEDTAK_ID_FK || ''',''' || DATO_FOM || ''',''' || DATO_TOM ||
        ''',''' || VERSJON || ''',''' || DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' || DATO_ENDRET ||
        ''',''' || ENDRET_AV || ''',''' || K_RP_KOMP_S || ''',''' || K_RP_KOMP_T || ''',''' || PERSON_ID || ''',''' ||
        K_RESTP_BEH_ARSAK || ''');'
FROM T_RP_KOMP b
where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)
AND b.K_RP_KOMP_S = 'G'#

SELECT DISTINCT
        'INSERT INTO T_OPPTJN (OPPTJN_ID,K_OPPTJN_T,PERSON_ID_OPPTJN,PERSON_ID_OMSORG,K_KILDE_T,K_OPPTJN_STATUS,INNTEKT_ID,FIL_ID_FK,OPPTJN_AR,PGI_ANVENDT,POENG,MAX_UFOREGRAD,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,KONV_FJERNING_KODE,PI_RAPPDATO,OMSORG_ID) VALUES (''' ||
        OPPTJN_ID || ''',''' || K_OPPTJN_T || ''',''' || PERSON_ID_OPPTJN || ''',''' || PERSON_ID_OMSORG || ''',''' ||
        K_KILDE_T || ''',''' || K_OPPTJN_STATUS || ''',''' || INNTEKT_ID || ''',''' || FIL_ID_FK || ''',''' ||
        OPPTJN_AR || ''',''' || PGI_ANVENDT || ''',''' || POENG || ''',''' || MAX_UFOREGRAD || ''',''' || VERSJON ||
        ''',''' || DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' || DATO_ENDRET || ''',''' || ENDRET_AV ||
        ''',''' || KONV_FJERNING_KODE || ''',''' || PI_RAPPDATO || ''',''' || OMSORG_ID || ''');'
FROM T_OPPTJN b
where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID_OPPTJN)
AND b.K_OPPTJN_STATUS = 'G'#

SELECT DISTINCT
        'INSERT INTO T_OPPTJN_REL (OPPTJN_REL_ID,OPPTJN_ID_MESTER,OPPTJN_ID_SLAVE,K_OPPTJN_REL_T,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV) VALUES (''' ||
        rel.OPPTJN_REL_ID || ''',''' || rel.OPPTJN_ID_MESTER || ''',''' || rel.OPPTJN_ID_SLAVE || ''',''' ||
        rel.K_OPPTJN_REL_T || ''',''' || rel.VERSJON || ''',''' || rel.DATO_OPPRETTET || ''',''' || rel.OPPRETTET_AV ||
        ''',''' || rel.DATO_ENDRET || ''',''' || rel.ENDRET_AV || ''');'
FROM T_PERSON p
         JOIN T_OPPTJN o on o.PERSON_ID_OPPTJN = p.PERSON_ID AND o.K_OPPTJN_STATUS = 'G'
         JOIN T_OPPTJN_REL rel on rel.OPPTJN_ID_SLAVE = o.OPPTJN_ID or rel.OPPTJN_ID_MESTER = o.OPPTJN_ID
WHERE p.FNR_FK = '{fnr}'#

SELECT DISTINCT
        'INSERT INTO T_OPPTJN_RP_KOMP (OPPTJN_RP_KOMP_ID,OPPTJN_ID,RP_KOMP_ID,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV) VALUES (''' ||
        OPPTJN_RP_KOMP_ID || ''',''' || OPPTJN_ID || ''',''' || RP_KOMP_ID || ''',''' || VERSJON || ''',''' ||
        DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' || DATO_ENDRET || ''',''' || ENDRET_AV || ''');'
FROM T_OPPTJN_RP_KOMP r
where EXISTS(SELECT 1
             FROM T_OPPTJN o
             where r.OPPTJN_ID = o.OPPTJN_ID
               AND o.K_OPPTJN_STATUS = 'G'
               AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = o.PERSON_ID_OPPTJN))#

SELECT DISTINCT
        'INSERT INTO T_LONN_VEKST_REG (DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,LONNSVEKST_REG_ID,REGULERINGSBELOP,REGULERING_DATO,VERSJON) VALUES (''' ||
        DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' || DATO_ENDRET || ''',''' || ENDRET_AV || ''',''' ||
        LONNSVEKST_REG_ID || ''',''' || REGULERINGSBELOP || ''',''' || REGULERING_DATO || ''',''' || VERSJON || ''');'
FROM T_LONN_VEKST_REG l
where EXISTS(SELECT 1
             FROM T_BEHOLDNING b
             where b.LONNSVEKST_REG_ID = l.LONNSVEKST_REG_ID
               AND b.K_BEHOLDNING_S = 'G'
               AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID))#

SELECT DISTINCT
        'INSERT INTO T_UFORE_OPPTJ (UFORE_OPPTJ_ID,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,BELOP,AR,PRORATA_BEREGNET_UP,POENGTALL,UFG,ANTATT_INNTEKT,ANTATT_INNT_PRORATA,ANDEL_PRORATA,PNGAR_TELLER_PRORATA,PNGAR_NEVNER_PRORATA,ANT_FREMT_AR_PRORATA,PAA,YUG,ANTATT_INNTEKT_YRKE,VEIET_GRUNNBELOP,YRKESSKADE,UFORETRYGD,UFOREAR,KONVERTERT_UFT) VALUES (''' ||
        UFORE_OPPTJ_ID || ''',''' || VERSJON || ''',''' || DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' ||
        DATO_ENDRET || ''',''' || ENDRET_AV || ''',''' || BELOP || ''',''' || AR || ''',''' || PRORATA_BEREGNET_UP ||
        ''',''' || POENGTALL || ''',''' || UFG || ''',''' || ANTATT_INNTEKT || ''',''' || ANTATT_INNT_PRORATA ||
        ''',''' || ANDEL_PRORATA || ''',''' || PNGAR_TELLER_PRORATA || ''',''' || PNGAR_NEVNER_PRORATA || ''',''' ||
        ANT_FREMT_AR_PRORATA || ''',''' || PAA || ''',''' || YUG || ''',''' || ANTATT_INNTEKT_YRKE || ''',''' ||
        VEIET_GRUNNBELOP || ''',''' || YRKESSKADE || ''',''' || UFORETRYGD || ''',''' || UFOREAR || ''',''' ||
        KONVERTERT_UFT || ''');'
FROM T_UFORE_OPPTJ u
where EXISTS(SELECT 1
             FROM T_BEHOLDNING b
             where b.UFORE_OPPTJ_ID = u.UFORE_OPPTJ_ID
               AND b.K_BEHOLDNING_S = 'G'
               AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID))#

SELECT DISTINCT
        'INSERT INTO T_INNTEKT_OPPTJ (VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,INNTEKT_OPPTJ_ID,INNTEKT_ID,AR,BELOP) VALUES (''' ||
        VERSJON || ''',''' || DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' || DATO_ENDRET || ''',''' ||
        ENDRET_AV || ''',''' || INNTEKT_OPPTJ_ID || ''',''' || INNTEKT_ID || ''',''' || AR || ''',''' || BELOP || ''');'
FROM T_INNTEKT_OPPTJ i
where EXISTS(SELECT 1
             FROM T_BEHOLDNING b
             where b.INNTEKT_OPPTJ_ID = i.INNTEKT_OPPTJ_ID
               AND b.K_BEHOLDNING_S = 'G'
               AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID))#

SELECT DISTINCT
        'INSERT INTO T_DAGPENGER_OPPTJ (DAGPENGER_OPPTJ_ID,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,BELOP_ORDINAR,AR,BELOP_FISKERE) VALUES (''' ||
        DAGPENGER_OPPTJ_ID || ''',''' || VERSJON || ''',''' || DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' ||
        DATO_ENDRET || ''',''' || ENDRET_AV || ''',''' || BELOP_ORDINAR || ''',''' || AR || ''',''' || BELOP_FISKERE ||
        ''');'
FROM T_DAGPENGER_OPPTJ d
where EXISTS(SELECT 1
             FROM T_BEHOLDNING b
             where b.DAGPENGER_OPPTJ_ID = d.DAGPENGER_OPPTJ_ID
               AND b.K_BEHOLDNING_S = 'G'
               AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID))#

SELECT DISTINCT
        'INSERT INTO T_F_TJEN_OPPTJ (F_TJEN_OPPTJ_ID,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,AR,BELOP) VALUES (''' ||
        F_TJEN_OPPTJ_ID || ''',''' || VERSJON || ''',''' || DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' ||
        DATO_ENDRET || ''',''' || ENDRET_AV || ''',''' || AR || ''',''' || BELOP || ''');'
FROM T_F_TJEN_OPPTJ f
where EXISTS(SELECT 1
             FROM T_BEHOLDNING b
             where b.F_TJEN_OPPTJ_ID = f.F_TJEN_OPPTJ_ID
               AND b.K_BEHOLDNING_S = 'G'
               AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID))#

SELECT DISTINCT
        'INSERT INTO T_FT_PER_FT_OPPTJ (FT_PER_FT_OPPTJ_ID,F_TJEN_PERIODE_ID,F_TJEN_OPPTJ_ID,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV) VALUES (''' ||
        FT_PER_FT_OPPTJ_ID || ''',''' || F_TJEN_PERIODE_ID || ''',''' || F_TJEN_OPPTJ_ID || ''',''' || VERSJON ||
        ''',''' || DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' || DATO_ENDRET || ''',''' || ENDRET_AV || ''');'
FROM T_FT_PER_FT_OPPTJ f
WHERE EXISTS(SELECT 1
             FROM T_F_TJEN_OPPTJ o
             where o.F_TJEN_OPPTJ_ID = f.F_TJEN_OPPTJ_ID
               AND EXISTS(SELECT 1
                          FROM T_BEHOLDNING b
                          where b.F_TJEN_OPPTJ_ID = f.F_TJEN_OPPTJ_ID
                            AND b.K_BEHOLDNING_S = 'G'
                            AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)))#

SELECT DISTINCT
        'INSERT INTO T_DP_DP_OPPTJ (DAGPENGER_ID,DAGPENGER_OPPTJ_ID,DP_DP_OPPTJ,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV) VALUES (''' ||
        DAGPENGER_ID || ''',''' || DAGPENGER_OPPTJ_ID || ''',''' || DP_DP_OPPTJ || ''',''' || VERSJON || ''',''' ||
        DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' || DATO_ENDRET || ''',''' || ENDRET_AV || ''');'
FROM T_DP_DP_OPPTJ dp
where EXISTS(SELECT 1
             FROM T_DAGPENGER_OPPTJ o
             WHERE o.DAGPENGER_OPPTJ_ID = dp.DAGPENGER_OPPTJ_ID
               AND EXISTS(SELECT 1
                          FROM T_BEHOLDNING b
                          where b.DAGPENGER_OPPTJ_ID = dp.DAGPENGER_OPPTJ_ID
                            AND b.K_BEHOLDNING_S = 'G'
                            AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)))#

SELECT DISTINCT
        'INSERT INTO T_ENDR_OPPTJN_GRL (ENDR_OPPTJN_GRL_ID,OPPTJN_AR,NY_GRL_ID,UGYLDIG_GRL_ID,STATUS,K_GRUNNLAG_T,PERSON_ID,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV) VALUES (''' ||
        ENDR_OPPTJN_GRL_ID || ''',''' || OPPTJN_AR || ''',''' || NY_GRL_ID || ''',''' || UGYLDIG_GRL_ID || ''',''' ||
        STATUS || ''',''' || K_GRUNNLAG_T || ''',''' || PERSON_ID || ''',''' || VERSJON || ''',''' || DATO_OPPRETTET ||
        ''',''' || OPPRETTET_AV || ''',''' || DATO_ENDRET || ''',''' || ENDRET_AV || ''');'
FROM T_ENDR_OPPTJN_GRL b
where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)#

SELECT DISTINCT
        'INSERT INTO T_ENDR_OPPTJN_GRL_BE (OPPTJN_AR,NY_GRL_ID,UGYLDIG_GRL_ID,STATUS,ENDR_OPPTJN_GRL_BE_ID,K_GRUNNLAG_T,FIL_INFO_ID,PERSON_ID,DATO_OPPRETTET,VERSJON,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV) VALUES (''' ||
        OPPTJN_AR || ''',''' || NY_GRL_ID || ''',''' || UGYLDIG_GRL_ID || ''',''' || STATUS || ''',''' ||
        ENDR_OPPTJN_GRL_BE_ID || ''',''' || K_GRUNNLAG_T || ''',''' || FIL_INFO_ID || ''',''' || PERSON_ID || ''',''' ||
        DATO_OPPRETTET || ''',''' || VERSJON || ''',''' || OPPRETTET_AV || ''',''' || DATO_ENDRET || ''',''' ||
        ENDRET_AV || ''');'
FROM T_ENDR_OPPTJN_GRL_BE b
where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)#

SELECT DISTINCT
        'INSERT INTO T_OMSORG_OPPTJ (OMSORG_OPPTJ_ID,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,AR,BELOP,OMS_OPPTJ_INNSKUDD) VALUES (''' ||
        OMSORG_OPPTJ_ID || ''',''' || VERSJON || ''',''' || DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' ||
        DATO_ENDRET || ''',''' || ENDRET_AV || ''',''' || AR || ''',''' || BELOP || ''',''' || OMS_OPPTJ_INNSKUDD ||
        ''');'
FROM T_OMSORG_OPPTJ o
WHERE EXISTS(SELECT 1
             FROM T_BEHOLDNING b
             where b.OMSORG_OPPTJ_ID = o.OMSORG_OPPTJ_ID
               AND b.K_BEHOLDNING_S = 'G'
               AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID))#

SELECT DISTINCT
        'INSERT INTO T_OMS_OMS_OPPTJ (OMS_OMS_OPPTJ,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,OMSORG_ID,OMSORG_OPPTJ_ID) VALUES (''' ||
        OMS_OMS_OPPTJ || ''',''' || VERSJON || ''',''' || DATO_OPPRETTET || ''',''' || OPPRETTET_AV || ''',''' ||
        DATO_ENDRET || ''',''' || ENDRET_AV || ''',''' || OMSORG_ID || ''',''' || OMSORG_OPPTJ_ID || ''');'
FROM T_OMS_OMS_OPPTJ oms
WHERE EXISTS(SELECT 1
             FROM T_OMSORG_OPPTJ o
             where o.OMSORG_OPPTJ_ID = oms.OMSORG_OPPTJ_ID
               AND EXISTS(SELECT 1
                          FROM T_BEHOLDNING b
                          where b.OMSORG_OPPTJ_ID = o.OMSORG_OPPTJ_ID
                            AND b.K_BEHOLDNING_S = 'G'
                            AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)))#

SELECT
        'INSERT INTO T_BEHOLDNING (BEHOLDNING_ID,FORRIGE_BEH_ID,BELOP,VEDTAK_ID_FK,DATO_FOM,DATO_TOM,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,K_BEHOLDNING_T,K_BEHOLDNING_S,K_RESTP_BEH_ARSAK,PERSON_ID,BEH_GRLAG,BEH_GRLAG_AVKORTET,BEH_INNSKUDD,BEH_INNSKUDD_U_OMS,F_TJEN_OPPTJ_ID,UFORE_OPPTJ_ID,OMSORG_OPPTJ_ID,INNTEKT_OPPTJ_ID,DAGPENGER_OPPTJ_ID,LONNSVEKST_REG_ID) VALUES (''' ||
        BEHOLDNING_ID || ''',''' || FORRIGE_BEH_ID || ''',''' || BELOP || ''',''' || VEDTAK_ID_FK || ''',''' ||
        DATO_FOM || ''',''' || DATO_TOM || ''',''' || VERSJON || ''',''' || DATO_OPPRETTET || ''',''' || OPPRETTET_AV ||
        ''',''' || DATO_ENDRET || ''',''' || ENDRET_AV || ''',''' || K_BEHOLDNING_T || ''',''' || K_BEHOLDNING_S ||
        ''',''' || K_RESTP_BEH_ARSAK || ''',''' || PERSON_ID || ''',''' || BEH_GRLAG || ''',''' || BEH_GRLAG_AVKORTET ||
        ''',''' || BEH_INNSKUDD || ''',''' || BEH_INNSKUDD_U_OMS || ''',''' || F_TJEN_OPPTJ_ID || ''',''' ||
        UFORE_OPPTJ_ID || ''',''' || OMSORG_OPPTJ_ID || ''',''' || INNTEKT_OPPTJ_ID || ''',''' || DAGPENGER_OPPTJ_ID ||
        ''',''' || LONNSVEKST_REG_ID || ''');'
FROM (
         SELECT  *
         FROM T_BEHOLDNING b
         where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)
           AND b.K_BEHOLDNING_S = 'G'
     )
ORDER BY BEHOLDNING_ID, K_BEHOLDNING_T#


SELECT DISTINCT
        'INSERT INTO T_NY_OPPTJN_GRL (NY_OPPTJN_GRL_ID,OPPTJN_AR,INNTEKT_ID,DAGPENGER_ID,DAGPENGER_GFF_ID,PERSON_ID,VERSJON,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,STATUS,FNR,UFORE_FIL_INFO_ID,F_TJEN_TOT_ID,OMSORG_OBU_ID,OMSORG_OBOH_ID,OMSORG_OSFE_ID) VALUES (''' ||
        NY_OPPTJN_GRL_ID || ''',''' || OPPTJN_AR || ''',''' || INNTEKT_ID || ''',''' || DAGPENGER_ID || ''',''' ||
        DAGPENGER_GFF_ID || ''',''' || PERSON_ID || ''',''' || VERSJON || ''',''' || DATO_OPPRETTET || ''',''' ||
        OPPRETTET_AV || ''',''' || DATO_ENDRET || ''',''' || ENDRET_AV || ''',''' || STATUS || ''',''' || FNR ||
        ''',''' || UFORE_FIL_INFO_ID || ''',''' || F_TJEN_TOT_ID || ''',''' || OMSORG_OBU_ID || ''',''' ||
        OMSORG_OBOH_ID || ''',''' || OMSORG_OSFE_ID || ''');'
FROM T_NY_OPPTJN_GRL b
where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)#

SELECT DISTINCT
        'INSERT INTO T_NY_OPPTJN_GRL_BE (NY_OPPTJN_GRL_BE_ID,OPPTJN_AR,STATUS,DATO_OPPRETTET,OPPRETTET_AV,DATO_ENDRET,ENDRET_AV,PERSON_ID,INNTEKT_ID,DAGPENGER_ID,DAGPENGER_GFF_ID,FNR,UFORE_FIL_INFO_ID,F_TJEN_TOT_ID,OMSORG_OBU_ID,OMSORG_OBOH_ID,OMSORG_OSFE_ID) VALUES (''' ||
        NY_OPPTJN_GRL_BE_ID || ''',''' || OPPTJN_AR || ''',''' || STATUS || ''',''' || DATO_OPPRETTET || ''',''' ||
        OPPRETTET_AV || ''',''' || DATO_ENDRET || ''',''' || ENDRET_AV || ''',''' || PERSON_ID || ''',''' ||
        INNTEKT_ID || ''',''' || DAGPENGER_ID || ''',''' || DAGPENGER_GFF_ID || ''',''' || FNR || ''',''' ||
        UFORE_FIL_INFO_ID || ''',''' || F_TJEN_TOT_ID || ''',''' || OMSORG_OBU_ID || ''',''' || OMSORG_OBOH_ID ||
        ''',''' || OMSORG_OSFE_ID || ''');'
FROM T_NY_OPPTJN_GRL_BE b
where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)#
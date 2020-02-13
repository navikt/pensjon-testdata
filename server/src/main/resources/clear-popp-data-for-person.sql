DELETE FROM T_NY_OPPTJN_GRL b where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)#

DELETE FROM T_NY_OPPTJN_GRL_BE b where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)#

DELETE FROM T_RP_KOMP b where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)#

DELETE FROM T_FPP_AFP b where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)#

DELETE FROM T_FPP_UFORE b where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)#

DELETE FROM T_ENDR_OPPTJN_GRL b
where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)#

DELETE FROM T_ENDR_OPPTJN_GRL_BE b
where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)#

DELETE FROM T_BEHOLDNING b where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)#

DELETE FROM T_LONN_VEKST_REG l
where EXISTS(SELECT 1
             FROM T_BEHOLDNING b
             where b.LONNSVEKST_REG_ID = l.LONNSVEKST_REG_ID
               AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID))#

DELETE FROM T_UFORE_OPPTJ u
where EXISTS(SELECT 1
             FROM T_BEHOLDNING b
             where b.UFORE_OPPTJ_ID = u.UFORE_OPPTJ_ID
               AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID))#

DELETE FROM T_INNTEKT_OPPTJ i
where EXISTS(SELECT 1
             FROM T_BEHOLDNING b
             where b.INNTEKT_OPPTJ_ID = i.INNTEKT_OPPTJ_ID
               AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID))#

DELETE FROM T_INNTEKT_OPPTJ io
where EXISTS(SELECT 1
             FROM T_INNTEKT i
             where i.INNTEKT_ID = io.INNTEKT_ID
               AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = i.PERSON_ID))#

DELETE FROM T_DAGPENGER_OPPTJ d
where EXISTS(SELECT 1
             FROM T_BEHOLDNING b
             where b.DAGPENGER_OPPTJ_ID = d.DAGPENGER_OPPTJ_ID
               AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID))#

DELETE FROM T_F_TJEN_OPPTJ f
where EXISTS(SELECT 1
             FROM T_BEHOLDNING b
             where b.F_TJEN_OPPTJ_ID = f.F_TJEN_OPPTJ_ID
               AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID))#

DELETE FROM T_FT_PER_FT_OPPTJ f
WHERE EXISTS(SELECT 1
             FROM T_F_TJEN_OPPTJ o
             where o.F_TJEN_OPPTJ_ID = f.F_TJEN_OPPTJ_ID
               AND EXISTS(SELECT 1
                          FROM T_BEHOLDNING b
                          where b.F_TJEN_OPPTJ_ID = f.F_TJEN_OPPTJ_ID
                            AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)))#

DELETE FROM T_DP_DP_OPPTJ dp
where EXISTS(SELECT 1
             FROM T_DAGPENGER_OPPTJ o
             WHERE o.DAGPENGER_OPPTJ_ID = dp.DAGPENGER_OPPTJ_ID
               AND EXISTS(SELECT 1
                          FROM T_BEHOLDNING b
                          where b.DAGPENGER_OPPTJ_ID = dp.DAGPENGER_OPPTJ_ID
                            AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)))#

DELETE FROM T_OMSORG_OPPTJ o
WHERE EXISTS(SELECT 1
             FROM T_BEHOLDNING b
             where b.OMSORG_OPPTJ_ID = o.OMSORG_OPPTJ_ID
               AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID))#

DELETE FROM T_OMS_OMS_OPPTJ oms
WHERE EXISTS(SELECT 1
             FROM T_OMSORG_OPPTJ o
             where o.OMSORG_OPPTJ_ID = oms.OMSORG_OPPTJ_ID
               AND EXISTS(SELECT 1
                          FROM T_BEHOLDNING b
                          where b.OMSORG_OPPTJ_ID = o.OMSORG_OPPTJ_ID
                            AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)))#

DELETE FROM T_OPPTJN_RP_KOMP r
where EXISTS(SELECT 1
             FROM T_OPPTJN o
             where r.OPPTJN_ID = o.OPPTJN_ID
               AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = o.PERSON_ID_OPPTJN))#

DELETE FROM T_OPPTJN_REL rel
WHERE EXISTS (
              SELECT 1 FROM T_OPPTJN o
              WHERE rel.OPPTJN_ID_SLAVE = o.OPPTJN_ID or rel.OPPTJN_ID_MESTER = o.OPPTJN_ID
                  AND EXISTS (
                                                                 SELECT 1 FROM T_PERSON p
                                                                 WHERE p.PERSON_ID = o.PERSON_ID_OPPTJN
                                                                   AND p.FNR_FK = '{fnr}'
                                                             )
          )#

DELETE FROM T_OPPTJN b where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID_OPPTJN)#

DELETE FROM T_INNTEKT_REL rel
WHERE EXISTS (
              SELECT 1 FROM T_INNTEKT i
              WHERE rel.INNTEKT_ID_SLAVE = i.INNTEKT_ID or rel.INNTEKT_ID_MESTER = i.INNTEKT_ID
                  AND EXISTS (
                                                                   SELECT 1 FROM T_PERSON p
                                                                   WHERE p.PERSON_ID = i.PERSON_ID
                                                                     AND p.FNR_FK = '{fnr}'
                                                               )
          )#

DELETE FROM T_INNTEKT b where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)#

DELETE FROM T_OMSORG b where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)#

DELETE FROM T_F_TJEN_PERIODE f
WHERE EXISTS(SELECT 1
             FROM T_F_TJEN_TOT ft
             where ft.F_TJEN_TOT_ID = f.F_TJEN_TOT_ID
               AND EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = ft.PERSON_ID))#

DELETE FROM T_F_TJEN_TOT b where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)#


DELETE FROM T_DAGPENGER b where EXISTS(SELECT 1 FROM T_PERSON p where fnr_fk = '{fnr}' AND p.PERSON_ID = b.PERSON_ID)#
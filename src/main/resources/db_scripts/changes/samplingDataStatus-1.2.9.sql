--Sampling data status
insert into psffs_operational_code (id, edesc, fdesc, code_type, presentation_order, mod_by, mod_dt)
values (36, 'Entered', 'FR Entered', 'SAMPLING_DATA_STATUS', 1, 'mackinnonje', SYSDATE);
insert into psffs_operational_code (id, edesc, fdesc, code_type, presentation_order, mod_by, mod_dt)
values (37, 'Completed', 'FR Completed', 'SAMPLING_DATA_STATUS', 2, 'mackinnonje', SYSDATE);
insert into psffs_operational_code (id, edesc, fdesc, code_type, presentation_order, mod_by, mod_dt)
values (38, 'Marked for Archive', 'FR Marked for Archive', 'SAMPLING_DATA_STATUS', 3, 'mackinnonje', SYSDATE);
insert into psffs_operational_code (id, edesc, fdesc, code_type, presentation_order, mod_by, mod_dt)
values (39, 'Arhived', 'FR Arcived', 'SAMPLING_DATA_STATUS', 4, 'mackinnonje', SYSDATE);

--Add Status to Sampling Data table
ALTER TABLE PSFFS.SAMPLING_DATA ADD (sampling_data_sts_id NUMBER(8,0) DEFAULT 36 NOT NULL);
ALTER TABLE PSFFS.SAMPLING_DATA
  ADD CONSTRAINT FK_SMP_DATA_STS_ID
    FOREIGN KEY (sampling_data_sts_id)
      REFERENCES PSFFS.PSFFS_OPERATIONAL_CODE (id);
CREATE INDEX FKIND_SMP_DATA_STS_ID ON PSFFS.SAMPLING_DATA (sampling_data_sts_id);
ALTER INDEX PSFFS.FKIND_SMP_DATA_STS_ID REBUILD TABLESPACE PSFFS_INDEX;

--Fix sequence start value and default sampling settings
UPDATE "PSFFS"."SAMPLING_SETTING" SET CRT_YEAR = null, species_id = null where id = 1 OR id = 4;
DROP SEQUENCE "PSFFS"."CTAB_VESSEL_ID_SEQ";
CREATE SEQUENCE  "PSFFS"."CTAB_VESSEL_ID_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 15000 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL;
GRANT SELECT ON "PSFFS"."CTAB_VESSEL_ID_SEQ" to PSFFS_READ;
